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

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.FixedLengthMultipleOccurrenceElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class builds a prefix index. A prefix index can be used to build applications like auto-complete, spell checker.
 */
public class HollowPrefixIndex implements HollowTypeStateListener {

    private final FieldPath fieldPath;
    private final HollowReadStateEngine readStateEngine;
    private final String type;
    private final int estimatedMaxStringDuplicates;

    private volatile TST prefixIndexVolatile;
    private ArraySegmentRecycler memoryRecycle;

    private int totalWords;
    private int averageWordLen;
    private int maxOrdinalOfType;

    private boolean buildIndexOnUpdate;

    /**
     * This constructor defaults the estimatedMaxStringDuplicates to 4. If you expect a large
     * number of duplicate strings across your type, you should provide your own estimate for
     * estimatedMaxStringDuplicates.
     *
     * @param readStateEngine the read state
     * @param type the type name
     * @param fieldPath the field path
     * @see #HollowPrefixIndex(HollowReadStateEngine, String, String)
     */
    @SuppressWarnings("WeakerAccess")
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        // pass in a hardcoded estimate of 4 for now - in the future we could calculate this
        this(readStateEngine, type, fieldPath, 4);
    }

    /**
     * Initializes a new prefix index.
     *
     * @param readStateEngine              state engine to read data from
     * @param type                         type in the read state engine. Ordinals for this type
     *                                     will be returned when queried for a prefix.
     * @param fieldPath                    fieldPath should ultimately lead to a string field.
     *                                     The fields in the path could reference another Object,
     *                                     List, Set or a Map. The fields should be separated by ".".
     * @param estimatedMaxStringDuplicates The estimated number of strings that are duplicated
     *                                     across instances of your type. Note that this means an
     *                                     exactly matching string, not a prefix match. This
     *                                     parameter affects the efficiency of the index building.
     */
    @SuppressWarnings("WeakerAccess")
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath,
            int estimatedMaxStringDuplicates) {

        if (readStateEngine == null) throw new IllegalArgumentException("Read state engine cannot be null");
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (fieldPath == null || fieldPath.isEmpty())
            throw new IllegalArgumentException("fieldPath cannot be null or empty");
        if (estimatedMaxStringDuplicates < 1) {
            throw new IllegalArgumentException("estimatedMaxStringDuplicates cannot be < 1");
        }

        this.readStateEngine = readStateEngine;
        this.type = type;
        this.estimatedMaxStringDuplicates = estimatedMaxStringDuplicates;
        this.fieldPath = new FieldPath(readStateEngine, type, fieldPath);
        if (!this.fieldPath.getLastFieldType().equals(HollowObjectSchema.FieldType.STRING))
            throw new IllegalArgumentException("Field path should lead to a string type");

        // create memory recycle for using shared memory pools.
        memoryRecycle = WastefulRecycler.DEFAULT_INSTANCE;
        buildIndexOnUpdate = true;
        initialize();
    }

    // initialize field positions and field paths.
    private void initialize() {

        String lastRefType = this.fieldPath.getLastRefTypeInPath();

        // get all cardinality to estimate size of array bits needed.
        totalWords = readStateEngine.getTypeState(lastRefType).getPopulatedOrdinals().cardinality();
        averageWordLen = 0;
        double avg = 0;
        HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState(lastRefType);
        BitSet keyBitSet = objectTypeReadState.getPopulatedOrdinals();
        int ordinal = keyBitSet.nextSetBit(0);
        while (ordinal != -1) {
            avg += ((double) objectTypeReadState.readString(ordinal, 0).length()) / ((double) objectTypeReadState.maxOrdinal());
            ordinal = keyBitSet.nextSetBit(ordinal + 1);
        }
        averageWordLen = (int) Math.ceil(avg);

        HollowObjectTypeReadState valueState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess(type);
        maxOrdinalOfType = valueState.maxOrdinal();

        // initialize the prefix index.
        build();
    }

    private void build() {

        if (!buildIndexOnUpdate) return;
        // tell memory recycler to use current tst's long arrays next time when long array is requested.
        // note reuse only happens once swap is called and bits are reset
        TST current = prefixIndexVolatile;
        if (current != null) current.recycleMemory(memoryRecycle);

        long estimatedNumberOfNodes = estimateNumNodes(totalWords, averageWordLen);
        TST tst = new TST(estimatedNumberOfNodes, estimatedMaxStringDuplicates, maxOrdinalOfType,
                memoryRecycle);
        BitSet ordinals = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {
            for (String key : getKeys(ordinal)) {
                tst.insert(key, ordinal);
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }

        prefixIndexVolatile = tst;
        // safe to return previous long arrays on next request for long array.
        memoryRecycle.swap();
        buildIndexOnUpdate = false;
    }

    /**
     * Estimates the total number of nodes that will required to create the index.
     * Override this method if lower/higher estimate is needed compared to the default implementation.
     *
     * @param totalWords the total number of words
     * @param averageWordLen the average word length
     * @return the estimated total number of nodes
     */
    @SuppressWarnings("WeakerAccess")
    protected long estimateNumNodes(long totalWords, long averageWordLen) {
        return totalWords * averageWordLen;
    }

    /**
     * Return the key to index in prefix index. Override this method to support tokens for the key. By default keys are indexed as lower case characters.
     * <pre>{@code
     *     String[] keys = super.getKey(ordinal);
     *     String[] tokens = keys[0].split(" ")
     *     return tokens;
     * }</pre>
     *
     * @param ordinal ordinal of the parent type.
     * @return keys to index.
     */
    protected String[] getKeys(int ordinal) {
        Object[] values = fieldPath.findValues(ordinal);
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            stringValues[i] = ((String) values[i]).toLowerCase();
        }
        return stringValues;
    }

    /**
     * Query the index to find all the ordinals that match the given prefix. Example -
     * <pre>{@code
     *     HollowOrdinalIterator iterator = index.findKeysWithPrefix("a");
     *     int ordinal = iterator.next();
     *     while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINAL) {
     *         // print the result using API
     *     }
     * }</pre>
     * <p>
     * For larger data sets, querying smaller prefixes will be longer than querying for prefixes that are longer.
     *
     * @param prefix findKeysWithPrefix prefix.
     * @return An instance of HollowOrdinalIterator to iterate over ordinals that match the given findKeysWithPrefix.
     */
    @SuppressWarnings("WeakerAccess")
    public HollowOrdinalIterator findKeysWithPrefix(String prefix) {
        TST current;
        HollowOrdinalIterator it;
        do {
            current = prefixIndexVolatile;
            it = current.findKeysWithPrefix(prefix);
        } while (current != this.prefixIndexVolatile);
        return it;
    }

    /**
     * Check if the given key exists in the index.
     *
     * @param key the key
     * @return {@code true} if the key exists, otherwise {@code false}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        TST current;
        boolean result;
        do {
            current = prefixIndexVolatile;
            result = current.contains(key);
        } while (current != this.prefixIndexVolatile);
        return result;
    }

    /**
     * Use this method to keep the index updated with delta changes on the read state engine.
     * Remember to call detachFromDeltaUpdates to stop the delta changes.
     * NOTE: Each delta updates creates a new prefix index and swaps the new with current.
     */
    @SuppressWarnings("WeakerAccess")
    public void listenForDeltaUpdates() {
        readStateEngine.getTypeState(type).addListener(this);
    }

    /**
     * Stop delta updates for this index.
     */
    @SuppressWarnings("WeakerAccess")
    public void detachFromDeltaUpdates() {
        readStateEngine.getTypeState(type).removeListener(this);
    }

    @Override
    public void beginUpdate() {
        // before delta is applied -> no action to be taken
    }

    @Override
    public void addedOrdinal(int ordinal) {
        buildIndexOnUpdate = true;
    }

    @Override
    public void removedOrdinal(int ordinal) {
        buildIndexOnUpdate = true;
    }

    @Override
    public void endUpdate() {
        // pass 1 for delta support - rebuild the tree and swap the new tree with the one that is serving the queries.
        // next pass -  improve the index build time or add support for remove method.
        initialize();
    }

    private static class TST {

        private enum NodeType {
            Left, Right, Middle
        }

        // each node segment can be thought of as 16 bit key and bits to hold index of its children
        private int bitsPerNode;
        private int bitsPerKey;
        private int bitsForChildPointer;
        private int bitsPerOrdinal;

        // helper offsets
        private long leftChildOffset;
        private long middleChildOffset;
        private long rightChildOffset;
        private long isLeafNodeFlagOffset;

        private long maxNodes;
        private FixedLengthElementArray nodes;
        private FixedLengthMultipleOccurrenceElementArray ordinalSet;
        private long indexTracker;

        /**
         * Create new prefix index. Represents a ternary search tree.
         *
         * @param estimatedNumNodes estimate number of max nodes that will created.
         * @param estimatedMaxStringDuplicates estimated number string duplicates across all nodes
         * @param maxOrdinalValue  max ordinal that can be referenced
         * @param memoryRecycler   to reuse arrays from memory pool
         */
        private TST(long estimatedNumNodes, int estimatedMaxStringDuplicates, int maxOrdinalValue,
                ArraySegmentRecycler memoryRecycler) {

            // best guess
            maxNodes = estimatedNumNodes;

            // bits for pointers in a single node:
            bitsPerKey = 16;// key
            bitsForChildPointer = 64 - Long.numberOfLeadingZeros(maxNodes);// a child pointer
            bitsPerOrdinal = maxOrdinalValue == 0 ? 1 : 32 - Integer.numberOfLeadingZeros(maxOrdinalValue);

            // bits to represent one node
            bitsPerNode = bitsPerKey + (3 * bitsForChildPointer) + 1;

            nodes = new FixedLengthElementArray(memoryRecycler, bitsPerNode * maxNodes);
            ordinalSet = new FixedLengthMultipleOccurrenceElementArray(memoryRecycler,
                    maxNodes, bitsPerOrdinal, estimatedMaxStringDuplicates);
            indexTracker = 0;

            // initialize offsets
            leftChildOffset = bitsPerKey;// after first 16 bits in node is first left child offset.
            middleChildOffset = leftChildOffset + bitsForChildPointer;
            rightChildOffset = middleChildOffset + bitsForChildPointer;
            isLeafNodeFlagOffset = rightChildOffset + bitsForChildPointer;
        }

        // tell memory recycler to use these long array on next long array request from memory ONLY AFTER swap is called on memory recycler
        private void recycleMemory(ArraySegmentRecycler memoryRecycler) {
            nodes.destroy(memoryRecycler);
            ordinalSet.destroy();
        }

        private long getChildOffset(NodeType nodeType) {
            long offset;
            if (nodeType.equals(NodeType.Left)) offset = leftChildOffset;
            else if (nodeType.equals(NodeType.Middle)) offset = middleChildOffset;
            else offset = rightChildOffset;
            return offset;
        }

        private long getChildIndex(long currentNode, NodeType nodeType) {
            long offset = getChildOffset(nodeType);
            return nodes.getElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer);
        }

        private void setChildIndex(long currentNode, NodeType nodeType, long indexForNode) {
            long offset = getChildOffset(nodeType);
            nodes.setElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer, indexForNode);
        }

        private void setKey(long index, char ch) {
            nodes.setElementValue(index * bitsPerNode, bitsPerKey, ch);
        }

        private long getKey(long nodeIndex) {
            return nodes.getElementValue(nodeIndex * bitsPerNode, bitsPerKey);
        }

        private boolean isLeafNode(long nodeIndex) {
            return nodes.getElementValue((nodeIndex * bitsPerNode) + isLeafNodeFlagOffset, 1) == 1;
        }

        private void addOrdinal(long nodeIndex, long ordinal) {
            ordinalSet.addElement(nodeIndex, ordinal);
            nodes.setElementValue((nodeIndex * bitsPerNode) + isLeafNodeFlagOffset, 1, 1);
        }

        private Set<Integer> getOrdinals(long nodeIndex) {
            return ordinalSet.getElements(nodeIndex).stream()
                    .map(Long::intValue).collect(Collectors.toSet());
        }

        /**
         * Insert into ternary search tree for the given key and ordinal.
         */
        private void insert(String key, int ordinal) {
            if (key == null) throw new IllegalArgumentException("Null key cannot be indexed");
            long currentNodeIndex = 0;
            int keyIndex = 0;

            while (keyIndex < key.length()) {

                char ch = key.charAt(keyIndex);
                if (getKey(currentNodeIndex) == 0) {
                    setKey(currentNodeIndex, ch);
                    indexTracker++;
                    if (indexTracker >= maxNodes)
                        throw new IllegalStateException("Index Tracker reached max capacity. Try with larger estimate of number of nodes");
                }

                long keyAtCurrentNode = getKey(currentNodeIndex);
                if (ch < keyAtCurrentNode) {
                    long leftIndex = getChildIndex(currentNodeIndex, NodeType.Left);
                    if (leftIndex == 0) leftIndex = indexTracker;
                    setChildIndex(currentNodeIndex, NodeType.Left, leftIndex);
                    currentNodeIndex = leftIndex;
                } else if (ch > keyAtCurrentNode) {
                    long rightIndex = getChildIndex(currentNodeIndex, NodeType.Right);
                    if (rightIndex == 0) rightIndex = indexTracker;
                    setChildIndex(currentNodeIndex, NodeType.Right, rightIndex);
                    currentNodeIndex = rightIndex;
                } else {
                    keyIndex++;
                    if (keyIndex < key.length()) {
                        long midIndex = getChildIndex(currentNodeIndex, NodeType.Middle);
                        if (midIndex == 0) midIndex = indexTracker;
                        setChildIndex(currentNodeIndex, NodeType.Middle, midIndex);
                        currentNodeIndex = midIndex;
                    }
                }
            }
            addOrdinal(currentNodeIndex, ordinal);
        }

        /**
         * This functions checks if the given key exists in the trie.
         *
         * @return index of the node that findNodeWithKey the last character of the key, if not found then returns -1.
         */
        private long findNodeWithKey(String key) {
            long index = -1;

            boolean atRoot = true;
            long currentNodeIndex = 0;
            int keyIndex = 0;

            while (true) {
                if (currentNodeIndex == 0 && !atRoot) break;
                long currentValue = getKey(currentNodeIndex);
                char ch = key.charAt(keyIndex);
                if (ch < currentValue) currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Left);
                else if (ch > currentValue) currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Right);
                else {
                    if (keyIndex == (key.length() - 1)) {
                        index = currentNodeIndex;
                        break;
                    }
                    currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Middle);
                    keyIndex++;
                }
                if (atRoot) atRoot = false;
            }
            return index;
        }

        private boolean contains(String key) {
            long nodeIndex = findNodeWithKey(key);
            return nodeIndex >= 0 && isLeafNode(nodeIndex);
        }

        /**
         * Find all the ordinals that match the given prefix.
         */
        private HollowOrdinalIterator findKeysWithPrefix(String prefix) {
            if (prefix == null) throw new IllegalArgumentException("Cannot findKeysWithPrefix null prefix");
            final Set<Integer> ordinals = new HashSet<>();
            long currentNodeIndex = findNodeWithKey(prefix.toLowerCase());

            if (currentNodeIndex >= 0) {

                if (isLeafNode(currentNodeIndex))
                    ordinals.addAll(getOrdinals(currentNodeIndex));

                // go to all leaf nodes from current node mid pointer
                long subTree = getChildIndex(currentNodeIndex, NodeType.Middle);
                if (subTree != 0) {
                    Queue<Long> queue = new ArrayDeque<>();
                    queue.add(subTree);
                    while (!queue.isEmpty()) {
                        long nodeIndex = queue.remove();
                        long left = getChildIndex(nodeIndex, NodeType.Left);
                        long mid = getChildIndex(nodeIndex, NodeType.Middle);
                        long right = getChildIndex(nodeIndex, NodeType.Right);

                        if (left == 0 && mid == 0 && right == 0) {
                            if (isLeafNode(nodeIndex)) ordinals.addAll(getOrdinals(nodeIndex));
                        }
                        if (left != 0) queue.add(left);
                        if (mid != 0) queue.add(mid);
                        if (right != 0) queue.add(right);
                    }
                }
            }

            return new HollowOrdinalIterator() {
                private Iterator<Integer> it = ordinals.iterator();

                @Override
                public int next() {
                    if (it.hasNext()) return it.next();
                    return NO_MORE_ORDINALS;
                }
            };
        }
    }
}
