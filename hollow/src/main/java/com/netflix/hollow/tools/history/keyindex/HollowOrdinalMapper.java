/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.util.ObjectInternPool;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HollowOrdinalMapper {
    private int size = 0;
    private static final double LOAD_FACTOR = 0.7;
    
    private int[] ordinalMappings;
    private int[][] fieldOrdinalMappings;
    private int[] assignedOrdinalToIndex;
    private int[][] fieldHashToOrdinal;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;
    private final FieldType[] keyFieldTypes;

    private final ObjectInternPool memoizedPool;

    public HollowOrdinalMapper(PrimaryKey primaryKey, boolean[] keyFieldIsIndexed, int[][] keyFieldIndices, FieldType[] keyFieldTypes) {
        // Start with prime number to assist OA
        this.ordinalMappings = new int[2069];
        this.fieldOrdinalMappings = new int[primaryKey.numFields()][2069];
        this.fieldHashToOrdinal = new int[primaryKey.numFields()][2069];
        Arrays.fill(this.ordinalMappings, ORDINAL_NONE);
        for(int i=0;i<primaryKey.numFields();i++) {
            Arrays.fill(this.fieldHashToOrdinal[i], ORDINAL_NONE);
        }

        this.primaryKey = primaryKey;
        this.keyFieldIndices = keyFieldIndices;
        this.keyFieldIsIndexed = keyFieldIsIndexed;
        this.keyFieldTypes = keyFieldTypes;

        this.assignedOrdinalToIndex = new int[2069];
        Arrays.fill(this.assignedOrdinalToIndex, ORDINAL_NONE);

        this.memoizedPool = new ObjectInternPool();
    }

    public void addMatches(int hashCode, Object objectToMatch, int field, FieldType type, IntList results) {
        int[] fieldHashes = fieldHashToOrdinal[field];
        int index = indexFromHash(hashCode, fieldHashes.length);
        while(fieldHashes[index] != ORDINAL_NONE) {
            int ordinal = fieldHashes[index];
            Object matchingObject = getFieldObject(ordinal, field, type);
            if(objectToMatch.equals(matchingObject)) {
                results.add(ordinal);
            }
            index = (index + 1) % fieldHashes.length;
        }
    }

    public void writeKeyField(Object[] fieldObjects, int assignedOrdinal, int fieldIdx) {
        if (!keyFieldIsIndexed[fieldIdx])
            return;

        Object fieldObject = fieldObjects[fieldIdx];
        int fieldHash = HashCodes.hashInt(HollowReadFieldUtils.hashObject(fieldObject));

        int index = indexFromHash(fieldHash, fieldHashToOrdinal[fieldIdx].length);
        while (fieldHashToOrdinal[fieldIdx][index] != ORDINAL_NONE) {
            index = (index + 1) % fieldHashToOrdinal[fieldIdx].length;
        }
        fieldHashToOrdinal[fieldIdx][index] = assignedOrdinal;
    }

    public void prepareForRead() {
        memoizedPool.prepareForRead();
    }

    public int findAssignedOrdinal(HollowObjectTypeReadState typeState, int keyOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, keyOrdinal);
        int index = indexFromHash(hashedRecord, ordinalMappings.length);

        while (ordinalMappings[index]!=ORDINAL_NONE) {
            if(recordsAreEqual(typeState, keyOrdinal, index))
                return ordinalMappings[index];
            index = (index + 1) % ordinalMappings.length;
        }

        return ORDINAL_NONE;
    }

    private boolean recordsAreEqual(HollowObjectTypeReadState typeState, int keyOrdinal, int index) {
        for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
            if(!keyFieldIsIndexed[fieldIdx])
                continue;

            Object newFieldValue = readValueInState(typeState, keyOrdinal, fieldIdx);
            int existingFieldOrdinalValue = fieldOrdinalMappings[fieldIdx][index];

            //Assuming two records in the same cycle cannot be equal
            if(memoizedPool.ordinalInCurrentCycle(existingFieldOrdinalValue)) {
                return false;
            }
            Object existingFieldObjectValue = memoizedPool.getObject(existingFieldOrdinalValue, keyFieldTypes[fieldIdx]);
            if (!newFieldValue.equals(existingFieldObjectValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean storeNewRecord(HollowObjectTypeReadState typeState, int ordinal, int assignedOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, ordinal);

        if ((double) size / ordinalMappings.length > LOAD_FACTOR) {
            expandAndRehashTable();
        }

        int index = indexFromHash(hashedRecord, ordinalMappings.length);

        // Linear probing
        while (ordinalMappings[index] != ORDINAL_NONE) {
            if(recordsAreEqual(typeState, ordinal, index)) {
                this.assignedOrdinalToIndex[assignedOrdinal]=index;
                return false;
            }
            index = (index + 1) % ordinalMappings.length;
        }

        ordinalMappings[index] = assignedOrdinal;
        size++;

        storeFields(typeState, ordinal, index);

        this.assignedOrdinalToIndex[assignedOrdinal]=index;
        return true;
    }

    private void storeFields(HollowObjectTypeReadState typeState, int ordinal, int index) {
        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;

            Object objectToStore = readValueInState(typeState, ordinal, i);
            int objectOrdinal = memoizedPool.writeAndGetOrdinal(objectToStore);
            fieldOrdinalMappings[i][index] = objectOrdinal;
        }
    }

    private int[] getFieldOrdinals(int index) {
        int[] fieldObjects = new int[primaryKey.numFields()];
        for(int fieldIdx=0;fieldIdx< primaryKey.numFields();fieldIdx++) {
            fieldObjects[fieldIdx] = fieldOrdinalMappings[fieldIdx][index];
        }
        return fieldObjects;
    }

    private int hashFromIndex(int index) {
        int[] fieldOrdinals = getFieldOrdinals(index);
        Object[] fieldObjects = new Object[primaryKey.numFields()];
        for(int fieldIdx=0;fieldIdx< primaryKey.numFields();fieldIdx++) {
            fieldObjects[fieldIdx] = memoizedPool.getObject(fieldOrdinals[fieldIdx], keyFieldTypes[fieldIdx]);
        }
        return hashKeyRecord(fieldObjects);
    }

    private void expandAndRehashTable() {
        prepareForRead();

        int[] newTable = new int[ordinalMappings.length*2];
        Arrays.fill(newTable, ORDINAL_NONE);

        int[][] newFieldMappings = new int[primaryKey.numFields()][ordinalMappings.length*2];
        int[][] newFieldHashToOrdinal = new int[primaryKey.numFields()][ordinalMappings.length*2];
        for(int i=0;i<primaryKey.numFields();i++) {
            Arrays.fill(newFieldHashToOrdinal[i], ORDINAL_NONE);
        }
        assignedOrdinalToIndex = Arrays.copyOf(assignedOrdinalToIndex, ordinalMappings.length*2);

        for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
            for(int i=0;i<fieldHashToOrdinal[fieldIdx].length;i++) {
                if(fieldHashToOrdinal[fieldIdx][i]==ORDINAL_NONE)
                    continue;
                int objectIndex = assignedOrdinalToIndex[fieldHashToOrdinal[fieldIdx][i]];
                int fieldObjectOrdinal = fieldOrdinalMappings[fieldIdx][objectIndex];
                Object originalFieldObject = memoizedPool.getObject(fieldObjectOrdinal, keyFieldTypes[fieldIdx]);
                int firstHash = HashCodes.hashInt(HollowReadFieldUtils.hashObject(originalFieldObject));
                int newIndex = indexFromHash(firstHash, newTable.length);
                newFieldHashToOrdinal[fieldIdx][newIndex] = fieldHashToOrdinal[fieldIdx][i];
            }
        }

        for(int i=0;i<ordinalMappings.length;i++) {
            if(ordinalMappings[i]==ORDINAL_NONE)
                continue;
            // Recompute original hash
            int firstHash = hashFromIndex(i);
            int newIndex = rehashExistingRecord(newTable, firstHash, ordinalMappings[i]);

            for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
                newFieldMappings[fieldIdx][newIndex] = fieldOrdinalMappings[fieldIdx][i];
            }

            // Store new index in old table so we can remap assignedOrdinalToIndex
            ordinalMappings[i]=newIndex;
        }

        for (int assignedOrdinal=0;assignedOrdinal<assignedOrdinalToIndex.length;assignedOrdinal++) {
            int previousIndex = assignedOrdinalToIndex[assignedOrdinal];
            if (previousIndex==ORDINAL_NONE)
                //linear, so we can break
                break;
            int newIndex = ordinalMappings[previousIndex];

            assignedOrdinalToIndex[assignedOrdinal]=newIndex;
        }

        this.ordinalMappings = newTable;
        this.fieldOrdinalMappings = newFieldMappings;
        this.fieldHashToOrdinal = newFieldHashToOrdinal;
    }

    private int rehashExistingRecord(int[] newTable, int originalHash, int assignedOrdinal) {
        int newIndex = indexFromHash(originalHash, newTable.length);
        while (newTable[newIndex]!=ORDINAL_NONE)
            newIndex = (newIndex + 1) % newTable.length;

        newTable[newIndex] = assignedOrdinal;
        return newIndex;
    }

    public Object getFieldObject(int assignedOrdinal, int fieldIndex, FieldType type) {
        int index = assignedOrdinalToIndex[assignedOrdinal];
        int fieldOrdinal = fieldOrdinalMappings[fieldIndex][index];
        return memoizedPool.getObject(fieldOrdinal, type);
    }

    private int hashKeyRecord(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for (int i = 0; i < primaryKey.numFields(); i++) {
            Object fieldObjectToHash = readValueInState(typeState, ordinal, i);
            int fieldHashCode = HollowReadFieldUtils.hashObject(fieldObjectToHash);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    private int hashKeyRecord(Object[] objects) {
        int hashCode = 0;
        for (Object fieldObject : objects) {
            int fieldHashCode = HollowReadFieldUtils.hashObject(fieldObject);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    //taken and modified from HollowPrimaryKeyValueDeriver
    public Object readValueInState(HollowObjectTypeReadState typeState, int ordinal, int fieldIdx) {
        HollowObjectSchema schema = typeState.getSchema();

        int lastFieldPath = keyFieldIndices[fieldIdx].length - 1;
        for (int i = 0; i < lastFieldPath; i++) {
            int fieldPosition = keyFieldIndices[fieldIdx][i];
            ordinal = typeState.readOrdinal(ordinal, fieldPosition);
            typeState = (HollowObjectTypeReadState) schema.getReferencedTypeState(fieldPosition);
            schema = typeState.getSchema();
        }

        return HollowReadFieldUtils.fieldValueObject(typeState, ordinal, keyFieldIndices[fieldIdx][lastFieldPath]);
    }

    // Java modulo is more like a remainder, indices can't be negative
    private static int indexFromHash(int hashedValue, int length) {
        int modulus = hashedValue % length;
        return modulus < 0 ? modulus + length : modulus;
    }
}