package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.IOException;

/**
 * Conceptually this can be thought of as a single byte array or buffer of undefined length.  It will grow automatically
 * when a byte is written to an index greater than the currently allocated array/buffer.
 *  *
 */
public interface VariableLengthData extends ByteData {

    /**
     * Load <i>length</i> bytes of data from the supplied {@code HollowBlobInput}
     *
     * @param in the {@code HollowBlobInput}
     * @param length the length of the data to load
     * @throws IOException if data could not be loaded
     */
    void loadFrom(HollowBlobInput in, long length) throws IOException;

    /**
     * Copy bytes from another {@code VariableLengthData} object.
     *
     * @param src the source {@code VariableLengthData}
     * @param srcPos position in source data to begin copying from
     * @param destPos position in destination data to begin copying to
     * @param length length of data to copy in bytes
     */
    void copy(ByteData src, long srcPos, long destPos, long length);

    /**
     * Copies data from the provided source into destination, guaranteeing that if the update is seen
     * by another thread, then all other writes prior to this call are also visible to that thread.
     *
     * @param src the source data
     * @param srcPos position in source data to begin copying from
     * @param destPos position in destination to begin copying to
     * @param length length of data to copy in bytes
     */
    void orderedCopy(VariableLengthData src, long srcPos, long destPos, long length);

    /**
     * Data size in bytes
     * @return size in bytes
     */
    long size();
}
