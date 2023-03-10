package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.FixedLengthMultipleOccurrenceElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ternary Search Tree implementation. Insertion order of elements in TST controls the balancing factor of the tree and
 * in turn space utilization and query performance. The tree would be least balanced with worst performance if the keys
 * are inserted in sorted order, and it would be somewhat balanced with good performance if keys are inserted in random order.
 *
 * This implementation supports duplicate element references from a single tree node. As duplicate elements are encountered,
 * the per-node capacity of referencing elements is dynamically resized. A best effort guess of the measure of duplication
 * in inserted keys is helpful to avoid expensive resize operations.
 *
 * The total node capacity of the tree i.e. max no. of nodes in the tree is pre-allocated at the time of initialization
 * and can not be dynamically resized.
 */
class TST {   // ternary search tree
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
    private long isEndFlagOffset;   // indicates end of a value stored in TST

    private final long maxNodes;
    private final boolean caseSensitive;

    // pre-allocated array to store all nodes in the TST
    // each node contains a data key, links to 3 child nodes, and one bit for indicating if this node marks the end of a value
    private FixedLengthElementArray nodes;

    // dynamically resized array that can store multiple ordinals in the type state corresponding to a node in the TST
    private FixedLengthMultipleOccurrenceElementArray ordinalSet;
    private long indexTracker;
    private long maxDepth;

    /**
     * Create new prefix index. Represents a ternary search tree.
     *
     * @param estimatedMaxNodes estimate number of max nodes that will be created. This is a hard limit.
     * @param estimatedMaxStringDuplicates estimated number string duplicates across all nodes
     * @param maxOrdinalValue  max ordinal that can be referenced
     * @param caseSensitive controls whether indexing and querying should be case sensitive
     * @param memoryRecycler   to reuse arrays from memory pool
     */
    TST(long estimatedMaxNodes, int estimatedMaxStringDuplicates, int maxOrdinalValue, boolean caseSensitive,
        ArraySegmentRecycler memoryRecycler) {
        // best guess, hard limit
        maxNodes = estimatedMaxNodes;
        this.caseSensitive = caseSensitive;

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
        maxDepth = 0;

        // initialize offsets
        leftChildOffset = bitsPerKey;// after first 16 bits in node is first left child offset.
        middleChildOffset = leftChildOffset + bitsForChildPointer;
        rightChildOffset = middleChildOffset + bitsForChildPointer;
        isEndFlagOffset = rightChildOffset + bitsForChildPointer;
    }

