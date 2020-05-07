package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

public interface VariableLengthData extends ByteData {

    void loadFrom(HollowBlobInput in, long numBytesInVarLengthData) throws IOException;

    void copy(ByteData src, long srcPos, long destPos, long length);

    void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length);
}
