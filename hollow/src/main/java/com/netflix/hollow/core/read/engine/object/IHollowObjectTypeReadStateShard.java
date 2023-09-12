package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

interface IHollowObjectTypeReadStateShard {

    HollowObjectTypeDataElements currentDataElements();

    int shardOrdinalShift();

    int shardOrdinalOffset();

    void setCurrentData(HollowObjectTypeReadState.ShardsHolder shards, HollowObjectTypeDataElements data);

    boolean isNull(int ordinal, int fieldIndex);

    int readOrdinal(int ordinal, int fieldIndex);

    int readInt(int ordinal, int fieldIndex);

    float readFloat(int ordinal, int fieldIndex);

    double readDouble(int ordinal, int fieldIndex);

    long readLong(int ordinal, int fieldIndex);

    Boolean readBoolean(int ordinal, int fieldIndex);

    byte[] readBytes(int ordinal, int fieldIndex);

    String readString(int ordinal, int fieldIndex);

    boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue);

    int findVarLengthFieldHashCode(int ordinal, int fieldIndex);

    /**
     * Warning:  Not thread-safe.  Should only be called within the update thread.
     */
    int bitsRequiredForField(String fieldName);

    void invalidate();

    void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema, BitSet populatedOrdinals, int shardNumber, int numShards);

    long getApproximateHeapFootprintInBytes();

    long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards);
}
