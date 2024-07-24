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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class HollowMapWriteRecord implements HollowHashableWriteRecord {
    private static final Logger LOG = Logger.getLogger(HollowMapWriteRecord.class.getName());

    private static final Comparator<HollowMapEntry> MAP_ENTRY_COMPARATOR = new Comparator<HollowMapEntry>() {
        public int compare(HollowMapEntry o1, HollowMapEntry o2) {
            int res = o1.getKeyOrdinal() - o2.getKeyOrdinal();
            if (res == 0) {
                res =  o1.getValueOrdinal() - o2.getValueOrdinal();
            }
            return res;
        }
    };

    private final List<HollowMapEntry> entryList;
    private final HashBehavior defaultHashBehavior;

    public HollowMapWriteRecord() {
        this(HashBehavior.MIXED_HASHES);
    }
    
    public HollowMapWriteRecord(HashBehavior defaultHashBehavior) {
        this.entryList = new ArrayList<HollowMapEntry>();
        this.defaultHashBehavior = defaultHashBehavior;
    }

    public void addEntry(int keyOrdinal, int valueOrdinal) {
        addEntry(keyOrdinal, valueOrdinal, keyOrdinal);
    }

    public void addEntry(int keyOrdinal, int valueOrdinal, int hashCode) {
        entryList.add(new HollowMapEntry(keyOrdinal, valueOrdinal, hashCode));
    }

    @Override
    public void writeDataTo(ByteDataArray buf) {
        writeDataTo(buf, defaultHashBehavior);
    }

    @Override
    public void writeDataTo(ByteDataArray buf, HashBehavior hashBehavior) {
        Collections.sort(entryList, MAP_ENTRY_COMPARATOR);

        VarInt.writeVInt(buf, entryList.size());

        int hashTableSize = HashCodes.hashTableSize(entryList.size());
        int bucketMask = hashTableSize - 1; /// hashTableSize is a power of 2.

        int previousKeyOrdinal = 0;

        for(int i=0;i<entryList.size();i++) {
            HollowMapEntry entry = entryList.get(i);

            VarInt.writeVInt(buf, entry.getKeyOrdinal() - previousKeyOrdinal);
            VarInt.writeVInt(buf, entry.getValueOrdinal());

            if(hashBehavior != IGNORED_HASHES) {
                int hashCode = entry.getHashCode();
                if(hashBehavior == MIXED_HASHES)
                    hashCode = HashCodes.hashInt(hashCode);
                int bucketToHashTo = hashCode & bucketMask;
                VarInt.writeVInt(buf, bucketToHashTo);
            }

            previousKeyOrdinal = entry.getKeyOrdinal();
        }
    }

    @Override
    public void reset() {
        entryList.clear();
    }

    private static class HollowMapEntry {
        private final int keyOrdinal;
        private final int valueOrdinal;
        private final int hashCode;

        public HollowMapEntry(int keyOrdinal, int valueOrdinal, int hashCode) {
            this.keyOrdinal = keyOrdinal;
            this.valueOrdinal = valueOrdinal;
            this.hashCode = hashCode;
        }

        public int getKeyOrdinal() {
            return keyOrdinal;
        }

        public int getValueOrdinal() {
            return valueOrdinal;
        }

        public int getHashCode() {
            return hashCode;
        }
    }

}
