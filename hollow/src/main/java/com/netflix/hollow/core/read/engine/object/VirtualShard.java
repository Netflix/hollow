package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.util.BitSet;

class VirtualShard implements IHollowObjectTypeReadStateShard { // TODO: Currently only splits one shard into two

    HollowObjectTypeReadStateShard originalShard;

    HollowObjectTypeReadStateShard leftShard;
    HollowObjectTypeReadStateShard rightShard;

    VirtualShard(HollowObjectTypeReadStateShard physicalShard) {
        this.originalShard = physicalShard;
        // TODO: initialize left and right physical shards in virtual shard
    }

    @Override
    public HollowObjectTypeDataElements currentDataElements() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setCurrentData(HollowObjectTypeDataElements data) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        // BEFORE:
        // originalShard.readOrdinal(ordinal, fieldIndex);

        // AFTER:
        if (ordinal%2 == 0) {
            rightShard.readOrdinal(ordinal/2, fieldIndex);
        } else {
            leftShard.readOrdinal(ordinal/2, fieldIndex);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int bitsRequiredForField(String fieldName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema, BitSet populatedOrdinals, int shardNumber, int numShards) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long getApproximateHeapFootprintInBytes() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long getApproximateHoleCostInBytes(BitSet populatedOrdinals, int shardNumber, int numShards) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
