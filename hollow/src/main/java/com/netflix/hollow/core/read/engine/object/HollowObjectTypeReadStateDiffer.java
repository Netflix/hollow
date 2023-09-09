package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class HollowObjectTypeReadStateDiffer {
    public static void diff(HollowObjectTypeReadState from, HollowObjectTypeReadState to, BitSet populatedOrdinals) {
//        if (from.numShards() != to.numShards()) {
//            throw new IllegalArgumentException("Shard count mismatch");
//        }
//        if (!from.getSchema().equals(to.getSchema())) {
//            throw new IllegalArgumentException("Schema mismatch");
//        }
//        if (!from.getPopulatedOrdinals().equals(to.getPopulatedOrdinals())) {
//            throw new IllegalArgumentException("Populated ordinals mismatch");
//        }
//
//        List<String> commonFieldNames = new ArrayList<String>();
//        for(int i=0;i<from.getSchema().numFields();i++)
//            commonFieldNames.add(from.getSchema().getFieldName(i));
//        Collections.sort(commonFieldNames);
//
//        int fieldIndexes[] = new int[commonFieldNames.size()];
//        for(int i=0;i<commonFieldNames.size();i++) {
//            fieldIndexes[i] = from.getSchema().getPosition(commonFieldNames.get(i));
//        }
//
//        HollowObjectSchema schema = from.getSchema();
//        int numShards = from.numShards();
//        for (int s=0;s<from.numShards();s++) {
//            HollowObjectTypeReadStateShard fromShard = (HollowObjectTypeReadStateShard) from.shardsVolatile.shards[s];
//            HollowObjectTypeReadStateShard toShard= (HollowObjectTypeReadStateShard) to.shardsVolatile.shards[s];
//
//            HollowObjectTypeDataElements fromData = fromShard.currentDataElements();
//            HollowObjectTypeDataElements toData = toShard.currentDataElements();
//            int ordinal = populatedOrdinals.nextSetBit(0);
//            while(ordinal != ORDINAL_NONE) {
//                if((ordinal & (numShards - 1)) == s) {
//                    int shardOrdinal = ordinal / numShards;
//                    for(int i=0;i<fieldIndexes.length;i++) {
//                        int fieldIdx = fieldIndexes[i];
//                        if(!schema.getFieldType(fieldIdx).isVariableLength()) {
//                            long fromBitOffset = fieldOffset(fromData, shardOrdinal, fieldIdx);
//                            long toBitOffset = fieldOffset(toData, shardOrdinal, fieldIdx);
//                            int fromNumBitsForField = fromData.bitsPerField[fieldIdx];
//                            int toNumBitsForField = toData.bitsPerField[fieldIdx];
//
//                            long fromFixedLengthValue = fromNumBitsForField <= 56 ?
//                                    fromData.fixedLengthData.getElementValue(fromBitOffset, fromNumBitsForField)
//                                    : fromData.fixedLengthData.getLargeElementValue(fromBitOffset, fromNumBitsForField);
//                            long toFixedLengthValue = toNumBitsForField <= 56 ?
//                                    toData.fixedLengthData.getElementValue(toBitOffset, toNumBitsForField)
//                                    : toData.fixedLengthData.getLargeElementValue(toBitOffset, toNumBitsForField);
//
//                            if (fromFixedLengthValue != toFixedLengthValue) {
//                                throw new IllegalArgumentException("Found mismatchin fixed len value");
//                            }
//                        } else {
//                            int fromVarLengthFieldHashCode = findVarLengthFieldHashCode(fromData, shardOrdinal, fieldIdx);
//                            int toVarLengthFieldHashCode = findVarLengthFieldHashCode(toData, shardOrdinal, fieldIdx);
//                            if (fromVarLengthFieldHashCode != toVarLengthFieldHashCode) {
//                                fromVarLengthFieldHashCode = findVarLengthFieldHashCode(fromData, shardOrdinal, fieldIdx);
//                                toVarLengthFieldHashCode = findVarLengthFieldHashCode(toData, shardOrdinal, fieldIdx);
//
//                                System.out.println("SNAP: SNAP: checksum mismatch for ordinal " + ordinal
//                                        + ": from= " + from.readString(ordinal, 0) + ", to= "
//                                        + to.readString(ordinal, 0));
//
//                                throw new IllegalArgumentException("Found mismatch in var len field hashcode");
//                            }
//                        }
//                    }
//                }
//                ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
//            }
//        }
    }

    public static int findVarLengthFieldHashCode(HollowObjectTypeDataElements currentData, int ordinal, int fieldIndex) {
        int numBitsForField;
        long endByte;
        long startByte;

        numBitsForField = currentData.bitsPerField[fieldIndex];
        long currentBitOffset = fieldOffset(currentData, ordinal, fieldIndex);
        endByte = currentData.fixedLengthData.getElementValue(currentBitOffset, numBitsForField);
        startByte = ordinal != 0 ? currentData.fixedLengthData.getElementValue(currentBitOffset - currentData.bitsPerRecord, numBitsForField) : 0;

        if((endByte & (1L << numBitsForField - 1)) != 0)
            return -1;

        startByte &= (1L << numBitsForField - 1) - 1;

        int length = (int)(endByte - startByte);

        int hashCode = HashCodes.hashCode(currentData.varLengthData[fieldIndex], startByte, length);

        return hashCode;
    }

    private static long fieldOffset(HollowObjectTypeDataElements currentData, int ordinal, int fieldIndex) {
        return ((long)currentData.bitsPerRecord * ordinal) + currentData.bitOffsetPerField[fieldIndex];
    }
}
