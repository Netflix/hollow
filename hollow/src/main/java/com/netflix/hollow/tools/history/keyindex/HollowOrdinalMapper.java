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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.util.ObjectInternPool;
import java.util.Arrays;

public class HollowOrdinalMapper {
    private int size = 0;
    private static final double LOAD_FACTOR = 0.7;
    private static final int STARTING_SIZE = 2069;

    /*
    * hashToAssignedOrdinal: OA/LP record hash -> assigned ordinal
    * fieldHashToObjectOrdinal: field index -> OA/LP record hash -> object ordinal
    * fieldHashToAssignedOrdinal: field index -> OA/LP record hash -> assigned ordinal
    * assignedOrdinalToIndex: assigned ordinal -> index
    *
    * NOTE: hashToAssignedOrdinal and fieldHashToObjectOrdinal are parallel arrays.
    * This is why fieldHashToObjectOrdinal is always used in conjunction with fieldHashToAssignedOrdinal.
    * */
    private int[] hashToAssignedOrdinal;
    private int[][] fieldHashToObjectOrdinal;
    private IntList[][] fieldHashToAssignedOrdinal;
    private int[] assignedOrdinalToIndex;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;
    private final FieldType[] keyFieldTypes;

    private final ObjectInternPool memoizedPool;

    public HollowOrdinalMapper(PrimaryKey primaryKey, boolean[] keyFieldIsIndexed, int[][] keyFieldIndices, FieldType[] keyFieldTypes) {
        this.hashToAssignedOrdinal = new int[STARTING_SIZE];
        this.fieldHashToObjectOrdinal = new int[primaryKey.numFields()][STARTING_SIZE];
        this.fieldHashToAssignedOrdinal = new IntList[primaryKey.numFields()][STARTING_SIZE];
        this.assignedOrdinalToIndex = new int[STARTING_SIZE];

        Arrays.fill(this.hashToAssignedOrdinal, ORDINAL_NONE);
        for(int field=0;field<primaryKey.numFields();field++) {
            Arrays.fill(this.fieldHashToObjectOrdinal[field], ORDINAL_NONE);
        }
        Arrays.fill(this.assignedOrdinalToIndex, ORDINAL_NONE);

        this.primaryKey = primaryKey;
        this.keyFieldIndices = keyFieldIndices;
        this.keyFieldIsIndexed = keyFieldIsIndexed;
        this.keyFieldTypes = keyFieldTypes;

        this.memoizedPool = new ObjectInternPool();
    }

    public void addMatches(int hashCode, Object objectToMatch, int field, FieldType type, IntList results) {
        IntList[] fieldHashes = fieldHashToAssignedOrdinal[field];
        int scanIndex = indexFromHash(hashCode, fieldHashes.length);
        if (fieldHashes[scanIndex] == null)
            return;
        for(int i=0;i<fieldHashes[scanIndex].size();i++) {
            int assignedOrdinal = fieldHashes[scanIndex].get(i);
            Object object = getFieldObject(assignedOrdinal, field, type);
            if(object.equals(objectToMatch))
                results.add(assignedOrdinal);
        }
    }

    public void writeKeyFieldHash(Object fieldObject, int assignedOrdinal, int fieldIdx) {
        if (!keyFieldIsIndexed[fieldIdx])
            return;

        IntList[] fieldHashes = fieldHashToAssignedOrdinal[fieldIdx];

        int fieldHash = hashObject(fieldObject);
        int newIndex = indexFromHash(fieldHash, fieldHashes.length);

        if(fieldHashes[newIndex]==null) {
            fieldHashes[newIndex] = new IntList();
        }

        fieldHashes[newIndex].add(assignedOrdinal);
    }

    public void prepareForRead() {
        memoizedPool.prepareForRead();
    }

    public int findAssignedOrdinal(HollowObjectTypeReadState typeState, int keyOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, keyOrdinal);
        int scanIndex = indexFromHash(hashedRecord, hashToAssignedOrdinal.length);

        while (hashToAssignedOrdinal[scanIndex]!=ORDINAL_NONE) {
            if(recordsAreEqual(typeState, keyOrdinal, scanIndex))
                return hashToAssignedOrdinal[scanIndex];

            scanIndex = (scanIndex + 1) % hashToAssignedOrdinal.length;
        }

