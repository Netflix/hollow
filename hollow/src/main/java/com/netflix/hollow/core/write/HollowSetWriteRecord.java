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
package com.netflix.hollow.core.write;

import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.IGNORED_HASHES;
import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.MIXED_HASHES;

import com.netflix.hollow.core.memory.ByteDataArray;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.util.LongList;

public class HollowSetWriteRecord implements HollowHashableWriteRecord {

    private final LongList elementsAndHashes;
    private final HashBehavior defaultHashBehavior;

    public HollowSetWriteRecord() {
        this(HashBehavior.MIXED_HASHES);
    }
    
    public HollowSetWriteRecord(HashBehavior defaultHashBehavior) {
        this.elementsAndHashes = new LongList();
        this.defaultHashBehavior = defaultHashBehavior;
    }

    public void addElement(int ordinal) {
        addElement(ordinal, ordinal);
    }

    public void addElement(int ordinal, int hashCode) {
        long elementAndHash = (long)ordinal << 32 | (hashCode & 0xFFFFFFFFL);
        elementsAndHashes.add(elementAndHash);
    }

    @Override
    public void writeDataTo(ByteDataArray buf) {
        writeDataTo(buf, defaultHashBehavior);
    }

    @Override
    public void writeDataTo(ByteDataArray buf, HashBehavior hashBehavior) {
        elementsAndHashes.sort();

        int hashTableSize = HashCodes.hashTableSize(elementsAndHashes.size());
        int bucketMask = hashTableSize - 1; /// hashTableSize is a power of 2.

        VarInt.writeVInt(buf, elementsAndHashes.size());
        int previousOrdinal = 0;

        for(int i=0;i<elementsAndHashes.size();i++) {
            int ordinal = (int)(elementsAndHashes.get(i) >>> 32);
            VarInt.writeVInt(buf, ordinal - previousOrdinal);

            if(hashBehavior != IGNORED_HASHES) {
                int hashCode = (int)elementsAndHashes.get(i);
                if(hashBehavior == MIXED_HASHES)
                    hashCode = HashCodes.hashInt(hashCode);
                int bucketToHashTo = hashCode & bucketMask;
                VarInt.writeVInt(buf, bucketToHashTo);
            }

            previousOrdinal = ordinal;
        }
    }

    @Override
    public void reset() {
        elementsAndHashes.clear();
    }

}
