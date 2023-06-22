package com.netflix.hollow.tools.history.keyindex;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;
import java.util.Arrays;
import java.util.HashMap;

public class HollowOrdinalMapper {
    // Open addressing/linear probing, hashed ordinals to assigned ordinals
    // If hash collision and
    final private Integer[] ordinalMappings;
    private final HashMap<Integer, Object[]> ordinalFieldObjectMapping;
    private final HashMap<Integer, Integer> assignedOrdinalToIndex;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.7;

    private final PrimaryKey primaryKey;
    private final int[][] keyFieldIndices;

    public HollowOrdinalMapper(PrimaryKey primaryKey, int[][] keyFieldIndices, HashMap<Integer, Object[]> ofom) {
        // Start with prime number to assist OA
        this.ordinalMappings = new Integer[2069];
        Arrays.fill(this.ordinalMappings, ORDINAL_NONE);

        this.primaryKey = primaryKey;
        this.keyFieldIndices = keyFieldIndices;
        this.ordinalFieldObjectMapping = ofom;
        this.assignedOrdinalToIndex = new HashMap<>();
    }

    public int findAssignedOrdinal(HollowObjectTypeReadState typeState, int keyOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, keyOrdinal);
        int index = modulus(hashedRecord, ordinalMappings.length);
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
        int index = modulus(hashedRecord, ordinalMappings.length);
        for(int i=0;i<primaryKey.numFields();i++) {
            Object newFieldValue = readValue(typeState, keyOrdinal, i);
            Object existingFieldValue = ordinalFieldObjectMapping.get(index)[i];
            if(!newFieldValue.equals(existingFieldValue)) {
                return false;
            }
        }
        return true;
    }

    private static int modulus(int dividend, int divisor) {
        int modulus = dividend % divisor;
        return modulus < 0 ? modulus + divisor : modulus;
    }

    //returns stored index
    public int storeNewOrdinal(HollowObjectTypeReadState typeState, int ordinal, int assignedOrdinal) {
        int hashedRecord = hashKeyRecord(typeState, ordinal);

        if ((double) size / ordinalMappings.length > LOAD_FACTOR) {
            System.exit(1); //implement later
            //expandTable();
        }

        int index = modulus(hashedRecord, ordinalMappings.length);

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
        //designed to be overwritten
        this.assignedOrdinalToIndex.put(assignedOrdinal, index);
        return index;
    }

    public int getIndex(int ordinal) {
        return this.assignedOrdinalToIndex.get(ordinal);
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
    private Object readValue(HollowObjectTypeReadState typeState, int ordinal, int fieldIdx) {
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