/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;

/**
 * A HollowHashIndex is used for indexing non-primary-key data.  This type of index can map multiple keys to a single matching record, and/or
 * multiple records to a single key.
 * <p>
 * The field definitions in a hash key may be hierarchical (traverse multiple record types) via dot-notation.  For example,
 * the field definition <i>actors.element.actorId</i> may be used to traverse a child <b>LIST</b> or <b>SET</b> type record referenced by the field 
 * <i>actors</i>, each elements contained therein, and finally each actors <i>actorId</i> field. 
 * <p>
 */
public class HollowHashIndex {
    
    private final FixedLengthElementArray matchHashTable;
    final FixedLengthElementArray selectHashArray;

    private final HollowHashIndexField[] matchFields;
    private final int matchHashMask;
    private final int bitsPerMatchHashKey;
    private final int bitsPerMatchHashEntry;
    
    private final int[] bitsPerTraverserField;
    private final int[] offsetPerTraverserField;
    private final int bitsPerSelectTableSize;
    private final int bitsPerSelectTablePointer;
    
    final int bitsPerSelectHashEntry;
    
    /**
     * Define a {@link HollowHashIndex}. 
     * 
     * @param stateEngine The state engine to index
     * @param type The query starts with the specified type
     * @param selectField The query will select records at this field (specify "" to select the specified type).  
     * The selectField may span collection elements and/or map keys or values, which can result in multiple matches per record of the specified start type. 
     * @param matchFields The query will match on the specified match fields.  The match fields may span collection elements and/or map keys or values.
     */
    public HollowHashIndex(HollowReadStateEngine stateEngine, String type, String selectField, String... matchFields) {
        HollowHashIndexBuilder builder = new HollowHashIndexBuilder(stateEngine, type, selectField, matchFields);
        
        builder.buildIndex();
        
        this.matchHashTable = builder.getFinalMatchHashTable();
        this.selectHashArray = builder.getFinalSelectHashArray();
        this.matchFields = builder.getMatchFields();
        this.matchHashMask = (int)builder.getFinalMatchHashMask();
        this.bitsPerMatchHashKey = builder.getBitsPerMatchHashKey();
        this.bitsPerMatchHashEntry = builder.getFinalBitsPerMatchHashEntry();
        this.bitsPerTraverserField = builder.getBitsPerTraverserField();
        this.offsetPerTraverserField = builder.getOffsetPerTraverserField();
        this.bitsPerSelectTableSize = builder.getFinalBitsPerSelectTableSize();
        this.bitsPerSelectTablePointer = builder.getFinalBitsPerSelectTablePointer();
        this.bitsPerSelectHashEntry = builder.getBitsPerSelectHashEntry();
        
    }

    /**
     * Query the index.  The returned {@link HollowHashIndexResult} will be null if no matches were found.  Otherwise, may be used
     * to gather the matched ordinals.
     */
    public HollowHashIndexResult findMatches(Object... query) {
        int hashCode = 0;

        for(int i=0;i<query.length;i++) {
            hashCode ^= HashCodes.hashInt(keyHashCode(query[i], i));
        }

        //System.out.println("QUERY HASH: " + hashCode);

        long bucket = hashCode & matchHashMask;
        long hashBucketBit = bucket * bitsPerMatchHashEntry;
        boolean bucketIsEmpty = matchHashTable.getElementValue(hashBucketBit, bitsPerTraverserField[0]) == 0;

        while(!bucketIsEmpty) {
            if(matchIsEqual(matchHashTable, hashBucketBit, query)) {
                int selectSize = (int) matchHashTable.getElementValue(hashBucketBit + bitsPerMatchHashKey, bitsPerSelectTableSize);
                long selectBucketPointer = matchHashTable.getElementValue(hashBucketBit + bitsPerMatchHashKey + bitsPerSelectTableSize, bitsPerSelectTablePointer);

                return new HollowHashIndexResult(this, selectBucketPointer, selectSize);
            }

            bucket = (bucket + 1) & matchHashMask;
            hashBucketBit = (long)bucket * bitsPerMatchHashEntry;
            bucketIsEmpty = matchHashTable.getElementValue(hashBucketBit, bitsPerTraverserField[0]) == 0;
        }

        return null;
    }

    private int keyHashCode(Object key, int fieldIdx) {
        switch(matchFields[fieldIdx].getFieldType()) {
        case BOOLEAN:
            return HollowReadFieldUtils.booleanHashCode((Boolean)key);
        case DOUBLE:
            return HollowReadFieldUtils.doubleHashCode(((Double)key).doubleValue());
        case FLOAT:
            return HollowReadFieldUtils.floatHashCode(((Float)key).floatValue());
        case INT:
            return HollowReadFieldUtils.intHashCode(((Integer)key).intValue());
        case LONG:
            return HollowReadFieldUtils.longHashCode(((Long)key).longValue());
        case REFERENCE:
            return ((Integer)key).intValue();
        case BYTES:
            return HashCodes.hashCode((byte[])key);
        case STRING:
            return HashCodes.hashCode((String)key);
        }

        throw new IllegalArgumentException("I don't know how to hash a " + matchFields[fieldIdx].getFieldType());
    }

    private boolean matchIsEqual(FixedLengthElementArray matchHashTable, long hashBucketBit, Object[] query) {
        for(int i=0;i<matchFields.length;i++) {
            HollowHashIndexField field = matchFields[i];
            int hashOrdinal = (int)matchHashTable.getElementValue(hashBucketBit + offsetPerTraverserField[field.getBaseIteratorFieldIdx()], bitsPerTraverserField[field.getBaseIteratorFieldIdx()]) - 1;

            HollowTypeReadState readState = field.getBaseDataAccess();
            int fieldPath[] = field.getSchemaFieldPositionPath();

            if(fieldPath.length == 0) {
                if(hashOrdinal != ((Integer)query[i]).intValue())
                    return false;
            } else {
                for(int j=0;j<fieldPath.length - 1;j++) {
                    HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                    readState = objectAccess.getSchema().getReferencedTypeState(fieldPath[j]);
                    hashOrdinal = objectAccess.readOrdinal(hashOrdinal, fieldPath[j]);
                }

                HollowObjectTypeReadState objectAccess = (HollowObjectTypeReadState)readState;
                int fieldIdx = fieldPath[fieldPath.length-1];
                if(!HollowReadFieldUtils.fieldValueEquals(objectAccess, hashOrdinal, fieldIdx, query[i]))
                    return false;
            }
        }

        return true;
    }
}
