/*
 *
 *  Copyright 2016 Netflix, Inc.
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

/*
*
*  Copyright 2013 Netflix, Inc.
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

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLongArray;

/**
*
* This data structure maps byte sequences to ordinals.  This is a hash table.
*
* The <code>pointersAndOrdinals</code> AtomicLongArray contains keys, and the {@link ByteDataBuffer}
* contains values.  Each key has two components.
*
* The high 29 bits in the key represents the ordinal.  The low 35 bits represents the pointer to the start position
* of the byte sequence in the ByteDataBuffer.  Each byte sequence is preceded by a variable-length integer
* (see {@link VarInt}), indicating the length of the sequence.<p>
*
* @author dkoszewnik
*
*/
public class ByteArrayOrdinalMap {

   private static final long EMPTY_BUCKET_VALUE = -1L;

   private static final int BITS_PER_ORDINAL = 29;
   private static final int BITS_PER_POINTER = Long.SIZE - BITS_PER_ORDINAL;
   private static final long POINTER_MASK = (1L << BITS_PER_POINTER) - 1;
   private static final long ORDINAL_MASK = (1L << BITS_PER_ORDINAL) - 1;

   /// Thread safety:  We need volatile access semantics to the individual elements in the
   /// pointersAndOrdinals array.
   /// Ordinal is the high 29 bits.  Pointer to byte data is the low 35 bits.
   private AtomicLongArray pointersAndOrdinals;
   private final ByteDataBuffer byteData;
   private final FreeOrdinalTracker freeOrdinalTracker;
   private int size;
   private int sizeBeforeGrow;

   private BitSet unusedPreviousOrdinals;

   private long pointersByOrdinal[];


   public ByteArrayOrdinalMap() {
       this.freeOrdinalTracker = new FreeOrdinalTracker();
       this.byteData = new ByteDataBuffer(WastefulRecycler.DEFAULT_INSTANCE);
       this.pointersAndOrdinals = emptyKeyArray(256);
       this.sizeBeforeGrow = 179; /// 70% load factor
       this.size = 0;
   }

   public int getOrAssignOrdinal(ByteDataBuffer serializedRepresentation) {
       return getOrAssignOrdinal(serializedRepresentation, -1);
   }

   /**
    * Add a sequence of bytes to this map.  If the sequence of bytes has already been added to this map, return the originally assigned ordinal.
    * If the sequence of bytes has not been added to this map, assign and return a new ordinal.  This operation is thread-safe.
    */
   public int getOrAssignOrdinal(ByteDataBuffer serializedRepresentation, int preferredOrdinal) {
       int hash = HashCodes.hashCode(serializedRepresentation);

       int modBitmask = pointersAndOrdinals.length() - 1;
       int bucket = hash & modBitmask;
       long key = pointersAndOrdinals.get(bucket);

       /// linear probing to resolve collisions.
       while(key != EMPTY_BUCKET_VALUE) {
           if(compare(serializedRepresentation, key)) {
               return (int)(key >>> BITS_PER_POINTER);
           }

           bucket = (bucket + 1) & modBitmask;
           key = pointersAndOrdinals.get(bucket);
       }

       return assignOrdinal(serializedRepresentation, hash, preferredOrdinal);
   }

   /// acquire the lock before writing.
   private synchronized int assignOrdinal(ByteDataBuffer serializedRepresentation, int hash, int preferredOrdinal) {
       if(size > sizeBeforeGrow)
           growKeyArray();

       /// check to make sure that after acquiring the lock, the element still does not exist.
       /// this operation is akin to double-checked locking which is 'fixed' with the JSR 133 memory model in JVM >= 1.5.
       int modBitmask = pointersAndOrdinals.length() - 1;
       int bucket = hash & modBitmask;
       long key = pointersAndOrdinals.get(bucket);

       while(key != EMPTY_BUCKET_VALUE) {
           if(compare(serializedRepresentation, key)) {
               return (int)(key >>> BITS_PER_POINTER);
           }

           bucket = (bucket + 1) & modBitmask;
           key = pointersAndOrdinals.get(bucket);
       }

       /// the ordinal for this object still does not exist in the list, even after the lock has been acquired.
       /// it is up to this thread to add it at the current bucket position.
       int ordinal = findFreeOrdinal(preferredOrdinal);
       long pointer = byteData.length();

       VarInt.writeVInt(byteData, (int)serializedRepresentation.length());
       serializedRepresentation.copyTo(byteData);

       key = ((long)ordinal << BITS_PER_POINTER) | pointer;

       size++;

       /// this set on the AtomicLongArray has volatile semantics (i.e. behaves like a monitor release).
       /// Any other thread reading this element in the AtomicLongArray will have visibility to all memory writes this thread has made up to this point.
       /// This means the entire byte sequence is guaranteed to be visible to any thread which reads the pointer to that data.
       pointersAndOrdinals.set(bucket, key);

       return ordinal;
   }

