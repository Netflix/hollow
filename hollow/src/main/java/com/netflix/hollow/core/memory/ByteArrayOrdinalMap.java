/*
 *  Copyright 2016-2021 Netflix, Inc.
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
package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This data structure maps byte sequences to ordinals.  This is a hash table.
 * <p>
 * The <code>pointersAndOrdinals</code> AtomicLongArray contains keys, and the {@link ByteDataArray}
 * contains values.  Each key has two components.
 * <p>
 * The high 29 bits in the key represents the ordinal.  The low 35 bits represents the pointer to the start position
 * of the byte sequence in the ByteDataBuffer.  Each byte sequence is preceded by a variable-length integer
 * (see {@link VarInt}), indicating the length of the sequence.<p>
 *
 * @author dkoszewnik
 */
public class ByteArrayOrdinalMap {

    private static final Logger LOG = Logger.getLogger(ByteArrayOrdinalMap.class.getName());

    private static final long EMPTY_BUCKET_VALUE = -1L;

    private static final int BITS_PER_ORDINAL = 29;
    private static final int BITS_PER_POINTER = Long.SIZE - BITS_PER_ORDINAL;
    private static final long POINTER_MASK = (1L << BITS_PER_POINTER) - 1;
    private static final long ORDINAL_MASK = (1L << BITS_PER_ORDINAL) - 1;
    private static final long MAX_BYTE_DATA_LENGTH = 1L << BITS_PER_POINTER;
    private static final int SINGLE_PARTITION_ORDINAL_LIMIT = 1 << 28;

    /// Thread safety:  We need volatile access semantics to the individual elements in the
    /// pointersAndOrdinals array.
    /// Ordinal is the high 29 bits.  Pointer to byte data is the low 35 bits.
    /// In addition need volatile access to the reference when resize occurs
    private volatile AtomicLongArray pointersAndOrdinals;
    private final ByteDataArray byteData;
    private final FreeOrdinalTracker freeOrdinalTracker;
    private final Supplier<Boolean> ignoreOrdinalThresholdBreach;
    private int size;
    private int sizeBeforeGrow;

    private BitSet unusedPreviousOrdinals;

    private long[] pointersByOrdinal;

    /**
     * Creates a byte array ordinal map with a an initial capacity of 256 elements,
     * and a load factor of 70%.
     */
    public ByteArrayOrdinalMap() {
        this(256);
    }

    /**
     * Creates a byte array ordinal map with an initial capacity of a given size
     * rounded up to the nearest power of two, and a load factor of 70%.
     */
    public ByteArrayOrdinalMap(int size) {
        this(size, null);
    }

    /**
     * Creates a byte array ordinal map with an initial capacity of a given size
     * rounded up to the nearest power of two, a load factor of 70%, and a configurable
     * supplier to determine whether to ignore ordinal threshold breaches.
     *
     * @param size the initial capacity
     * @param ignoreOrdinalThresholdBreach a supplier that returns true to log a warning instead of
     *                                      throwing an exception when SINGLE_PARTITION_ORDINAL_LIMIT is exceeded
     */
    public ByteArrayOrdinalMap(int size, Supplier<Boolean> ignoreOrdinalThresholdBreach) {
        size = bucketSize(size);

        this.freeOrdinalTracker = new FreeOrdinalTracker();
        this.byteData = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        this.pointersAndOrdinals = emptyKeyArray(size);
        this.sizeBeforeGrow = (int) (((float) size) * 0.7); /// 70% load factor
        this.size = 0;
        this.ignoreOrdinalThresholdBreach = ignoreOrdinalThresholdBreach;
    }

    private static int bucketSize(int x) {
        // See Hackers Delight Fig. 3-3
        x = x - 1;
        x = x | (x >> 1);
        x = x | (x >> 2);
        x = x | (x >> 4);
        x = x | (x >> 8);
        x = x | (x >> 16);
        return (x < 256) ? 256 : (x >= 1 << 30) ? 1 << 30 : x + 1;
    }

    public int getOrAssignOrdinal(ByteDataArray serializedRepresentation) {
        return getOrAssignOrdinal(serializedRepresentation, -1);
    }

