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
package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.HASH_TABLE_MAX_SIZE;

import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.Random;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class HashCodesTest {

    private Random rand = new Random();
    
    @Test
    public void testStringHashCode() {
        for(int i=0;i<10000;i++) {
            String str = buildRandomString(true, 25);
            Assert.assertEquals(accurateStringHashCode(str), HashCodes.hashCode(str));
        }
        
        for(int i=0;i<10000;i++) {
            String str = buildRandomString(false, 25);
            Assert.assertEquals(accurateStringHashCode(str), HashCodes.hashCode(str));
        }
    }

    @Test
    public void testHashTableSize() {
        // Current load factor is 10 / 7. If load factor calculation is changed, this test should be updated
        int N;

        try {
            HashCodes.hashTableSize(-1);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("cannot be negative; numElements=-1", ex.getMessage());
        }

        Assert.assertEquals(1, HashCodes.hashTableSize(0));
        Assert.assertEquals(2, HashCodes.hashTableSize(1));
        Assert.assertEquals(4, HashCodes.hashTableSize(2));

        // first integer overflow boundary condition (214_748_364)
        N = Integer.MAX_VALUE / 10;
        Assert.assertEquals(1 << 29, HashCodes.hashTableSize(N));
        Assert.assertEquals(536870912, HashCodes.hashTableSize(N + 1));

        // exceeding maximum hash table size (before load factor)
        N = HASH_TABLE_MAX_SIZE;
        Assert.assertEquals(1073741824, HashCodes.hashTableSize(N));
        try {
            HashCodes.hashTableSize(N + 1);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("exceeds maximum number of buckets; numElements=751619277", ex.getMessage());
        }

        // Note: technically these overflow conditions aren't reachable because max buckets is a lower
        //       threshold. Keeping the assertions to avoid regressions.
        N = (int)((1L<<31) * 7L / 10L);
        try {
            HashCodes.hashTableSize(N);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ex) {}
        try {
            HashCodes.hashTableSize(N + 1);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ex) {}

        // max int
        try {
            HashCodes.hashTableSize(Integer.MAX_VALUE);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException ex) {}
    }

    @Test
    @Ignore
    public void testHashTableSize_exhaustively() {
        int size = HashCodes.hashTableSize(2);
        for (int N=3; N< HASH_TABLE_MAX_SIZE; ++N) {
            int s = HashCodes.hashTableSize(N);
            if (s < size) {
                StringBuilder sb = new StringBuilder();
                sb.append("expected size to grow or stay same; N=");
                sb.append(N);
                sb.append(" previous=");
                sb.append(size);
                sb.append("(~2^");
                sb.append(31 - Integer.numberOfLeadingZeros(size));
                sb.append(") size=");
                sb.append(s);
                sb.append("(~2^");
                sb.append(31 - Integer.numberOfLeadingZeros(s));
                sb.append(')');
                throw new AssertionFailedError(sb.toString());
            }
            size = s;
        }
    }

    private String buildRandomString(boolean includeMultibyteCharacters, int strLen) {
        StringBuilder builder = new StringBuilder();
        
        for(int i=0;i<strLen;i++) {
            builder.append((char)rand.nextInt(includeMultibyteCharacters ? (int)Character.MAX_VALUE : 0x80));
        }
        
        return builder.toString();
    }
    
    
    private int accurateStringHashCode(String str) {
        ByteDataBuffer buf = new ByteDataBuffer(WastefulRecycler.SMALL_ARRAY_RECYCLER);

        for(int i=0;i<str.length();i++) {
            VarInt.writeVInt(buf, str.charAt(i));
        }

        return HashCodes.hashCode(buf.getUnderlyingArray(), 0, (int)buf.length());
    }

}