   /**
    * If the preferredOrdinal has not already been used, mark it and use it.  Otherwise,
    * delegate to the FreeOrdinalTracker.
    */
   private int findFreeOrdinal(int preferredOrdinal) {
       if(preferredOrdinal != -1 && unusedPreviousOrdinals.get(preferredOrdinal)) {
           unusedPreviousOrdinals.clear(preferredOrdinal);
           return preferredOrdinal;
       }

       return freeOrdinalTracker.getFreeOrdinal();
   }

   /**
    * Assign a predefined ordinal to a serialized representation.<p>
    *
    * WARNING: THIS OPERATION IS NOT THREAD-SAFE.<p>
    * WARNING: THIS OPERATION WILL NOT UPDATE THE FreeOrdinalTracker.<p>
    */
   public void put(ByteDataBuffer serializedRepresentation, int ordinal) {
       if(size > sizeBeforeGrow)
           growKeyArray();

       int hash = HashCodes.hashCode(serializedRepresentation);

       int modBitmask = pointersAndOrdinals.length() - 1;
       int bucket = hash & modBitmask;
       long key = pointersAndOrdinals.get(bucket);

       while(key != EMPTY_BUCKET_VALUE) {
           bucket = (bucket + 1) & modBitmask;
           key = pointersAndOrdinals.get(bucket);
       }

       long pointer = byteData.length();

       VarInt.writeVInt(byteData, (int)serializedRepresentation.length());
       serializedRepresentation.copyTo(byteData);

       key = ((long)ordinal << BITS_PER_POINTER) | pointer;

       size++;

       pointersAndOrdinals.set(bucket, key);
   }