    /**
     * Adds a sequence of bytes to this map.  If the sequence of bytes has previously been added
     * to this map then its assigned ordinal is returned.
     * If the sequence of bytes has not been added to this map then a new ordinal is assigned
     * and returned.
     * <p>
     * This operation is thread-safe.
     *
     * @param serializedRepresentation the sequence of bytes
     * @param preferredOrdinal the preferred ordinal to assign, if not already assigned to
     * another sequence of bytes and the given sequence of bytes has not previously been added
     * @return the assigned ordinal
     */
    public int getOrAssignOrdinal(ByteDataArray serializedRepresentation, int preferredOrdinal) {
        int hash = HashCodes.hashCode(serializedRepresentation);

        int ordinal = get(serializedRepresentation, hash);
        return ordinal != -1 ? ordinal : assignOrdinal(serializedRepresentation, hash, preferredOrdinal);
    }

    /// acquire the lock before writing.
    private synchronized int assignOrdinal(ByteDataArray serializedRepresentation, int hash, int preferredOrdinal) {
        if (preferredOrdinal < -1 || preferredOrdinal > ORDINAL_MASK) {
            throw new IllegalArgumentException(String.format(
                    "The given preferred ordinal %s is out of bounds and not within the closed interval [-1, %s]",
                    preferredOrdinal, ORDINAL_MASK));
        }
        if (size > sizeBeforeGrow) {
            growKeyArray();
        }

        /// check to make sure that after acquiring the lock, the element still does not exist.
        /// this operation is akin to double-checked locking which is 'fixed' with the JSR 133 memory model in JVM >= 1.5.
        /// Note that this also requires pointersAndOrdinals be volatile so resizes are also visible
        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = pao.length() - 1;
        int bucket = hash & modBitmask;
        long key = pao.get(bucket);

        while (key != EMPTY_BUCKET_VALUE) {
            if (compare(serializedRepresentation, key)) {
                return (int) (key >>> BITS_PER_POINTER);
            }

            bucket = (bucket + 1) & modBitmask;
            key = pao.get(bucket);
        }

        /// the ordinal for this object still does not exist in the list, even after the lock has been acquired.
        /// it is up to this thread to add it at the current bucket position.
        int ordinal = findFreeOrdinal(preferredOrdinal);
        if (ordinal > ORDINAL_MASK) {
            throw new IllegalStateException(String.format(
                    "Ordinal cannot be assigned. The to be assigned ordinal, %s, is greater than the maximum supported ordinal value of %s",
                    ordinal, ORDINAL_MASK));
        }

        if (ordinal > SINGLE_PARTITION_ORDINAL_LIMIT) {
            String message = String.format(
                    "Ordinal %d exceeds the ordinal limit of %d.",
                    ordinal, SINGLE_PARTITION_ORDINAL_LIMIT);
            if (ignoreOrdinalThresholdBreach != null &&
                    Boolean.TRUE.equals(ignoreOrdinalThresholdBreach.get())) {
                LOG.log(Level.WARNING, message);
            } else {
                throw new IllegalStateException(message);
            }
        }

        long pointer = byteData.length();

        VarInt.writeVInt(byteData, (int) serializedRepresentation.length());
        /// Copying might cause a resize to the segmented array held by byteData
        /// A reading thread may observe a null value for a segment during the creation
        /// of a new segments array (see SegmentedByteArray.ensureCapacity).
        serializedRepresentation.copyTo(byteData);
        if (byteData.length() > MAX_BYTE_DATA_LENGTH) {
            throw new IllegalStateException(String.format(
                    "The number of bytes for the serialized representations, %s, is too large and is greater than the maximum of %s bytes",
                    byteData.length(), MAX_BYTE_DATA_LENGTH));
        }

        key = ((long) ordinal << BITS_PER_POINTER) | pointer;

        size++;

        /// this set on the AtomicLongArray has volatile semantics (i.e. behaves like a monitor release).
        /// Any other thread reading this element in the AtomicLongArray will have visibility to all memory writes this thread has made up to this point.
        /// This means the entire byte sequence is guaranteed to be visible to any thread which reads the pointer to that data.
        pao.set(bucket, key);

        return ordinal;
    }

    /**
     * If the preferredOrdinal has not already been used, mark it and use it.  Otherwise,
     * delegate to the FreeOrdinalTracker.
     */
    private int findFreeOrdinal(int preferredOrdinal) {
        if (preferredOrdinal != -1 && unusedPreviousOrdinals.get(preferredOrdinal)) {
            unusedPreviousOrdinals.clear(preferredOrdinal);
            return preferredOrdinal;
        }

        return freeOrdinalTracker.getFreeOrdinal();
    }

