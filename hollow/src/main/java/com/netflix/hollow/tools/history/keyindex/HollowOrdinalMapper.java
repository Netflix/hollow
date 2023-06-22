package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.tools.util.ObjectInternPool;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import java.util.Arrays;
import java.util.HashMap;

public class HollowOrdinalMapper {
    private int size = 0;
    private static final double LOAD_FACTOR = 0.7;
    
    private Integer[] ordinalMappings;
    private Integer[] originalHash;

    private HashMap<Integer, Object[]> indexFieldObjectMapping;
    private final HashMap<Integer, Integer> assignedOrdinalToIndex;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;

    private final ObjectInternPool memoizedPool;

    public HollowOrdinalMapper(PrimaryKey primaryKey, boolean[] keyFieldIsIndexed, int[][] keyFieldIndices) {
        // Start with prime number to assist OA
        this.ordinalMappings = new Integer[2069];
        this.originalHash = new Integer[2069];
        Arrays.fill(this.ordinalMappings, ORDINAL_NONE);

        this.primaryKey = primaryKey;
        this.keyFieldIndices = keyFieldIndices;
        this.keyFieldIsIndexed = keyFieldIsIndexed;

        this.indexFieldObjectMapping = new HashMap<>();
        this.assignedOrdinalToIndex = new HashMap<>();

        this.memoizedPool = new ObjectInternPool();
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
        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;

            Object newFieldValue = readValueInState(typeState, keyOrdinal, i);
            Object existingFieldValue = indexFieldObjectMapping.get(index)[i];
            if(!newFieldValue.equals(existingFieldValue)) {
                return false;
            }
        }
        return true;
    }

    // Java modulo is more like a remainder, indices can't be negative
    private static int indexFromHash(int hashedValue, int length) {
        int modulus = hashedValue % length;
        return modulus < 0 ? modulus + length : modulus;
    }

    public int storeNewRecord(HollowObjectTypeReadState typeState, int ordinal, int assignedOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, ordinal);

        if ((double) size / ordinalMappings.length > LOAD_FACTOR) {
            expandAndRehashTable();
        }

        int index = indexFromHash(hashedRecord, ordinalMappings.length);

        // Linear probing
        while (ordinalMappings[index] != ORDINAL_NONE) {
            if(recordsAreEqual(typeState, ordinal, index)) {
                this.assignedOrdinalToIndex.put(assignedOrdinal, index);
                return ORDINAL_NONE;
            }
            index = (index + 1) % ordinalMappings.length;
        }

        ordinalMappings[index] = assignedOrdinal;
        originalHash[index] = hashedRecord;
        size++;

        storeFields(typeState, ordinal, index);

        this.assignedOrdinalToIndex.put(assignedOrdinal, index);
        return index;
    }

    private void storeFields(HollowObjectTypeReadState typeState, int ordinal, int index) {
        if(!indexFieldObjectMapping.containsKey(index))
            indexFieldObjectMapping.put(index, new Object[primaryKey.numFields()]);

        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;

            Object objectToStore = readValueInState(typeState, ordinal, i);
            //indexFieldObjectMapping.get(index)[i] = memoizedPool.intern(objectToStore);
            indexFieldObjectMapping.get(index)[i] = objectToStore;
        }
    }

    private void expandAndRehashTable() {
        Integer[] newTable = new Integer[ordinalMappings.length*2];
        Arrays.fill(newTable, ORDINAL_NONE);

        Integer[] newOriginalHash = new Integer[originalHash.length*2];
        HashMap<Integer, Object[]> newIndexFieldObjectMapping = new HashMap<>();

        for(int i=0;i<ordinalMappings.length;i++) {
            if(ordinalMappings[i]==ORDINAL_NONE)
                continue;
            int newIndex = rehashExistingRecord(newTable, originalHash[i], ordinalMappings[i]);
            newOriginalHash[newIndex] = originalHash[i];

            Object[] fieldObjects = indexFieldObjectMapping.get(i);
            newIndexFieldObjectMapping.put(newIndex, fieldObjects);
        }

        this.ordinalMappings = newTable;
        this.originalHash = newOriginalHash;
        this.indexFieldObjectMapping = newIndexFieldObjectMapping;
    }

    private int rehashExistingRecord(Integer[] newTable, int originalHash, int assignedOrdinal) {
        int newIndex = indexFromHash(originalHash, newTable.length);
        while (newTable[newIndex]!=ORDINAL_NONE)
            newIndex = (newIndex + 1) % newTable.length;

        assignedOrdinalToIndex.put(assignedOrdinal, newIndex);
        newTable[newIndex] = assignedOrdinal;
        return newIndex;
    }

    public Object getFieldObject(int keyOrdinal, int fieldIndex) {
        int index = assignedOrdinalToIndex.get(keyOrdinal);
        return indexFieldObjectMapping.get(index)[fieldIndex];
    }

    private int hashKeyRecord(HollowObjectTypeReadState typeState, int ordinal) {
        int hashCode = 0;
        for (int i = 0; i < primaryKey.numFields(); i++) {
            int fieldHashCode = HollowReadFieldUtils.fieldHashCode(typeState, ordinal, i);
            hashCode = (hashCode * 31) ^ fieldHashCode;
        }
        return HashCodes.hashInt(hashCode);
    }

    //taken and modified from HollowPrimaryKeyValueDeriver
    private Object readValueInState(HollowObjectTypeReadState typeState, int ordinal, int fieldIdx) {
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
}