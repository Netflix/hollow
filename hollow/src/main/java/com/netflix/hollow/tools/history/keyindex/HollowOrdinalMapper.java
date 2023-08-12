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
    
    private Integer[] ordinalMappings;
    private Integer[] originalHash;

    private HashMap<Integer, int[]> indexFieldOrdinalMapping;
    private final HashMap<Integer, Integer> assignedOrdinalToIndex;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;
    private final FieldType[] keyFieldTypes;

    private final ObjectInternPool memoizedPool;

    public HollowOrdinalMapper(PrimaryKey primaryKey, boolean[] keyFieldIsIndexed, int[][] keyFieldIndices, FieldType[] keyFieldTypes) {
        // Start with prime number to assist OA
        this.ordinalMappings = new Integer[2069];
        this.originalHash = new Integer[2069];
        Arrays.fill(this.ordinalMappings, ORDINAL_NONE);

        this.primaryKey = primaryKey;
        this.keyFieldIndices = keyFieldIndices;
        this.keyFieldIsIndexed = keyFieldIsIndexed;
        this.keyFieldTypes = keyFieldTypes;

        this.indexFieldOrdinalMapping = new HashMap<>();
        this.assignedOrdinalToIndex = new HashMap<>();

        this.memoizedPool = new ObjectInternPool();
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
            int existingFieldOrdinalValue = indexFieldOrdinalMapping.get(index)[fieldIdx];

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
                this.assignedOrdinalToIndex.put(assignedOrdinal, index);
                return false;
            }
            index = (index + 1) % ordinalMappings.length;
        }

        ordinalMappings[index] = assignedOrdinal;
        originalHash[index] = hashedRecord;
        size++;

        storeFields(typeState, ordinal, index);

        this.assignedOrdinalToIndex.put(assignedOrdinal, index);
        return true;
    }

    private void storeFields(HollowObjectTypeReadState typeState, int ordinal, int index) {
        if(!indexFieldOrdinalMapping.containsKey(index))
            indexFieldOrdinalMapping.put(index, new int[primaryKey.numFields()]);

        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;

            Object objectToStore = readValueInState(typeState, ordinal, i);
            int objectOrdinal = memoizedPool.writeAndGetOrdinal(objectToStore);
            indexFieldOrdinalMapping.get(index)[i] = objectOrdinal;
        }
    }

    private void expandAndRehashTable() {
        Integer[] newTable = new Integer[ordinalMappings.length*2];
        Arrays.fill(newTable, ORDINAL_NONE);

        Integer[] newOriginalHash = new Integer[originalHash.length*2];
        HashMap<Integer, int[]> newIndexFieldObjectMapping = new HashMap<>();

        for(int i=0;i<ordinalMappings.length;i++) {
            if(ordinalMappings[i]==ORDINAL_NONE)
                continue;
            int newIndex = rehashExistingRecord(newTable, originalHash[i], ordinalMappings[i]);
            newOriginalHash[newIndex] = originalHash[i];

            int[] fieldOrdinalObjects = indexFieldOrdinalMapping.get(i);
            newIndexFieldObjectMapping.put(newIndex, fieldOrdinalObjects);

            // Store new index in old table so we can remap assignedOrdinalToIndex
            ordinalMappings[i]=newIndex;
        }

        for (Map.Entry<Integer, Integer> entry : assignedOrdinalToIndex.entrySet()) {
            int assignedOrdinal = entry.getKey();
            int previousIndex = entry.getValue();
            int newIndex = ordinalMappings[previousIndex];

            assignedOrdinalToIndex.put(assignedOrdinal, newIndex);
        }

        this.ordinalMappings = newTable;
        this.originalHash = newOriginalHash;
        this.indexFieldOrdinalMapping = newIndexFieldObjectMapping;
    }

    private int rehashExistingRecord(Integer[] newTable, int originalHash, int assignedOrdinal) {
        int newIndex = indexFromHash(originalHash, newTable.length);
        while (newTable[newIndex]!=ORDINAL_NONE)
            newIndex = (newIndex + 1) % newTable.length;

        newTable[newIndex] = assignedOrdinal;
        return newIndex;
    }

    public Object getFieldObject(int assignedOrdinal, int fieldIndex, FieldType type) {
        int index = assignedOrdinalToIndex.get(assignedOrdinal);
        int fieldOrdinal = indexFieldOrdinalMapping.get(index)[fieldIndex];
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