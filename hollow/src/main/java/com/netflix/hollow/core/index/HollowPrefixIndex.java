package com.netflix.hollow.core.index;
/*
*
*  Copyright 2017 Netflix, Inc.
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
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * This class builds a prefix index. A prefix index can be used to build applications like auto-complete, spell checker.
 */
public class HollowPrefixIndex implements HollowTypeStateListener {

    private HollowReadStateEngine readStateEngine;
    private String type;
    private String fieldPath;

    private TST prefixIndex;
    private volatile TST prefixIndexVolatile;
    private ArraySegmentRecycler memoryRecycle;

    private String[] fields;
    private int[] fieldPositions;
    private HollowObjectSchema.FieldType[] fieldTypes;

    private int totalWords;
    private int averageWordLen;
    private int maxOrdinalOfType;

    private boolean buildIndexOnUpdate;

    /**
     * Initializes a new prefix index.
     *
     * @param readStateEngine state engine to read data from
     * @param type            type in the read state engine. Ordinals for this type will be returned when queried for a prefix.
     * @param fieldPath       fieldPath should ultimately lead to a string field.
     *                        The fields in the path could reference another Object, List, Set or a Map.
     *                        The fields should be separated by ".".
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.fieldPath = fieldPath;

        // create memory recycle for using shared memory pools.
        memoryRecycle = WastefulRecycler.DEFAULT_INSTANCE;
        buildIndexOnUpdate = true;
        initialize();
    }

    // initialize field positions and field paths.
    private void initialize() {

        //check arguments
        if (readStateEngine == null) throw new IllegalArgumentException("Read state engine cannot be null");
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (fieldPath == null || fieldPath.isEmpty())
            throw new IllegalArgumentException("fieldPath cannot be null or empty");

        String[] fieldParts = fieldPath.split("\\.");
        List<String> fields = new ArrayList<String>();
        List<Integer> fieldPositions = new ArrayList<Integer>();
        List<HollowObjectSchema.FieldType> fieldTypes = new ArrayList<HollowObjectSchema.FieldType>();

        // traverse through the field path to save field position and types.
        String refType = type;
        String lastRefType = type;
        int i = 0;
        while (i < fieldParts.length || refType != null) {

            HollowSchema schema = readStateEngine.getSchema(refType);
            if (schema == null)
                throw new IllegalArgumentException("Null schema found for field : " + fieldParts[i]);
            HollowSchema.SchemaType schemaType = readStateEngine.getSchema(refType).getSchemaType();

            String fieldName = null;
            int fieldPosition = 0;
            HollowObjectSchema.FieldType fieldType = HollowObjectSchema.FieldType.REFERENCE;

            if (schemaType.equals(HollowSchema.SchemaType.OBJECT)) {

                HollowObjectSchema objectSchema = (HollowObjectSchema) schema;

                // find field position, field name and field type
                if (i >= fieldParts.length) {
                    if (objectSchema.numFields() != 1)
                        throw new IllegalArgumentException("Found a reference in field path with more than one field in schema for type :" + refType);
                    fieldPosition = 0;
                } else {
                    fieldPosition = objectSchema.getPosition(fieldParts[i]);
                    i++; // increment index for field part for next iteration.
                }
                if (fieldPosition < 0)
                    throw new IllegalArgumentException("Could not find a valid field position in type " + refType);
                fieldName = objectSchema.getFieldName(fieldPosition);
                fieldType = objectSchema.getFieldType(fieldPosition);

                // check field type.
                if (fieldType.equals(HollowObjectSchema.FieldType.REFERENCE)) {
                    refType = objectSchema.getReferencedType(fieldName);
                } else if (fieldType.equals(HollowObjectSchema.FieldType.STRING)) {
                    lastRefType = refType;
                    refType = null;
                } else {
                    throw new IllegalArgumentException("field path should contain either a reference field or a string field. " +
                            "Found field :" + fieldName + " with type :" + fieldType.toString());
                }

            } else if (schemaType.equals(HollowSchema.SchemaType.LIST) || schemaType.equals(HollowSchema.SchemaType.SET) || schemaType.equals(HollowSchema.SchemaType.MAP)) {

                if (i >= fieldParts.length || (i < fieldParts.length && !fieldParts[i].equals("element"))) {
                    fieldName = "element";
                } else {
                    fieldName = fieldParts[i];
                    i++; // increment index for field part for next iteration.
                }
                // update ref type to element type.
                if (schema instanceof HollowMapSchema) refType = ((HollowMapSchema) schema).getValueType();
                else refType = ((HollowCollectionSchema) schema).getElementType();
            }

            // update lists
            fields.add(fieldName);
            fieldPositions.add(fieldPosition);
            fieldTypes.add(fieldType);
        }

        this.fields = fields.toArray(new String[fields.size()]);
        this.fieldPositions = new int[fieldPositions.size()];
        for (i = 0; i < fieldPositions.size(); i++) this.fieldPositions[i] = fieldPositions.get(i);
        this.fieldTypes = fieldTypes.toArray(new HollowObjectSchema.FieldType[fieldTypes.size()]);


        // field path should ultimately lead down to a String type
        if (!this.fieldTypes[this.fields.length - 1].equals(HollowObjectSchema.FieldType.STRING))
            throw new IllegalArgumentException("Field path should resolve to a String type");

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

    private synchronized void build() {

        if (!buildIndexOnUpdate) return;
        // tell memory recycler to use current tst's long arrays next time when long array is requested.
        // note reuse only happens once swap is called and bits are reset
        if (prefixIndex != null) prefixIndex.recycleMemory(memoryRecycle);

        long estimatedNumberOfNodes = estimateNumNodes(totalWords, averageWordLen);
        TST tst = new TST(estimatedNumberOfNodes, maxOrdinalOfType, memoryRecycle);
        BitSet ordinals = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {
            String[] keys = getKeys(ordinal);
            if (keys != null) {
                for (String key : keys) {
                    tst.insert(key, ordinal);
                }
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        prefixIndex = tst;
        prefixIndexVolatile = tst;
        // safe to return previous long arrays on next request for long array.
        memoryRecycle.swap();
        buildIndexOnUpdate = false;
    }

    /**
     * This method estimates the total number of nodes that will required to create the index.
     * Override this method if lower/higher estimate is needed compared to the default implementation.
     *
     * @param totalWords
     * @param averageWordLen
     * @return
     */
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
        return findKeys(ordinal, type, 0);
    }

    // recursively find all the keys to index using the field path starting from the given type and field
    private String[] findKeys(int ordinal, String type, int fieldIndex) {

        String[] keys;
        HollowTypeReadState typeReadState = readStateEngine.getTypeState(type);
        HollowSchema.SchemaType schemaType = readStateEngine.getSchema(type).getSchemaType();
        HollowSchema schema = readStateEngine.getSchema(type);

        if (schemaType.equals(HollowSchema.SchemaType.LIST) || schemaType.equals(HollowSchema.SchemaType.SET)) {

            HollowCollectionTypeReadState collectionTypeReadState = (HollowCollectionTypeReadState) typeReadState;
            HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
            String elementType = collectionSchema.getElementType();

            HollowOrdinalIterator it = collectionTypeReadState.ordinalIterator(ordinal);
            List<String> keyList = new ArrayList<>();
            int refOrdinal = it.next();
            while (refOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                String[] refKeys = findKeys(refOrdinal, elementType, fieldIndex + 1);
                for (String refKey : refKeys)
                    keyList.add(refKey);
                refOrdinal = it.next();
            }
            keys = new String[keyList.size()];
            keyList.toArray(keys);

        } else if (schemaType.equals(HollowSchema.SchemaType.OBJECT)) {

            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) typeReadState;

            if (fieldTypes[fieldIndex].equals(HollowObjectSchema.FieldType.REFERENCE)) {
                int refOrdinal = objectTypeReadState.readOrdinal(ordinal, fieldPositions[fieldIndex]);
                String refType = objectSchema.getReferencedType(fieldPositions[fieldIndex]);
                return findKeys(refOrdinal, refType, fieldIndex + 1);
            }

            String key = objectTypeReadState.readString(ordinal, fieldPositions[fieldIndex]).toLowerCase();
            keys = new String[]{key};
        } else {
            // Map type
            HollowMapTypeReadState mapTypeReadState = (HollowMapTypeReadState) typeReadState;
            HollowMapSchema mapSchema = (HollowMapSchema) schema;
            String mapValueType = mapSchema.getValueType();

            HollowMapEntryOrdinalIterator mapEntryIterator = mapTypeReadState.ordinalIterator(ordinal);
            List<String> keyList = new ArrayList<>();
            while (mapEntryIterator.next()) {
                String[] refKeys = findKeys(mapEntryIterator.getValue(), mapValueType, fieldIndex + 1);
                for (String refKey : refKeys)
                    keyList.add(refKey);
            }
            keys = new String[keyList.size()];
            keyList.toArray(keys);
        }
        return keys;
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
    public HollowOrdinalIterator findKeysWithPrefix(String prefix) {
        TST current = this.prefixIndex;
        HollowOrdinalIterator it;
        do {
            it = current.findKeysWithPrefix(prefix);
        } while (current != this.prefixIndexVolatile);
        return it;
    }

    /**
     * Check if the given exists in the index.
     *
     * @param key
     * @return boolean value indicating if the key exists in the index.
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        TST current = this.prefixIndex;
        boolean result;
        do {
            result = current.contains(key);
        } while (current != this.prefixIndexVolatile);
        return result;
    }

    /**
     * Use this method to keep the index updated with delta changes on the read state engine.
     * Remember to call detachFromDeltaUpdates to stop the delta changes.
     * NOTE: Each delta updates creates a new prefix index and swaps the new with current.
     */
    public void listenForDeltaUpdates() {
        readStateEngine.getTypeState(type).addListener(this);
    }

    /**
     * Stop delta updates for this index.
     */
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
        private FixedLengthElementArray ordinalSet;
        private long indexTracker;

        private long size;

        /**
         * Create new prefix index. Represents a ternary search tree.
         *
         * @param estimateNumNodes estimate number of max nodes that will created.
         * @param maxOrdinalValue  max ordinal that can be referenced
         * @param memoryRecycler   to reuse arrays from memory pool
         */
        private TST(long estimateNumNodes, int maxOrdinalValue, ArraySegmentRecycler memoryRecycler) {

            // best guess
            maxNodes = estimateNumNodes;

            // bits for pointers in a single node:
            bitsPerKey = 16;// key
            bitsForChildPointer = 64 - Long.numberOfLeadingZeros(maxNodes);// a child pointer
            bitsPerOrdinal = maxOrdinalValue == 0 ? 1 : 32 - Integer.numberOfLeadingZeros(maxOrdinalValue);

            // bits to represent one node
            bitsPerNode = bitsPerKey + (3 * bitsForChildPointer) + 1;

            nodes = new FixedLengthElementArray(memoryRecycler, bitsPerNode * maxNodes);
            ordinalSet = new FixedLengthElementArray(memoryRecycler, maxNodes * bitsPerOrdinal);
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
            ordinalSet.destroy(memoryRecycler);
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
            if (nodes.getElementValue((nodeIndex * bitsPerNode) + isLeafNodeFlagOffset, 1) == 1)
                return true;
            return false;
        }

        private void addOrdinal(long nodeIndex, long ordinal) {
            long ordinalIndex = nodeIndex * bitsPerOrdinal;
            ordinalSet.setElementValue(ordinalIndex, bitsPerOrdinal, ordinal);
            nodes.setElementValue((nodeIndex * bitsPerNode) + isLeafNodeFlagOffset, 1, 1);
        }

        private int getOrdinal(long nodeIndex) {
            long ordinalIndex = nodeIndex * bitsPerOrdinal;
            return (int) ordinalSet.getElementValue(ordinalIndex, bitsPerOrdinal);
        }

        private long size() {
            return size;
        }

        /**
         * Insert into ternary search tree for the given key and ordinal.
         *
         * @param key
         * @param ordinal
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
            size++;
        }

        /**
         * This functions checks if the given key exists in the trie.
         *
         * @param key
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
            if (nodeIndex >= 0 && isLeafNode(nodeIndex)) return true;
            return false;
        }

        /**
         * Find all the ordinals that match the given prefix.
         *
         * @param prefix
         * @return
         */
        private HollowOrdinalIterator findKeysWithPrefix(String prefix) {
            if (prefix == null) throw new IllegalArgumentException("Cannot findKeysWithPrefix null prefix");
            final Set<Integer> ordinals = new HashSet<>();
            long currentNodeIndex = findNodeWithKey(prefix.toLowerCase());

            if (currentNodeIndex >= 0) {

                if (isLeafNode(currentNodeIndex))
                    ordinals.add(getOrdinal(currentNodeIndex));

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
                            if (isLeafNode(nodeIndex)) ordinals.add(getOrdinal(nodeIndex));
                        }
                        if (left != 0) queue.add(left);
                        if (mid != 0) queue.add(mid);
                        if (right != 0) queue.add(right);
                    }
                }
            }

            HollowOrdinalIterator iterator = new HollowOrdinalIterator() {

                private Iterator<Integer> it = ordinals.iterator();

                @Override
                public int next() {
                    if (it.hasNext()) return it.next();
                    return NO_MORE_ORDINALS;
                }
            };

            return iterator;
        }
    }
}
