/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.core.memory.encoding;

import static com.netflix.hollow.core.HollowConstants.HASH_TABLE_MAX_SIZE;

import com.netflix.hollow.core.memory.ArrayByteData;
import com.netflix.hollow.core.memory.ByteData;
import com.netflix.hollow.core.memory.ByteDataBuffer;

public class HashCodes {
    private static final int MURMURHASH_SEED = 0xeab524b9;

    public static int hashCode(ByteDataBuffer data) {
        throw new UnsupportedOperationException();  // is the bug that hash codes changed when we switched from backing byte array to buffer?
        // return hashCode(data.getUnderlyingArray(), 0, (int) data.length());
    }

    public static int hashCode(final String data) {
        if(data == null)
            return -1;
        
        int arrayLen = calculateByteArrayLength(data);
        
        if(arrayLen == data.length()) {
            return hashCode(new ByteData() {
                @Override
                public byte get(long position) {
                    return (byte)(data.charAt((int)position) & 0x7F);
                }
            }, 0, data.length());
        } else {
            byte[] array = createByteArrayFromString(data, arrayLen);

            return hashCode(array);
        }
    }
    
    public static int hashCode(byte[] data) {
        return hashCode(new ArrayByteData(data), 0, data.length);
    }

    private static int calculateByteArrayLength(String data) {
        int length = data.length();
        for(int i=0;i<data.length();i++) {
            if(data.charAt(i) > 0x7F)
                length += VarInt.sizeOfVInt(data.charAt(i)) - 1;
        }
        return length;
    }

    private static byte[] createByteArrayFromString(String data, int arrayLen) {
        byte array[] = new byte[arrayLen];

        int pos = 0;
        for(int i=0;i<data.length();i++) {
            pos = VarInt.writeVInt(array, pos, data.charAt(i));
        }
        return array;
    }
    
    /**
     * MurmurHash3.  Adapted from:<p>
     *
     * https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java<p>
     *
     * On 11/19/2013 the license for this file read:<p>
     *
     *  The MurmurHash3 algorithm was created by Austin Appleby.  This java port was authored by
     *  Yonik Seeley and is placed into the public domain.  The author hereby disclaims copyright
     *  to this source code.
     *  <p>
     *  This produces exactly the same hash values as the final C++
     *  version of MurmurHash3 and is thus suitable for producing the same hash values across
     *  platforms.
     *  <p>
     *  The 32 bit x86 version of this hash should be the fastest variant for relatively short keys like ids.
     *  <p>
     *  Note - The x86 and x64 versions do _not_ produce the same results, as the
     *  algorithms are optimized for their respective platforms.
     *  <p>
     *  See http://github.com/yonik/java_util for future updates to this file.
     *
     * @param data the data to hash
     * @param offset the offset
     * @param len the length
     * @return the hash code
     */
    public static int hashCode(ByteData data, long offset, int len) {

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = MURMURHASH_SEED;
        long roundedEnd = offset + (len & 0xfffffffffffffffcL); // round down to
                                                                // 4 byte block

        for (long i = offset; i < roundedEnd; i += 4) {
            // little endian load order
            int k1 = (data.get(i) & 0xff) | ((data.get(i + 1) & 0xff) << 8) | ((data.get(i + 2) & 0xff) << 16) | (data.get(i + 3) << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17); // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19); // ROTL32(h1,13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch (len & 0x03) {
        case 3:
            k1 = (data.get(roundedEnd + 2) & 0xff) << 16;
            // fallthrough
        case 2:
            k1 |= (data.get(roundedEnd + 1) & 0xff) << 8;
            // fallthrough
        case 1:
            k1 |= (data.get(roundedEnd) & 0xff);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17); // ROTL32(k1,15);
            k1 *= c2;
            h1 ^= k1;
        }

        // finalization
        h1 ^= len;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

    public static int hashLong(long key) {
        key = (~key) + (key << 18);
        key ^= (key >>> 31);
        key *= 21;
        key ^= (key >>> 11);
        key += (key << 6);
        key ^= (key >>> 22);
        return (int) key;
    }

    public static int hashInt(int key) {
        key = ~key + (key << 15);
        key = key ^ (key >>> 12);
        key = key + (key << 2);
        key = key ^ (key >>> 4);
        key = key * 2057;
        key = key ^ (key >>> 16);
        return key;
    }

    /**
     * Determine size of hash table capable of storing the specified number of elements with a load
     * factor applied.
     *
     * @param numElements number of elements to be stored in the table
     * @return size of hash table, always a power of 2
     * @throws IllegalArgumentException when numElements is negative or exceeds
     *                                  {@link com.netflix.hollow.core.HollowConstants#HASH_TABLE_MAX_SIZE}
     */
    public static int hashTableSize(int numElements) throws IllegalArgumentException {
        if (numElements < 0) {
            throw new IllegalArgumentException("cannot be negative; numElements="+numElements);
        } else if (numElements > HASH_TABLE_MAX_SIZE) {
            throw new IllegalArgumentException("exceeds maximum number of buckets; numElements="+numElements);
        }

        if (numElements == 0)
            return 1;
        if (numElements < 3)
            return numElements * 2;

        // Apply load factor to number of elements and determine next
        // largest power of 2 that fits in an int
        int sizeAfterLoadFactor = (int)((long)numElements * 10 / 7);
        int bits = 32 - Integer.numberOfLeadingZeros(sizeAfterLoadFactor - 1);
        return 1 << bits;
    }
}