   public void recalculateFreeOrdinals() {
       BitSet populatedOrdinals = new BitSet();

       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               int ordinal = (int)(key >>> BITS_PER_POINTER);
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

       while(ordinal < length) {
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
    *
    * This is intended for use in the client-side heap-safe double snapshot load.
    *
    * @param serializedRepresentation
    * @return The ordinal for this serialized representation, or -1.
    */
   public int get(ByteDataBuffer serializedRepresentation) {
       int hash = HashCodes.hashCode(serializedRepresentation);

       int modBitmask = pointersAndOrdinals.length() - 1;
       int bucket = hash & modBitmask;
       long key = pointersAndOrdinals.get(bucket);

       /// linear probing to resolve collisions.
       while(key != EMPTY_BUCKET_VALUE) {
           if(compare(serializedRepresentation, key)) {
               return (int)(key >>> BITS_PER_POINTER);
           }

           bucket = (bucket + 1) & modBitmask;
           key = pointersAndOrdinals.get(bucket);
       }

       return -1;
   }

   /**
    * Create an array mapping the ordinals to pointers, so that they can be easily looked up
    * when writing to blob streams.
    *
    */
   public void prepareForWrite() {
       int maxOrdinal = 0;

       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               int ordinal = (int)(key >>> BITS_PER_POINTER);
               if(ordinal > maxOrdinal)
                   maxOrdinal = ordinal;
           }
       }

       pointersByOrdinal = new long[maxOrdinal + 1];
       Arrays.fill(pointersByOrdinal, -1);

       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               int ordinal = (int)(key >>> BITS_PER_POINTER);
               pointersByOrdinal[ordinal] = key & POINTER_MASK;
           }
       }
   }

   /**
    * Reclaim space in the byte array used in the previous cycle, but not referenced in this cycle.<p>
    *
    * This is achieved by shifting all used byte sequences down in the byte array, then updating
    * the key array to reflect the new pointers and exclude the removed entries.  This is also where ordinals
    * which are unused are returned to the pool.<p>
    *
    * @param usedOrdinals a bit set representing the ordinals which are currently referenced by any image.
    */
   public void compact(ThreadSafeBitSet usedOrdinals) {
       long populatedReverseKeys[] = new long[size];

       int counter = 0;

       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               populatedReverseKeys[counter++] = key << BITS_PER_ORDINAL | key >>> BITS_PER_POINTER;
           }
       }

       Arrays.sort(populatedReverseKeys);

       SegmentedByteArray arr = byteData.getUnderlyingArray();
       long currentCopyPointer = 0;

       for(int i=0;i<populatedReverseKeys.length;i++) {
           int ordinal = (int)(populatedReverseKeys[i] & ORDINAL_MASK);

           if(usedOrdinals.get(ordinal)) {
               long pointer = populatedReverseKeys[i] >>> BITS_PER_ORDINAL;
               int length = VarInt.readVInt(arr, pointer);
               length += VarInt.sizeOfVInt(length);

               if(currentCopyPointer != pointer)
                   arr.copy(arr, pointer, currentCopyPointer, length);

               populatedReverseKeys[i] = populatedReverseKeys[i] << BITS_PER_POINTER | currentCopyPointer;

               currentCopyPointer += length;
           } else {
               freeOrdinalTracker.returnOrdinalToPool(ordinal);
               populatedReverseKeys[i] = EMPTY_BUCKET_VALUE;
           }
       }

       byteData.setPosition(currentCopyPointer);
       freeOrdinalTracker.sort();

       for(int i=0;i<pointersAndOrdinals.length();i++) {
           pointersAndOrdinals.set(i, EMPTY_BUCKET_VALUE);
       }

       populateNewHashArray(pointersAndOrdinals, populatedReverseKeys);
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
       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               int ordinal = (int)(pointersAndOrdinals.get(i) >>> BITS_PER_POINTER);
               if(ordinal > maxOrdinal)
                   maxOrdinal = ordinal;
           }
       }
       return maxOrdinal;
   }

   /**
    * Compare the byte sequence contained in the supplied ByteDataBuffer with the
    * sequence contained in the map pointed to by the specified key, byte by byte.
    */
   private boolean compare(ByteDataBuffer serializedRepresentation, long key) {
       long position = key & POINTER_MASK;

       int sizeOfData = VarInt.readVInt(byteData.getUnderlyingArray(), position);

       if(sizeOfData != serializedRepresentation.length())
           return false;

       position += VarInt.sizeOfVInt(sizeOfData);

       for(int i=0;i<sizeOfData;i++) {
           if(serializedRepresentation.get(i) != byteData.get(position++))
               return false;
       }

       return true;
   }

   /**
    * Grow the key array.  All of the values in the current array must be re-hashed and added to the new array.
    */
   private void growKeyArray() {
       int newSize = pointersAndOrdinals.length() * 2;
       if (newSize < 0) {
           throw new IllegalStateException("New size computed to grow the underlying array for the map is negative. " +
                   "This is most likely due to the total number of keys added to map has exceeded the max capacity of the keys map can hold. " +
                   "Current array size :" + pointersAndOrdinals.length() + " and size to grow :" + newSize);
       }
       AtomicLongArray newKeys = emptyKeyArray(pointersAndOrdinals.length() * 2);

       long valuesToAdd[] = new long[size];

       int counter = 0;

       /// do not iterate over these values in the same order in which they appear in the hashed array.
       /// if we do so, we cause large clusters of collisions to appear (because we resolve collisions with linear probing).
       for(int i=0;i<pointersAndOrdinals.length();i++) {
           long key = pointersAndOrdinals.get(i);
           if(key != EMPTY_BUCKET_VALUE) {
               valuesToAdd[counter++] = key;
           }
       }

       Arrays.sort(valuesToAdd);

       populateNewHashArray(newKeys, valuesToAdd);

       /// 70% load factor
       sizeBeforeGrow = (int) (((float) newKeys.length()) * 0.7);
       pointersAndOrdinals = newKeys;
   }

   /**
    * Hash all of the existing values specified by the keys in the supplied long array
    * into the supplied AtomicLongArray.
    */
   private void populateNewHashArray(AtomicLongArray newKeys, long[] valuesToAdd) {
       int modBitmask = newKeys.length() - 1;

       for(int i=0;i<valuesToAdd.length;i++) {
           if(valuesToAdd[i] != EMPTY_BUCKET_VALUE) {
               int hash = rehashPreviouslyAddedData(valuesToAdd[i]);
               int bucket = hash & modBitmask;
               while(newKeys.get(bucket) != EMPTY_BUCKET_VALUE)
                   bucket = (bucket + 1) & modBitmask;
               newKeys.set(bucket, valuesToAdd[i]);
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
       for(int i=0;i<arr.length();i++) {
           arr.set(i, EMPTY_BUCKET_VALUE);
       }
       return arr;
   }

   public ByteDataBuffer getByteData() {
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
       return (int)(pointerAndOrdinal >>> BITS_PER_POINTER);
   }

}