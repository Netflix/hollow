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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import java.util.Random;
import org.junit.Assert;
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
