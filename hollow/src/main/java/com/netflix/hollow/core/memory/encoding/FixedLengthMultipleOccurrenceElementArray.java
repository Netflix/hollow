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

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

/**
 * This class stores multiple instances of a fixed bit width element in a list of nodes. For
 * example, it can store multiple 6 bit elements at different indices of the list of nodes.
 *
 * Under the hood, it uses a {@link FixedLengthElementArray} in order to implement compact storage
 * that allows inserting multiple instances of the fixed length elements. It maintains at least
 * enough space to hold up to the max number of occurrences of each element. This means that if
 * there are 8 elements at one node index, it will contain enough space to store at least 8 elements
 * at each index.
 *
 * Running out of space at any index triggers a relatively expensive resize operation, where we
 * create storage of a multiple (currently 1.5x) of the previous storage and copy items over, but
 * this can be alleviated by passing in a better guess for max elements per node.
 * Note that this class is currently designed to be used for a relatively small number for
 * bitsPerElement - it will not work for bitsPerElement greater than 60.
 */
public class FixedLengthMultipleOccurrenceElementArray {
    private static final double RESIZE_MULTIPLE = 1.5;
    private static final long NO_ELEMENT = 0L;

    private final ArraySegmentRecycler memoryRecycler;
    private final FixedLengthElementArray nodesWithOrdinalZero;
    private final int bitsPerElement;
    private final long elementMask;
    private final long numNodes;

    private volatile FixedLengthElementArray storage;
    private volatile int maxElementsPerNode;

    public FixedLengthMultipleOccurrenceElementArray(ArraySegmentRecycler memoryRecycler,
            long numNodes, int bitsPerElement, int maxElementsPerNodeEstimate) {
        nodesWithOrdinalZero = new FixedLengthElementArray(memoryRecycler, numNodes);
        storage = new FixedLengthElementArray(
                memoryRecycler, numNodes * bitsPerElement * maxElementsPerNodeEstimate);
        this.memoryRecycler = memoryRecycler;
        this.bitsPerElement = bitsPerElement;
        this.elementMask = (1L << bitsPerElement) - 1;
        this.numNodes = numNodes;
        this.maxElementsPerNode = maxElementsPerNodeEstimate;
    }

    /**
     * This method adds an element at nodeIndex. Note that this does not check for duplicates; if
     * the element already exists, another instance of it will be added.
     * This method is not thread-safe - you cannot call this method concurrently with itself or with
     * {@link #getElements}.
     *
     * @param nodeIndex the node index
     * @param element the element to add
     */
    public void addElement(long nodeIndex, long element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return a list of elements at the specified node index. The returned list may contain
     * duplicates.
     * This method not thread-safe - the caller must ensure that no one calls {@link #addElement}
     * concurrently with this method, but calling this method concurrently with itself is safe.
     *
     * @param nodeIndex the node index
     * @return a list of element at the node index
     */
    public List<Long> getElements(long nodeIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * A destructor function - call to free up the underlying memory.
     */
    public void destroy() {
        storage.destroy(memoryRecycler);
    }

    /**
     * Resize the underlying storage to a multiple of what it currently is. This method is not
     * thread-safe.
     */
    private void resizeStorage() {
        throw new UnsupportedOperationException();
    }
}