    /**
     * Assign a predefined ordinal to a serialized representation.<p>
     * <p>
     * WARNING: THIS OPERATION IS NOT THREAD-SAFE.<p>
     * WARNING: THIS OPERATION WILL NOT UPDATE THE FreeOrdinalTracker.
     *
     * @param serializedRepresentation the serialized representation
     * @param ordinal the ordinal
     */
    public void put(ByteDataArray serializedRepresentation, int ordinal) {
        if (ordinal < 0 || ordinal > ORDINAL_MASK) {
            throw new IllegalArgumentException(String.format(
                    "The given ordinal %s is out of bounds and not within the closed interval [0, %s]",
                    ordinal, ORDINAL_MASK));
        }
        if (size > sizeBeforeGrow) {
            growKeyArray();
        }

        int hash = HashCodes.hashCode(serializedRepresentation);

        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = pao.length() - 1;
        int bucket = hash & modBitmask;
        long key = pao.get(bucket);

        while (key != EMPTY_BUCKET_VALUE) {
            bucket = (bucket + 1) & modBitmask;
            key = pao.get(bucket);
        }

        long pointer = byteData.length();

        VarInt.writeVInt(byteData, (int) serializedRepresentation.length());
        serializedRepresentation.copyTo(byteData);
        if (byteData.length() > MAX_BYTE_DATA_LENGTH) {
            throw new IllegalStateException(String.format(
                    "The number of bytes for the serialized representations, %s, is too large and is greater than the maximum of %s bytes",
                    byteData.length(), MAX_BYTE_DATA_LENGTH));
        }

        key = ((long) ordinal << BITS_PER_POINTER) | pointer;

        size++;

        pao.set(bucket, key);
    }

