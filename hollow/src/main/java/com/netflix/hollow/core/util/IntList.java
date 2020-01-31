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
 * A list of primitive ints
 */
public class IntList {

    private int values[];
    private int size;

    public IntList() {
        this(12);
    }

    public IntList(int initialSize) {
        this.values = new int[initialSize];
    }

    public int get(int index) {
        return values[index];
    }

    public void add(int value) {
        if(values.length == size)
            values = Arrays.copyOf(values, (values.length * 3) / 2);
        values[size++] = value;
    }

    public void addAll(IntList list) {
        for(int i=0;i<list.size;i++)
            add(list.get(i));
    }

    public void set(int index, int value) {
        values[index] = value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    public void sort() {
        Arrays.sort(values, 0, size);
    }
    
    public int binarySearch(int value) {
        return Arrays.binarySearch(values, 0, size, value);
    }

    public void expandTo(int size) {
        if(values.length < size)
            values = Arrays.copyOf(values, size);
        this.size = size;
    }

    public void trim() {
        values = Arrays.copyOf(values, Math.max(size, 12));
    }

    public int[] arrayCopyOfRange(int beginIdx, int endIdx) {
        int arr[] = new int[endIdx - beginIdx];
        System.arraycopy(values, beginIdx, arr, 0, endIdx - beginIdx);
        return arr;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof IntList) {
            IntList that = (IntList)other;
            if(this.size() == that.size()) {
                for(int i=0;i<size;i++) {
                    if(this.get(i) != that.get(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
}
