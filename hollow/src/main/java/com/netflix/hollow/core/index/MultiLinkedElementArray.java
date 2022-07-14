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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

public class MultiLinkedElementArray {

    private final GrowingSegmentedLongArray listPointersAndSizes;
    private final GrowingSegmentedLongArray linkedElements;

    private int nextNewPointer = 0;
    private long nextLinkedElement = 0;

    public MultiLinkedElementArray(ArraySegmentRecycler memoryRecycler) {
        this.listPointersAndSizes = new GrowingSegmentedLongArray(memoryRecycler);
        this.linkedElements = new GrowingSegmentedLongArray(memoryRecycler);
    }

    public HollowOrdinalIterator iterator(int listIdx) {
        if((listPointersAndSizes.get(listIdx) & Long.MIN_VALUE) != 0)
            return new PivotedElementIterator(listIdx);
        return new LinkedElementIterator(listIdx);
    }

    public void add(int listIdx, int value) {
        long listPtr = listPointersAndSizes.get(listIdx);

        if(listPtr == 0) {
            listPointersAndSizes.set(listIdx, Long.MIN_VALUE | (long) value << 32);
            return;
        }

        if((listPtr & 0xFFFFFFFFL) == 0) {
            listPointersAndSizes.set(listIdx, listPtr | 0x80000000L | value);
            return;
        }

        if((listPtr & Long.MIN_VALUE) != 0) {
            linkedElements.set(nextLinkedElement, listPtr);

            long newLink = (long) value << 32 | nextLinkedElement;

            linkedElements.set(++nextLinkedElement, newLink);

            listPtr = (long) (nextLinkedElement++) << 32 | 3;
            listPointersAndSizes.set(listIdx, listPtr);
        } else {
            long linkedElement = listPtr >> 32;
            long size = listPtr & Integer.MAX_VALUE;

            long newLink = (long) value << 32 | linkedElement;

            linkedElements.set(nextLinkedElement, newLink);

            listPtr = (long) (nextLinkedElement++) << 32 | (size + 1);

            listPointersAndSizes.set(listIdx, listPtr);
        }
    }

    public int numLists() {
        return nextNewPointer;
    }

    public int newList() {
        return nextNewPointer++;
    }

    public int listSize(int listIdx) {
        long listPtr = listPointersAndSizes.get(listIdx);
        if(listPtr == 0)
            return 0;
        if((listPtr & Long.MIN_VALUE) != 0)
            return (listPtr & 0xFFFFFFFFL) == 0 ? 1 : 2;
        return (int) (listPtr & Integer.MAX_VALUE);
    }

    public void destroy() {
        listPointersAndSizes.destroy();
        linkedElements.destroy();
    }

    public class LinkedElementIterator implements HollowOrdinalIterator {

        private int currentElement;
        private boolean lastElement;
        private boolean finished;

        private LinkedElementIterator(int listIdx) {
            this.currentElement = (int) (listPointersAndSizes.get(listIdx) >> 32);
        }

        @Override
        public int next() {
            if(finished)
                return NO_MORE_ORDINALS;
            if(lastElement) {
                int value = (int) (linkedElements.get(currentElement) >>> 32) & Integer.MAX_VALUE;
                finished = true;
                return value;
            } else {
                long element = linkedElements.get(currentElement);
                if(element < 0) {
                    lastElement = true;
                    return (int) element & Integer.MAX_VALUE;
                } else {
                    currentElement = (int) element;
                    return (int) (element >> 32);
                }
            }
        }
    }

    public class PivotedElementIterator implements HollowOrdinalIterator {

        private int listIdx;
        private int currentElement;

        private PivotedElementIterator(int listIdx) {
            this.listIdx = listIdx;
        }

        @Override
        public int next() {
            if(currentElement > 1)
                return NO_MORE_ORDINALS;

            long element = listPointersAndSizes.get(listIdx);

            if(currentElement++ == 0) {
                if((element & 0xFFFFFFFFL) != 0)
                    return (int) element & Integer.MAX_VALUE;
            }

            currentElement++;

            return (int) (element >>> 32) & Integer.MAX_VALUE;
        }

    }

}