    // tell memory recycler to use these long array on next long array request from memory ONLY AFTER swap is called on memory recycler
    void recycleMemory(ArraySegmentRecycler memoryRecycler) {
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

    private boolean isEndNode(long nodeIndex) {
        return nodes.getElementValue((nodeIndex * bitsPerNode) + isEndFlagOffset, 1) == 1;
    }

    private void addOrdinal(long nodeIndex, long ordinal) {
        ordinalSet.addElement(nodeIndex, ordinal);
        nodes.setElementValue((nodeIndex * bitsPerNode) + isEndFlagOffset, 1, 1);
    }

    List<Integer> getOrdinals(long nodeIndex) {
        if (nodeIndex < 0) {
            return Collections.EMPTY_LIST;
        }
        return ordinalSet.getElements(nodeIndex).stream()
                .map(Long::intValue).collect(Collectors.toList());
    }

    /**
     * Insert into ternary search tree for the given key and ordinal.
     * The TST does not support nulls or empty strings.
     */
    void insert(String key, int ordinal) {
        if (key == null) throw new IllegalArgumentException("Null key cannot be indexed");
        if (key.length() == 0) throw new IllegalArgumentException("Empty string cannot be indexed");
        long currentNodeIndex = 0;
        int keyIndex = 0;
        int depth = 0;
        if (!caseSensitive) {
            key = key.toLowerCase();
        }

        while (keyIndex < key.length()) {   // if key is empty string "" it isn't inserted
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
            depth++;
        }
        addOrdinal(currentNodeIndex, ordinal);
        if (depth > maxDepth) {
            maxDepth = depth;
        }
    }

    /**
     * Note that it will match the longest substring in {@code prefix} that was inserted as a key into the tree, and not
     * match partial prefix with partial key. Null values and empty strings are not indexed so those return ORDINAL_NONE (-1).
     *
     * @return index of the node corresponding to longest match with a given prefix, -1 if no match or input was null or empty string
     */
    long findLongestMatch(String prefix) {
        long index = -1;
        if (prefix == null || prefix.length() == 0) {
            return index;
        }
        if (!caseSensitive) {
            prefix = prefix.toLowerCase();
        }

        boolean atRoot = true;
        long currentNodeIndex = 0;
        int keyIndex = 0;

        while (true) {
            if (currentNodeIndex == 0 && !atRoot) break;
            long currentValue = getKey(currentNodeIndex);
            char ch = prefix.charAt(keyIndex);
            if (ch < currentValue) {
                currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Left);
            }
            else if (ch > currentValue) {
                currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Right);
            }
            else {
                if (isEndNode(currentNodeIndex)) {
                    index = currentNodeIndex;   // update longest prefix match
                }
                if (keyIndex == (prefix.length() - 1)) {
                    break;
                }
                currentNodeIndex = getChildIndex(currentNodeIndex, NodeType.Middle);
                keyIndex ++;
            }
            if (atRoot) atRoot = false;
        }
        return index;
    }

    /**
     * This functions checks if the given key exists in the TST.
     *
     * @return index of the node corresponding to the last character of the key, or -1 if not found or input was null or empty string.
     */
    long findNodeWithKey(String key) {
        long index = -1;
        if (key == null || key.length() == 0) {
            return index;
        }
        if (!caseSensitive) {
            key = key.toLowerCase();
        }

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

    boolean contains(String key) {
        long nodeIndex = findNodeWithKey(key);
        return nodeIndex >= 0 && isEndNode(nodeIndex);
    }

    /**
     * Find all the ordinals that match the given prefix. A prefix of empty string "" will return all ordinals indexed
     * in the tree.
     */
    HollowOrdinalIterator findKeysWithPrefix(String prefix) {
        if (prefix == null){
            throw new IllegalArgumentException("Cannot findKeysWithPrefix null prefix");
        }
        if (!caseSensitive) {
            prefix = prefix.toLowerCase();
        }

        final Set<Integer> ordinals = new HashSet<>();
        long currentNodeIndex;
        if (prefix.length() == 0) {
            currentNodeIndex = 0;
        } else {
            currentNodeIndex = findNodeWithKey(prefix);
        }

        if (currentNodeIndex >= 0) {
            if (isEndNode(currentNodeIndex))
                ordinals.addAll(getOrdinals(currentNodeIndex));

            // go to all leaf nodes from current node mid pointer
            Queue<Long> queue = new ArrayDeque<>();
            if (prefix.length() == 0) {
                queue.add(0l);  // root node index
            } else {
                long subTree = getChildIndex(currentNodeIndex, NodeType.Middle);
                if (subTree != 0) {
                    queue.add(subTree);
                }
            }
            while (!queue.isEmpty()) {
                long nodeIndex = queue.remove();
                long left = getChildIndex(nodeIndex, NodeType.Left);
                long mid = getChildIndex(nodeIndex, NodeType.Middle);
                long right = getChildIndex(nodeIndex, NodeType.Right);

                if (isEndNode(nodeIndex)) ordinals.addAll(getOrdinals(nodeIndex));
                if (left != 0) queue.add(left);
                if (mid != 0) queue.add(mid);
                if (right != 0) queue.add(right);
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

    /**
     * Returns the max depth of the prefix tree. The depth depends on insertion order of elements and effects the
     * worst case no. of hops for search. For e.g., a more balanced tree depth closer to log n (n being the no. of nodes)
     * will yield closer to O(log n) search time complexity whereas a balanced tree with depth closer to n would mean
     * upto O(n) search time complexity.
     * @return the max depth of the tree
     */
    long getMaxDepth() {
        return maxDepth;
    }

    /**
     * Returns the no. of empty nodes (capacity minus populated) as a measure of how much pre-allocated space is
     * under utilized.
     * @return no. of populated nodes in prefix tree
     */
    long getEmptyNodes() {
        return maxNodes - indexTracker;
    }

    /**
     * Returns the no. of populated nodes (out of the entire node capcity) in the underlying prefix tree. The no. of
     * nodes required to index a set of records can vary depending on underlying prefix tree implementation and
     * insertion order of records.
     * @return no. of populated nodes in prefix tree
     */
    long getNumNodes() {
        return indexTracker;
    }

    /**
     * Returns the max node capacity of the underlying prefix tree, used as a measure of in-memory space efficiency.
     * An attempt to insert no. of nodes greater than this value will result in failure. The no. of nodes required to
     * index over the a set of records, can vary depending on underlying prefix tree implementation and insertion order
     * of records.
     * @return max node capacity of underlying prefix tree
     */
    long getMaxNodes() {
        return maxNodes;
    }

    /**
     * This is a measure of duplication in the indexed field. Duplication has an adverse effect on memory efficiency, since
     * each node of the tree reserves space to reference multiple records.
     * @return no. of elements that can be referenced from a single node of the tree
     */
    int getMaxElementsPerNode() {
        return ordinalSet.getMaxElementsPerNode();
    }

    /**
     * Returns the approx heap footprint of the prefix tree
     * @return approx heap footprint in bytes
     */
    long approxHeapFootprintInBytes() {
        return nodes.approxHeapFootprintInBytes()
                + ordinalSet.approxHeapFootprintInBytes();
    }
}