        return ORDINAL_NONE;
    }

    private boolean recordsAreEqual(HollowObjectTypeReadState typeState, int keyOrdinal, int index) {
        for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
            if(!keyFieldIsIndexed[fieldIdx])
                continue;

            Object newFieldValue = readValueInState(typeState, keyOrdinal, fieldIdx);
            int existingFieldOrdinalValue = fieldHashToObjectOrdinal[fieldIdx][index];

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

        if ((double) size / hashToAssignedOrdinal.length > LOAD_FACTOR) {
            expandAndRehashTable();
        }

        int newIndex = indexFromHash(hashedRecord, hashToAssignedOrdinal.length);

        // Linear probing
        while (hashToAssignedOrdinal[newIndex] != ORDINAL_NONE) {
            if(recordsAreEqual(typeState, ordinal, newIndex)) {
                assignedOrdinalToIndex[assignedOrdinal]=newIndex;
                return false;
            }
            newIndex = (newIndex + 1) % hashToAssignedOrdinal.length;
        }

        for (int i = 0; i < primaryKey.numFields(); i++) {
            Object objectToHash = readValueInState(typeState, ordinal, i);
            writeKeyFieldHash(objectToHash, assignedOrdinal, i);
        }

        storeFieldObjects(typeState, ordinal, newIndex);

        hashToAssignedOrdinal[newIndex] = assignedOrdinal;
        assignedOrdinalToIndex[assignedOrdinal]=newIndex;
        size++;
        return true;
    }

    private void storeFieldObjects(HollowObjectTypeReadState typeState, int ordinal, int index) {
        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;

            Object objectToStore = readValueInState(typeState, ordinal, i);
            int objectOrdinal = memoizedPool.writeAndGetOrdinal(objectToStore);
            fieldHashToObjectOrdinal[i][index] = objectOrdinal;
        }
    }

    private int[] getFieldOrdinals(int index) {
        int[] fieldObjects = new int[primaryKey.numFields()];
        for(int fieldIdx=0;fieldIdx< primaryKey.numFields();fieldIdx++) {
            fieldObjects[fieldIdx] = fieldHashToObjectOrdinal[fieldIdx][index];
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

        int[] newTable = new int[hashToAssignedOrdinal.length*2];
        Arrays.fill(newTable, ORDINAL_NONE);

        int[][] newFieldMappings = new int[primaryKey.numFields()][hashToAssignedOrdinal.length*2];
        IntList[][] newFieldHashToOrdinal = new IntList[primaryKey.numFields()][hashToAssignedOrdinal.length*2];
        assignedOrdinalToIndex = Arrays.copyOf(assignedOrdinalToIndex, hashToAssignedOrdinal.length*2);

        for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
            IntList[] hashToOrdinal = fieldHashToAssignedOrdinal[fieldIdx];

            for (IntList ordinalList : hashToOrdinal) {
                if(ordinalList==null || ordinalList.size()==0)
                    continue;

                // Recompute original hash, objects in the IntList don't necessarily share the same hash (see indexFromHash)
                for (int i=0;i<ordinalList.size();i++) {
                    int ordinal = ordinalList.get(i);
                    Object originalFieldObject = getFieldObject(ordinal, fieldIdx, keyFieldTypes[fieldIdx]);
                    int originalHash = hashObject(originalFieldObject);
                    int newIndex = indexFromHash(originalHash, newTable.length);
                    if (newFieldHashToOrdinal[fieldIdx][newIndex] == null) {
                        newFieldHashToOrdinal[fieldIdx][newIndex] = new IntList();
                    }
                    newFieldHashToOrdinal[fieldIdx][newIndex].add(ordinal);
                }
            }
        }

        for(int i=0;i<hashToAssignedOrdinal.length;i++) {
            if(hashToAssignedOrdinal[i]==ORDINAL_NONE)
                continue;
            // Recompute original hash
            int firstHash = hashFromIndex(i);
            int newIndex = rehashExistingRecord(newTable, firstHash, hashToAssignedOrdinal[i]);

            for(int fieldIdx=0;fieldIdx<primaryKey.numFields();fieldIdx++) {
                newFieldMappings[fieldIdx][newIndex] = fieldHashToObjectOrdinal[fieldIdx][i];
            }

            // Store new index in old table, so we can remap assignedOrdinalToIndex
            hashToAssignedOrdinal[i]=newIndex;
        }

        for (int assignedOrdinal=0;assignedOrdinal<assignedOrdinalToIndex.length;assignedOrdinal++) {
            int previousIndex = assignedOrdinalToIndex[assignedOrdinal];
            if (previousIndex==ORDINAL_NONE)
                //linear, so we can break
                break;
            int newIndex = hashToAssignedOrdinal[previousIndex];

            assignedOrdinalToIndex[assignedOrdinal]=newIndex;
        }

        this.hashToAssignedOrdinal = newTable;
        this.fieldHashToObjectOrdinal = newFieldMappings;
        this.fieldHashToAssignedOrdinal = newFieldHashToOrdinal;
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
        int fieldOrdinal = fieldHashToObjectOrdinal[fieldIndex][index];
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

    private static int hashObject(Object object) {
        return HashCodes.hashInt(HollowReadFieldUtils.hashObject(object));
    }
}