    public void recalculateFreeOrdinals() {
        BitSet populatedOrdinals = new BitSet();
        AtomicLongArray pao = pointersAndOrdinals;

        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                int ordinal = (int) (key >>> BITS_PER_POINTER);
                populatedOrdinals.set(ordinal);
            }
        }

        recalculateFreeOrdinals(populatedOrdinals);
    }

    public void reservePreviouslyPopulatedOrdinals(BitSet populatedOrdinals) {
        unusedPreviousOrdinals = BitSet.valueOf(populatedOrdinals.toLongArray());

        recalculateFreeOrdinals(populatedOrdinals);
    }

    private void recalculateFreeOrdinals(BitSet populatedOrdinals) {
        freeOrdinalTracker.reset();

        int length = populatedOrdinals.length();
        int ordinal = populatedOrdinals.nextClearBit(0);

        while (ordinal < length) {
            freeOrdinalTracker.returnOrdinalToPool(ordinal);
            ordinal = populatedOrdinals.nextClearBit(ordinal + 1);
        }

        freeOrdinalTracker.setNextEmptyOrdinal(length);
    }

    public BitSet getUnusedPreviousOrdinals() {
        return unusedPreviousOrdinals;
    }

    /**
     * Returns the ordinal for a previously added byte sequence.  If this byte sequence has not been added to the map, then -1 is returned.<p>
     * <p>
     * This is intended for use in the client-side heap-safe double snapshot load.
     *
     * @param serializedRepresentation the serialized representation
     * @return The ordinal for this serialized representation, or -1.
     */
    public int get(ByteDataArray serializedRepresentation) {
        return get(serializedRepresentation, HashCodes.hashCode(serializedRepresentation));
    }

    private int get(ByteDataArray serializedRepresentation, int hash) {
        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = pao.length() - 1;
        int bucket = hash & modBitmask;
        long key = pao.get(bucket);

        // Linear probing to resolve collisions
        // Given the load factor it is guaranteed that the loop will terminate
        // as there will be at least one empty bucket
        // To ensure this is the case it is important that pointersAndOrdinals
        // is read into a local variable and thereafter used, otherwise a concurrent
        // size increase may break this invariant
        while (key != EMPTY_BUCKET_VALUE) {
            if (compare(serializedRepresentation, key)) {
                return (int) (key >>> BITS_PER_POINTER);
            }

            bucket = (bucket + 1) & modBitmask;
            key = pao.get(bucket);
        }

        return -1;
    }

    /**
     * Create an array mapping the ordinals to pointers, so that they can be easily looked up
     * when writing to blob streams.
     */
    public int prepareForWrite() {
        int maxOrdinal = 0;
        AtomicLongArray pao = pointersAndOrdinals;

        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                int ordinal = (int) (key >>> BITS_PER_POINTER);
                if (ordinal > maxOrdinal) {
                    maxOrdinal = ordinal;
                }
            }
        }

        long[] pbo = new long[maxOrdinal + 1];
        Arrays.fill(pbo, -1);

        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                int ordinal = (int) (key >>> BITS_PER_POINTER);
                pbo[ordinal] = key & POINTER_MASK;
            }
        }

        pointersByOrdinal = pbo;
        return maxOrdinal;
    }

    /**
     * Reclaim space in the byte array used in the previous cycle, but not referenced in this cycle.<p>
     * <p>
     * This is achieved by shifting all used byte sequences down in the byte array, then updating
     * the key array to reflect the new pointers and exclude the removed entries.  This is also where ordinals
     * which are unused are returned to the pool.<p>
     *
     * @param usedOrdinals a bit set representing the ordinals which are currently referenced by any image.
     */
    public void compact(ThreadSafeBitSet usedOrdinals, int numShards, boolean focusHoleFillInFewestShards) {
        long[] populatedReverseKeys = new long[size];

        int counter = 0;
        AtomicLongArray pao = pointersAndOrdinals;

        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                populatedReverseKeys[counter++] = key << BITS_PER_ORDINAL | key >>> BITS_PER_POINTER;
            }
        }

        Arrays.sort(populatedReverseKeys);

        SegmentedByteArray arr = byteData.getUnderlyingArray();
        long currentCopyPointer = 0;

        for (int i = 0; i < populatedReverseKeys.length; i++) {
            int ordinal = (int) (populatedReverseKeys[i] & ORDINAL_MASK);

            if (usedOrdinals.get(ordinal)) {
                long pointer = populatedReverseKeys[i] >>> BITS_PER_ORDINAL;
                int length = VarInt.readVInt(arr, pointer);
                length += VarInt.sizeOfVInt(length);

                if (currentCopyPointer != pointer) {
                    arr.copy(arr, pointer, currentCopyPointer, length);
                }

                populatedReverseKeys[i] = populatedReverseKeys[i] << BITS_PER_POINTER | currentCopyPointer;

                currentCopyPointer += length;
            } else {
                freeOrdinalTracker.returnOrdinalToPool(ordinal);
                populatedReverseKeys[i] = EMPTY_BUCKET_VALUE;
            }
        }

        byteData.setPosition(currentCopyPointer);

        if(focusHoleFillInFewestShards && numShards > 1)
            freeOrdinalTracker.sort(numShards);
        else
            freeOrdinalTracker.sort();

        // Reset the array then fill with compacted values
        // Volatile store not required, could use plain store
        // See VarHandles for JDK >= 9
        for (int i = 0; i < pao.length(); i++) {
            pao.lazySet(i, EMPTY_BUCKET_VALUE);
        }
        populateNewHashArray(pao, populatedReverseKeys);
        size = usedOrdinals.cardinality();

        pointersByOrdinal = null;
        unusedPreviousOrdinals = null;
    }

    public long getPointerForData(int ordinal) {
        long pointer = pointersByOrdinal[ordinal] & POINTER_MASK;
        return pointer + VarInt.nextVLongSize(byteData.getUnderlyingArray(), pointer);
    }

    public boolean isReadyForWriting() {
        return pointersByOrdinal != null;
    }

    public boolean isReadyForAddingObjects() {
        return pointersByOrdinal == null;
    }

    public long getDataSize() {
        return byteData.length();
    }

    public int maxOrdinal() {
        int maxOrdinal = -1;
        AtomicLongArray pao = pointersAndOrdinals;

        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                int ordinal = (int) (key >>> BITS_PER_POINTER);
                if (ordinal > maxOrdinal) {
                    maxOrdinal = ordinal;
                }
            }
        }
        return maxOrdinal;
    }

    /**
     * Compare the byte sequence contained in the supplied ByteDataBuffer with the
     * sequence contained in the map pointed to by the specified key, byte by byte.
     */
    private boolean compare(ByteDataArray serializedRepresentation, long key) {
        long position = key & POINTER_MASK;

        int sizeOfData = VarInt.readVInt(byteData.getUnderlyingArray(), position);

        if (sizeOfData != serializedRepresentation.length()) {
            return false;
        }

        position += VarInt.sizeOfVInt(sizeOfData);

        for (int i = 0; i < sizeOfData; i++) {
            if (serializedRepresentation.get(i) != byteData.get(position++)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Resize the ordinal map by increasing its capacity.
     * <p>
     * No action is take if the current capacity is sufficient for the given size.
     * <p>
     * WARNING: THIS OPERATION IS NOT THREAD-SAFE.
     *
     * @param size the size to increase to, rounded up to the nearest power of two.
     */
    public void resize(int size) {
        size = bucketSize(size);

        if (pointersAndOrdinals.length() < size) {
            growKeyArray(size);
        }
    }

    /**
     * Grow the key array.  All of the values in the current array must be re-hashed and added to the new array.
     */
    private void growKeyArray() {
        int newSize = pointersAndOrdinals.length() << 1;
        if (newSize < 0) {
            throw new IllegalStateException("New size computed to grow the underlying array for the map is negative. " +
                    "This is most likely due to the total number of keys added to map has exceeded the max capacity of the keys map can hold. "
                    +
                    "Current array size :" + pointersAndOrdinals.length() + " and size to grow :" + newSize);
        }
        growKeyArray(newSize);
    }

    private void growKeyArray(int newSize) {
        AtomicLongArray pao = pointersAndOrdinals;
        assert (newSize & (newSize - 1)) == 0; // power of 2
        assert pao.length() < newSize;

        AtomicLongArray newKeys = emptyKeyArray(newSize);

        long[] valuesToAdd = new long[size];

        int counter = 0;

        /// do not iterate over these values in the same order in which they appear in the hashed array.
        /// if we do so, we cause large clusters of collisions to appear (because we resolve collisions with linear probing).
        for (int i = 0; i < pao.length(); i++) {
            long key = pao.get(i);
            if (key != EMPTY_BUCKET_VALUE) {
                valuesToAdd[counter++] = key;
            }
        }

        Arrays.sort(valuesToAdd);

        populateNewHashArray(newKeys, valuesToAdd, counter);

        /// 70% load factor
        sizeBeforeGrow = (int) (((float) newSize) * 0.7);
        pointersAndOrdinals = newKeys;
    }

    /**
     * Hash all of the existing values specified by the keys in the supplied long array
     * into the supplied AtomicLongArray.
     */
    private void populateNewHashArray(AtomicLongArray newKeys, long[] valuesToAdd) {
        populateNewHashArray(newKeys, valuesToAdd, valuesToAdd.length);
    }

    private void populateNewHashArray(AtomicLongArray newKeys, long[] valuesToAdd, int length) {
        assert length <= valuesToAdd.length;

        int modBitmask = newKeys.length() - 1;

        for (int i = 0; i < length; i++) {
            long value = valuesToAdd[i];
            if (value != EMPTY_BUCKET_VALUE) {
                int hash = rehashPreviouslyAddedData(value);
                int bucket = hash & modBitmask;
                while (newKeys.get(bucket) != EMPTY_BUCKET_VALUE) {
                    bucket = (bucket + 1) & modBitmask;
                }
                // Volatile store not required, could use plain store
                // See VarHandles for JDK >= 9
                newKeys.lazySet(bucket, value);
            }
        }
    }

    /**
     * Get the hash code for the byte array pointed to by the specified key.
     */
    private int rehashPreviouslyAddedData(long key) {
        long position = key & POINTER_MASK;

        int sizeOfData = VarInt.readVInt(byteData.getUnderlyingArray(), position);
        position += VarInt.sizeOfVInt(sizeOfData);

        return HashCodes.hashCode(byteData.getUnderlyingArray(), position, sizeOfData);
    }

    /**
     * Create an AtomicLongArray of the specified size, each value in the array will be EMPTY_BUCKET_VALUE
     */
    private AtomicLongArray emptyKeyArray(int size) {
        AtomicLongArray arr = new AtomicLongArray(size);
        // Volatile store not required, could use plain store
        // See VarHandles for JDK >= 9
        for (int i = 0; i < arr.length(); i++) {
            arr.lazySet(i, EMPTY_BUCKET_VALUE);
        }
        return arr;
    }

    public ByteDataArray getByteData() {
        return byteData;
    }

    public AtomicLongArray getPointersAndOrdinals() {
        return pointersAndOrdinals;
    }

    public static boolean isPointerAndOrdinalEmpty(long pointerAndOrdinal) {
        return pointerAndOrdinal == EMPTY_BUCKET_VALUE;
    }

    public static long getPointer(long pointerAndOrdinal) {
        return pointerAndOrdinal & POINTER_MASK;
    }

    public static int getOrdinal(long pointerAndOrdinal) {
        return (int) (pointerAndOrdinal >>> BITS_PER_POINTER);
    }

    public long getByteDataLength() {
        return byteData.length();
    }

    public float getLoadFactor() {
        return (float) size / pointersAndOrdinals.length();
    }
}