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
    private final HashMap<Integer, Object[]> indexFieldObjectMapping;
    private final HashMap<Integer, Integer> assignedOrdinalToIndex;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;
    private final boolean[] keyFieldIsIndexed;

    private final ObjectInternPool memoizedPool;

    public HollowOrdinalMapper(PrimaryKey primaryKey, boolean[] keyFieldIsIndexed, int[][] keyFieldIndices) {
        // Start with prime number to assist OA
        this.ordinalMappings = new Integer[2069];
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
        int index = indexFromHash(hashedRecord);

        while (ordinalMappings[index]!=ORDINAL_NONE) {
            if(recordsAreEqual(typeState, keyOrdinal, hashedRecord))
                return ordinalMappings[index];
            index = (index + 1) % ordinalMappings.length;
        }
        return ORDINAL_NONE;
    }

    private boolean recordsAreEqual(HollowObjectTypeReadState typeState, int keyOrdinal, int hashedRecord) {
        int keyHash = hashKeyRecord(typeState, keyOrdinal);
        if(keyHash!=hashedRecord) {
            return false;
        }
        int index = indexFromHash(hashedRecord);
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

    // Java modulo is more like a remainder, and we don't want it to be negative
    // Even if the hash is
    private int indexFromHash(int hashedValue) {
        int modulus = hashedValue % ordinalMappings.length;
        return modulus < 0 ? modulus + ordinalMappings.length : modulus;
    }

    //returns stored index
    public int storeNewRecord(HollowObjectTypeReadState typeState, int ordinal, int assignedOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, ordinal);

        if ((double) size / ordinalMappings.length > LOAD_FACTOR) {
            expandTable();
        }

        int index = indexFromHash(hashedRecord);

        // Linear probing
        while (ordinalMappings[index] != ORDINAL_NONE) {
            if(recordsAreEqual(typeState, ordinal, index)) {
                //TODO: not ordinal, shouldn't return ORDINAL_NONE
                this.assignedOrdinalToIndex.put(assignedOrdinal, index);
                return ORDINAL_NONE;
            }
            index = (index + 1) % ordinalMappings.length;
        }

        ordinalMappings[index] = assignedOrdinal;
        size++;

        storeFields(typeState, ordinal, index);

        this.assignedOrdinalToIndex.put(assignedOrdinal, index);
        return index;
    }

    private void expandTable() {
        Integer[] newOrdinalMapping = new Integer[ordinalMappings.length * 2];
        System.arraycopy(ordinalMappings, 0, newOrdinalMapping, 0, ordinalMappings.length);
        ordinalMappings = newOrdinalMapping;
        System.exit(1);
        //TODO: support rehashing
    }

    private void storeFields(HollowObjectTypeReadState typeState, int ordinal, int index) {
        if(!indexFieldObjectMapping.containsKey(index))
            indexFieldObjectMapping.put(index, new Object[primaryKey.numFields()]);
        for(int i=0;i<primaryKey.numFields();i++) {
            if(!keyFieldIsIndexed[i])
                continue;
            Object objectToStore = readValueInState(typeState, ordinal, i);
            indexFieldObjectMapping.get(index)[i] = memoizedPool.intern(objectToStore);
        }
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