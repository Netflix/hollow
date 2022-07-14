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

import java.util.Arrays;

/**
 * A map of positive primitive ints to positive primitive ints.
 */
public class IntMap {

    private final int keys[];
    private final int values[];
    private int size;

    public IntMap(int numEntries) {
        int arraySize = 1 << 32 - Integer.numberOfLeadingZeros((((numEntries + 1) * 4) / 3) - 1);
        keys = new int[arraySize];
        values = new int[arraySize];
        Arrays.fill(keys, -1);
    }

    public int size() {
        return size;
    }

    public int get(int key) {
        int bucket = hashKey(key) % keys.length;
        while(keys[bucket] != -1) {
            if(keys[bucket] == key)
                return values[bucket];
            bucket++;
            if(bucket == keys.length)
                bucket = 0;
        }
        return -1;
    }

    public void put(int key, int value) {
        int bucket = hashKey(key) % keys.length;
        while(keys[bucket] != -1) {
            if(keys[bucket] == key) {
                values[bucket] = value;
                return;
            }
            bucket++;
            if(bucket == keys.length)
                bucket = 0;
        }

        keys[bucket] = key;
        values[bucket] = value;
        size++;
    }

    private int hashKey(int key) {
        key = ~key + (key << 15);
        key = key ^ (key >>> 12);
        key = key + (key << 2);
        key = key ^ (key >>> 4);
        key = key * 2057;
        key = key ^ (key >>> 16);
        return key & Integer.MAX_VALUE;
    }

    public IntMapEntryIterator iterator() {
        return new IntMapEntryIterator();
    }

    public class IntMapEntryIterator {
        private int currentEntry = -1;

        public boolean next() {
            while(++currentEntry < keys.length) {
                if(keys[currentEntry] != -1)
                    return true;
            }
            return false;
        }

        public int getKey() {
            return keys[currentEntry];
        }

        public int getValue() {
            return values[currentEntry];
        }
    }

}
