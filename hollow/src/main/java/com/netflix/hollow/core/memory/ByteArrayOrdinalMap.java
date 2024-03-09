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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import sun.awt.Mutex;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLongArray;

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
    Mutex lock = new Mutex();

    private long EMPTY_BUCKET_VALUE = ~0 & ((1 << 22)-1);

    private synchronized void resizeBitsPerOrdinal(int bitsPerOrdinal, int bitsPerPointer) {
        AtomicLongArray pao = pointersAndOrdinals;
        if(bitsPerPointer != bitsPerOrdinal || bitsPerPointer!=22)
            assert false;

        assert bitsPerOrdinal+bitsPerPointer <= 64;

        if(bitsPerPointer!=BITS_PER_POINTER) {
            for (int ptr = 0; ptr < paoSize; ptr++) {
                long key = pao.get(ptr);
                if (key == EMPTY_BUCKET_VALUE)
                    continue;
                int ordinal = getOrdinal(key);
                long dataPtr = getPointer(key);
                long newKey = ((long) ordinal << bitsPerPointer) | dataPtr;
                pao.set(ptr, newKey);
            }
        }

        BITS_PER_ORDINAL = bitsPerOrdinal;
        BITS_PER_POINTER = bitsPerPointer;
        POINTER_MASK = (1L << BITS_PER_POINTER) - 1;
        ORDINAL_MASK = (1L << BITS_PER_ORDINAL) - 1;
        MAX_BYTE_DATA_LENGTH = 1L << BITS_PER_POINTER;
        BITS_PER_BOTH = BITS_PER_ORDINAL + BITS_PER_POINTER;
        EMPTY_BUCKET_VALUE = ~0 & ORDINAL_MASK;
    }

    private int BITS_PER_ORDINAL;
    private int BITS_PER_POINTER;
    private int paoSize = 0;
    private FixedLengthElementArray pointers;
    private FixedLengthElementArray ordinals;
    private int BITS_PER_BOTH;
    private long POINTER_MASK;
    private long ORDINAL_MASK;
    private long MAX_BYTE_DATA_LENGTH;

    /// Thread safety:  We need volatile access semantics to the individual elements in the
    /// pointersAndOrdinals array.
    /// Ordinal is the high 29 bits.  Pointer to byte data is the low 35 bits.
    /// In addition need volatile access to the reference when resize occurs
    private volatile AtomicLongArray pointersAndOrdinals;
    private final ByteDataArray byteData;
    private final FreeOrdinalTracker freeOrdinalTracker;
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
        size = bucketSize(size);

        this.freeOrdinalTracker = new FreeOrdinalTracker();
        this.byteData = new ByteDataArray(WastefulRecycler.DEFAULT_INSTANCE);
        this.pointersAndOrdinals = emptyKeyArray(size);
        this.pointers = emptyKeyArray(size, 22);
        this.ordinals = emptyKeyArray(size, 22);
        paoSize = size;
        this.sizeBeforeGrow = (int) (((float) size) * 0.7); /// 70% load factor
        this.size = 0;

        resizeBitsPerOrdinal(22, 22);
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
        int rv = getOrAssignOrdinal(serializedRepresentation, -1);
        return rv;
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
        if (preferredOrdinal < -1) {
            throw new IllegalArgumentException(String.format(
                    "The given preferred ordinal %s is out of bounds and not within the closed interval [-1, %s]",
                    preferredOrdinal, ORDINAL_MASK));
        }
        while(preferredOrdinal >= ORDINAL_MASK) {
            resizeBitsPerOrdinal(BITS_PER_ORDINAL+1, BITS_PER_POINTER);
        }
        if (size > sizeBeforeGrow) {
            growKeyArray();
        }

        /// check to make sure that after acquiring the lock, the element still does not exist.
        /// this operation is akin to double-checked locking which is 'fixed' with the JSR 133 memory model in JVM >= 1.5.
        /// Note that this also requires pointersAndOrdinals be volatile so resizes are also visible
        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = paoSize - 1;
        int bucket = hash & modBitmask;
        lock.lock();

        while (getOrdinal(bucket) != EMPTY_BUCKET_VALUE) {
            if (compare(serializedRepresentation, getPointer(bucket))) {
                lock.unlock();
                return (int) getOrdinal(bucket);
            }

            bucket = (bucket + 1) & modBitmask;
        }

        /// the ordinal for this object still does not exist in the list, even after the lock has been acquired.
        /// it is up to this thread to add it at the current bucket position.
        int ordinal = findFreeOrdinal(preferredOrdinal);
        while (ordinal >= ORDINAL_MASK) {
            resizeBitsPerOrdinal(BITS_PER_ORDINAL+1, BITS_PER_POINTER);
        }

        long pointer = byteData.length();

        VarInt.writeVInt(byteData, (int) serializedRepresentation.length());
        /// Copying might cause a resize to the segmented array held by byteData
        /// A reading thread may observe a null value for a segment during the creation
        /// of a new segments array (see SegmentedByteArray.ensureCapacity).
        serializedRepresentation.copyTo(byteData);
        while (byteData.length() >= MAX_BYTE_DATA_LENGTH) {
            resizeBitsPerOrdinal(BITS_PER_ORDINAL, BITS_PER_POINTER+1);
        }

        long key = ((long) ordinal << BITS_PER_POINTER) | pointer;

        size++;

        /// this set on the AtomicLongArray has volatile semantics (i.e. behaves like a monitor release).
        /// Any other thread reading this element in the AtomicLongArray will have visibility to all memory writes this thread has made up to this point.
        /// This means the entire byte sequence is guaranteed to be visible to any thread which reads the pointer to that data.
        pao.set(bucket, key);
        setOrdinal(ordinals, bucket, ordinal);
        setPointer(pointers, bucket, pointer);
        lock.unlock();

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
        if (ordinal < 0) {
            throw new IllegalArgumentException(String.format(
                    "The given ordinal %s is out of bounds and not within the closed interval [0, %s]",
                    ordinal, ORDINAL_MASK));
        }
        while(ordinal >= ORDINAL_MASK) {
            resizeBitsPerOrdinal(BITS_PER_ORDINAL+1, BITS_PER_POINTER);
        }
        if (size > sizeBeforeGrow) {
            growKeyArray();
        }

        int hash = HashCodes.hashCode(serializedRepresentation);

        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = paoSize - 1;
        int bucket = hash & modBitmask;

        while (getOrdinal(bucket) != EMPTY_BUCKET_VALUE) {
            bucket = (bucket + 1) & modBitmask;
        }

        long pointer = byteData.length();

        VarInt.writeVInt(byteData, (int) serializedRepresentation.length());
        serializedRepresentation.copyTo(byteData);
        while (byteData.length() >= MAX_BYTE_DATA_LENGTH) {
            resizeBitsPerOrdinal(BITS_PER_ORDINAL, BITS_PER_POINTER+1);
//            throw new IllegalStateException(String.format(
//                    "The number of bytes for the serialized representations, %s, is too large and is greater than the maximum of %s bytes",
//                    byteData.length(), MAX_BYTE_DATA_LENGTH));
        }

        long key = ((long) ordinal << BITS_PER_POINTER) | pointer;

        size++;

        pao.set(bucket, key);
        setOrdinal(ordinals, bucket, ordinal);
        setPointer(pointers, bucket, pointer);
    }

    private void setOrdinal(FixedLengthElementArray ordinalArr, int index, long ordinal) {
        long ordinalIndex = ((long)index)*BITS_PER_ORDINAL;
        ordinalArr.clearElementValue(ordinalIndex, BITS_PER_ORDINAL);
        ordinalArr.setElementValue(ordinalIndex, BITS_PER_ORDINAL, ordinal);
    }

    private void setPointer(FixedLengthElementArray pointerArr, int index, long pointer) {
        long pointerIndex = ((long)index)*BITS_PER_POINTER;
        pointerArr.clearElementValue(pointerIndex, BITS_PER_POINTER);
        pointerArr.setElementValue(pointerIndex, BITS_PER_POINTER, pointer);
    }

    private static int getOrdinal(FixedLengthElementArray ordinals, int index, int BITS_PER_ORDINAL) {
        assert BITS_PER_ORDINAL <= 32;
        return (int)ordinals.getElementValue(((long)index)*BITS_PER_ORDINAL, BITS_PER_ORDINAL);
    }

    private int getOrdinal(int index) {
        return getOrdinal(ordinals, index, BITS_PER_ORDINAL);
    }

    private long getPointer(int index) {
        return pointers.getLargeElementValue(((long)index)*BITS_PER_POINTER, BITS_PER_POINTER);
    }

    public void recalculateFreeOrdinals() {
        BitSet populatedOrdinals = new BitSet();

        for (int i = 0; i < paoSize; i++) {
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                int ordinal = getOrdinal(i);
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
        lock.lock();
        AtomicLongArray pao = pointersAndOrdinals;

        int modBitmask = paoSize - 1;
        int bucket = hash & modBitmask;

        // Linear probing to resolve collisions
        // Given the load factor it is guaranteed that the loop will terminate
        // as there will be at least one empty bucket
        // To ensure this is the case it is important that pointersAndOrdinals
        // is read into a local variable and thereafter used, otherwise a concurrent
        // size increase may break this invariant
        while (getOrdinal(bucket) != EMPTY_BUCKET_VALUE) {
            if (compare(serializedRepresentation, getPointer(bucket))) {
                lock.unlock();
                return (int) getOrdinal(bucket);
            }

            bucket = (bucket + 1) & modBitmask;
        }
        lock.unlock();

        return -1;
    }

    /**
     * Create an array mapping the ordinals to pointers, so that they can be easily looked up
     * when writing to blob streams.
     */
    public void prepareForWrite() {
        int maxOrdinal = 0;
        AtomicLongArray pao = pointersAndOrdinals;

        for (int i = 0; i < paoSize; i++) {
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                if (getOrdinal(i) > maxOrdinal) {
                    maxOrdinal = (int)getOrdinal(i);
                }
            }
        }

        long[] pbo = new long[maxOrdinal + 1];
        Arrays.fill(pbo, -1);

        for (int i = 0; i < paoSize; i++) {
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                pbo[(int)getOrdinal(i)] = getPointer(i);
            }
        }

        pointersByOrdinal = pbo;
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
        long[][] populatedReverseKeys = new long[size][2];

        int counter = 0;
        AtomicLongArray pao = pointersAndOrdinals;


        for (int i = 0; i < paoSize; i++) {
            long key = pao.get(i);
            // xxx ordinal pointer
            long pointer = key & POINTER_MASK;
            long ordinal = key >>> BITS_PER_POINTER;
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                populatedReverseKeys[counter][0] = ordinal;
                populatedReverseKeys[counter][1] = pointer;
                counter++;
            }
        }

        Arrays.sort(populatedReverseKeys, Comparator.comparingLong(a -> a[1]));

        SegmentedByteArray arr = byteData.getUnderlyingArray();
        long currentCopyPointer = 0;

        for (int i = 0; i < populatedReverseKeys.length; i++) {
            int ordinal = (int) populatedReverseKeys[i][0];

            if (usedOrdinals.get(ordinal)) {
                long pointer = populatedReverseKeys[i][1];
                int length = VarInt.readVInt(arr, pointer);
                length += VarInt.sizeOfVInt(length);

                if (currentCopyPointer != pointer) {
                    arr.copy(arr, pointer, currentCopyPointer, length);
                }

                populatedReverseKeys[i][1] = currentCopyPointer;

                currentCopyPointer += length;
            } else {
                freeOrdinalTracker.returnOrdinalToPool(ordinal);
                populatedReverseKeys[i][0] = EMPTY_BUCKET_VALUE;
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
        for (int i = 0; i < paoSize; i++) {
            pao.lazySet(i, EMPTY_BUCKET_VALUE);
            setPointer(pointers, i, EMPTY_BUCKET_VALUE);
            setOrdinal(ordinals, i, EMPTY_BUCKET_VALUE);
        }
        populateNewHashArray(pao, pointers, ordinals, populatedReverseKeys, paoSize);
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

        for (int i = 0; i < paoSize; i++) {
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                if (getOrdinal(i) > maxOrdinal) {
                    maxOrdinal = getOrdinal(i);
                }
            }
        }
        return maxOrdinal;
    }

    /**
     * Compare the byte sequence contained in the supplied ByteDataBuffer with the
     * sequence contained in the map pointed to by the specified key, byte by byte.
     */
    private boolean compare(ByteDataArray serializedRepresentation, long position) {
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
        lock.lock();
        assert (newSize & (newSize - 1)) == 0; // power of 2

        AtomicLongArray newKeys = emptyKeyArray(newSize);
        FixedLengthElementArray newPointers = emptyKeyArray(newSize, BITS_PER_POINTER);
        FixedLengthElementArray newOrdinals = emptyKeyArray(newSize, BITS_PER_ORDINAL);

        long[][] valuesToAdd = new long[size][2];

        int counter = 0;

        /// do not iterate over these values in the same order in which they appear in the hashed array.
        /// if we do so, we cause large clusters of collisions to appear (because we resolve collisions with linear probing).
        for (int i = 0; i < paoSize; i++) {
            if (getOrdinal(i) != EMPTY_BUCKET_VALUE) {
                valuesToAdd[counter][0]=getOrdinal(i);
                valuesToAdd[counter][1]=getPointer(i);
                counter++;
            }
        }

        Arrays.sort(valuesToAdd, Comparator.comparingDouble(a -> a[0]));

        populateNewHashArray(newKeys, newPointers, newOrdinals, valuesToAdd, counter, newSize);

        /// 70% load factor
        sizeBeforeGrow = (int) (((float) newSize) * 0.7);
        pointersAndOrdinals = newKeys;
        pointers = newPointers;
        ordinals = newOrdinals;
        paoSize = newSize;
        lock.unlock();
    }

    /**
     * Hash all of the existing values specified by the keys in the supplied long array
     * into the supplied AtomicLongArray.
     */
    private void populateNewHashArray(AtomicLongArray newKeys, FixedLengthElementArray newPointers,
                                      FixedLengthElementArray newOrdinals, long[][] valuesToAdd, int newKeysLength) {
        populateNewHashArray(newKeys, newPointers, newOrdinals, valuesToAdd, valuesToAdd.length, newKeysLength);
    }

    private void populateNewHashArray(AtomicLongArray newKeys, FixedLengthElementArray newPointers,
                                      FixedLengthElementArray newOrdinals, long[][] valuesToAdd, int valuesLength, int newKeysLength) {
        int modBitmask = newKeysLength - 1;

        for (int i = 0; i < valuesLength; i++) {
            long ordinal = valuesToAdd[i][0];
            long pointer = valuesToAdd[i][1];
            if (ordinal != EMPTY_BUCKET_VALUE) {
                int hash = rehashPreviouslyAddedData(pointer);
                int bucket = hash & modBitmask;
                long bucketVal = getOrdinal(newOrdinals, bucket, BITS_PER_ORDINAL);
                while (bucketVal != EMPTY_BUCKET_VALUE) {
                    bucket = (bucket + 1) & modBitmask;
                    bucketVal = getOrdinal(newOrdinals, bucket, BITS_PER_ORDINAL);
                }
                // Volatile store not required, could use plain store
                // See VarHandles for JDK >= 9
                newKeys.lazySet(bucket, ordinal << BITS_PER_POINTER | pointer);
                setPointer(newPointers, bucket, pointer);
                setOrdinal(newOrdinals, bucket, ordinal);
            }
        }
    }

    /**
     * Get the hash code for the byte array pointed to by the specified key.
     */
    private int rehashPreviouslyAddedData(long position) {
        int sizeOfData = VarInt.readVInt(byteData.getUnderlyingArray(), position);
        position += VarInt.sizeOfVInt(sizeOfData);

        return HashCodes.hashCode(byteData.getUnderlyingArray(), position, sizeOfData);
    }

    /**
     * Create an AtomicLongArray of the specified size, each value in the array will be EMPTY_BUCKET_VALUE
     */
    private AtomicLongArray emptyKeyArray(int size) {
        /*int numBits = size*BITS_PER_BOTH;
        FixedLengthElementArray arr = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numBits);
        for(int i = 0; i < size; i++) {
            arr.setElementValue(i, BITS_PER_BOTH, EMPTY_BUCKET_VALUE);
        }*/
        long[] arr = new long[size];
        Arrays.fill(arr, EMPTY_BUCKET_VALUE);
        return new AtomicLongArray(arr);
    }

    private FixedLengthElementArray emptyKeyArray(int size, int bitsSize) {
        FixedLengthElementArray arr = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, ((long)size+10)*bitsSize);
        for(long i = 0; i < size; i++) {
            arr.clearElementValue(i*bitsSize, bitsSize);
            arr.setElementValue(i*bitsSize, bitsSize, EMPTY_BUCKET_VALUE);
        }
        return arr;
    }

    public ByteDataArray getByteData() {
        return byteData;
    }

    public AtomicLongArray getPointersAndOrdinals() {
        return pointersAndOrdinals;
    }

    public boolean isPointerAndOrdinalEmpty(long pointerAndOrdinal) {
        return pointerAndOrdinal == EMPTY_BUCKET_VALUE;
    }

    public long getPointer(long pointerAndOrdinal) {
        return pointerAndOrdinal & POINTER_MASK;
    }

    public int getOrdinal(long pointerAndOrdinal) {
        return (int) (pointerAndOrdinal >>> BITS_PER_POINTER);
    }

}