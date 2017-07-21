package com.netflix.hollow.core.index;

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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class builds a prefix index. A prefix index can be used to build applications like auto-complete, spell checker.
 */
public class HollowPrefixIndex implements HollowTypeStateListener {

    private HollowReadStateEngine readStateEngine;
    private String type;
    private String fieldPath;

    private Tst prefixIndex;
    private volatile Tst prefixIndexVolatile;
    private ArraySegmentRecycler memoryRecycle;

    private String[] fields;
    private int[] fieldPositions;
    private HollowObjectSchema.FieldType[] fieldTypes;

    private int totalWords;
    private int averageWordLen;
    private int totalValues;
    private int maxOrdinalOfType;

    /**
     * Constructor to create a prefix index for a type using the key defined in one of the fields in that type.
     * The fieldPath could be a reference to another type and path ultimately leads to a string type fieldPath.
     * <p>
     * Example: class "Movie" with a fieldPath String name or class "Movie" with a reference fieldPath of type "Name" which has a string fieldPath "movieName".
     * The former example should provide type as "Movie" and fieldPath path as "name.value".
     * The latter example should provide type as "Movie" and fieldPath path as "name.movieName.value".
     * Note the .value is optional in field path.
     *
     * @param readStateEngine state engine to read data from
     * @param type            type in the read state engine. Ordinals for this type will be returned when queried for a prefix.
     * @param fieldPath       fieldPath should ultimately lead to a string fieldPath, which will be the keys to build the prefix index.
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.fieldPath = fieldPath;

        // create memory recycle for using shared memory pools.
        memoryRecycle = WastefulRecycler.DEFAULT_INSTANCE;
        initialize();
    }

    // initialize field positions and field paths.
    private void initialize() {

        //check arguments
        if (readStateEngine == null || type == null || fieldPath == null)
            throw new IllegalArgumentException("Null arguments received");

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
        totalValues = valueState.getPopulatedOrdinals().cardinality();
        maxOrdinalOfType = valueState.maxOrdinal();
        if (maxOrdinalOfType == 0) maxOrdinalOfType++;// if only 1 record then max ordinal of type will be 0.

        // initialize the prefix index.
        build();
    }

    private void build() {

        // tell memory recycler to use current tst's long arrays next time when long array is requested.
        // note reuse only happens once swap is called and bits are reset
        if (prefixIndex != null) prefixIndex.recycleMemory(memoryRecycle);

        long estimatedNumberOfNodes = estimateNumNodes(totalWords, averageWordLen);
        long estimatedTotalValues = estimateNumberOfValuesPerNode(totalValues);

        Tst tst = new Tst(estimatedNumberOfNodes, estimatedTotalValues, maxOrdinalOfType, memoryRecycle);
        BitSet ordinals = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {
            String[] keys = getKey(ordinal);
            for (String key : keys) {
                tst.insert(key, ordinal);
            }
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        prefixIndex = tst;
        prefixIndexVolatile = tst;
        // safe to return previous long arrays on next request for long array.
        memoryRecycle.swap();
    }

    protected long estimateNumNodes(long totalWords, long averageWordLen) {
        long diff = totalWords - averageWordLen;
        // total words more than average word len
        if (diff > 0) {
            // total symbols 2^16 - unicode is 16 bits. if total words are very large, then approximate
            if (totalWords > (Math.pow(2, 16) * averageWordLen)) {
                return (long) (Math.pow(2, 16) * averageWordLen);
            }
            return totalWords * averageWordLen;// good estimate

        } else {
            return (long) Math.pow(2, 8) * (averageWordLen);
        }
    }

    // ordinal set size -> bitsPerOrdinal, maxNodes, total words per Ordinal
    protected long estimateNumberOfValuesPerNode(long totalValues) {
        if (totalValues > (int) Math.pow(2, 16)) {
            return (long) Math.pow(2, 8);
        }
        return totalValues;
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
    protected String[] getKey(int ordinal) {
        return findKeys(ordinal, type, 0);
    }


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
     *     HollowOrdinalIterator iterator = index.query("a");
     *     int ordinal = iterator.next();
     *     while(ordinal != HollowOrdinalIterator.NO_MORE_ORDINAL) {
     *         // print the result using API
     *     }
     * }</pre>
     *
     * @param prefix query prefix.
     * @return An instance of HollowOrdinalIterator to iterate over ordinals that match the given query.
     */
    public HollowOrdinalIterator query(String prefix) {
        Tst current = this.prefixIndex;
        HollowOrdinalIterator it;
        do {
            it = current.query(prefix);
        } while (current != this.prefixIndexVolatile);
        return it;
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
        // for each ordinal and fieldPath fieldPositionInValueObject -> get key and ordinal of the object
    }

    @Override
    public void removedOrdinal(int ordinal) {
        // for each ordinal and fieldPath fieldPositionInValueObject -> get key and ordinal of the object
    }

    @Override
    public void endUpdate() {
        // pass 1 for delta support - rebuild the tree and swap the new tree with the one that is serving the queries.
        initialize();
        // pass 2 fast creation by re-using index built
        // pass 3 support remove method and dynamic size creation.
    }

    // data structure to match keys (prefix) with ordinalSet.
    // TST is very space efficient compared to a trie, with small trade-off in cost to perform look-ups (specially for misses).
    // TST plus FixedLengthElementArray is super duper compact.
    private static class Tst {

        private enum NodeType {
            Left, Right, Middle
        }

        // each node segment can be thought of as 16 bit key, bits to hold index of its children, bits to get size of ordinal set, and finally bits to point to ordinal set.
        private int bitsPerNode;
        private int bitsPerKey;
        private int bitsForChildPointer;
        private int bitsForOrdinalSetPointer;
        private int bitsForOrdinalSetSize;

        // helper offsets
        private long leftChildOffset;
        private long middleChildOffset;
        private long rightChildOffset;
        private long ordinalSetPointerOffset;
        private long ordinalSetSizeOffset;

        // bits for ordinal sets
        private int bitsPerOrdinal;
        private long bitsPerOrdinalSet;

        private long maxNodes;
        private FixedLengthElementArray nodes, ordinalSet;
        private long indexTracker;// where to create new node

        /**
         * Create new prefix index. Represents a ternary search tree.
         *
         * @param estimateNumNodes       estimate number of max nodes that will created.
         * @param estimatedValuesPerNode estimate number of values per node
         * @param maxOrdinalValue        max ordinal that can be referenced
         * @param memoryRecycler         to reuse arrays from memory pool
         */
        private Tst(long estimateNumNodes, long estimatedValuesPerNode, int maxOrdinalValue, ArraySegmentRecycler memoryRecycler) {

            // best guess
            maxNodes = estimateNumNodes;

            // bits needed to hold a value ordinal
            bitsPerOrdinal = 64 - Long.numberOfLeadingZeros(maxOrdinalValue);
            // total bits to hold all ordinals for a given key
            bitsPerOrdinalSet = estimatedValuesPerNode * bitsPerOrdinal;

            // bits for pointers in a single node:
            bitsPerKey = 16;// key
            bitsForChildPointer = 64 - Long.numberOfLeadingZeros(maxNodes);// a child pointer
            bitsForOrdinalSetPointer = 64 - Long.numberOfLeadingZeros(maxNodes);// ordinal set pointer
            bitsForOrdinalSetSize = 64 - Long.numberOfLeadingZeros(bitsPerOrdinalSet);// ordinal set size pointer

            // bits to represent one node
            bitsPerNode = bitsPerKey + (3 * bitsForChildPointer) + bitsForOrdinalSetSize + bitsForOrdinalSetPointer;

            nodes = new FixedLengthElementArray(memoryRecycler, bitsPerNode * maxNodes);
            ordinalSet = new FixedLengthElementArray(memoryRecycler, bitsPerOrdinalSet * maxNodes);
            indexTracker = 0;

            // initialize offsets
            leftChildOffset = bitsPerKey;// after first 16 bits in node is first left child offset.
            middleChildOffset = leftChildOffset + bitsForChildPointer;
            rightChildOffset = middleChildOffset + bitsForChildPointer;
            ordinalSetPointerOffset = bitsPerKey + (3 * bitsForChildPointer);
            ordinalSetSizeOffset = ordinalSetPointerOffset + bitsForOrdinalSetPointer;
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

        // get child index of the given node, if not set then 0 is returned
        private long getIndex(long currentNode, NodeType nodeType) {
            long offset = getChildOffset(nodeType);
            return nodes.getElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer);
        }

        // create new node at the given index and given key
        private void setDataForNewNode(long index, char ch) {
            // set the key for new node
            nodes.setElementValue(index * bitsPerNode, bitsPerKey, ch);
            // set the ordinal set pointer to use the same index as node index
            nodes.setElementValue((index * bitsPerNode) + ordinalSetPointerOffset, bitsForOrdinalSetPointer, index);
        }

        private void setChildIndex(long currentNode, NodeType nodeType, long indexForNode) {
            long offset = getChildOffset(nodeType);
            nodes.setElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer, indexForNode);
        }

        // get key for the given node index
        private long getKey(long nodeIndex) {
            return nodes.getElementValue(nodeIndex * bitsPerNode, bitsPerKey);
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
                    setDataForNewNode(currentNodeIndex, ch);
                    indexTracker++;
                    if (indexTracker >= maxNodes)
                        throw new IllegalStateException("Index Tracker reached max capacity. Try with larger estimate of number of nodes");
                }

                long keyAtCurrentNode = getKey(currentNodeIndex);
                if (ch < keyAtCurrentNode) {
                    long leftIndex = getIndex(currentNodeIndex, NodeType.Left);
                    if (leftIndex == 0) leftIndex = indexTracker;
                    setChildIndex(currentNodeIndex, NodeType.Left, leftIndex);
                    currentNodeIndex = leftIndex;
                } else if (ch > keyAtCurrentNode) {
                    long rightIndex = getIndex(currentNodeIndex, NodeType.Right);
                    if (rightIndex == 0) rightIndex = indexTracker;
                    setChildIndex(currentNodeIndex, NodeType.Right, rightIndex);
                    currentNodeIndex = rightIndex;
                } else {
                    addOrdinal(currentNodeIndex, ordinal);
                    long midIndex = getIndex(currentNodeIndex, NodeType.Middle);
                    if (midIndex == 0) midIndex = indexTracker;
                    setChildIndex(currentNodeIndex, NodeType.Middle, midIndex);
                    currentNodeIndex = midIndex;
                    keyIndex++;
                }
            }
        }

        private void addOrdinal(long nodeIndex, long ordinal) {
            // find index of ordinal set that current node points to
            long ordinalSetIndex = nodes.getElementValue((nodeIndex * bitsPerNode) + ordinalSetPointerOffset, bitsForOrdinalSetPointer);
            long ordinalSetSize = nodes.getElementValue((nodeIndex * bitsPerNode) + ordinalSetSizeOffset, bitsForOrdinalSetSize);

            // if ordinal set size has reached max capacity then do not add.
            if (ordinalSetSize < (bitsPerOrdinalSet / bitsPerOrdinal)) {
                ordinalSet.setElementValue((ordinalSetIndex * bitsPerOrdinalSet) + (ordinalSetSize * bitsPerOrdinal), bitsPerOrdinal, ordinal);
                // increment set size and re-add for the node
                nodes.setElementValue((nodeIndex * bitsPerNode) + ordinalSetSizeOffset, bitsForOrdinalSetSize, (ordinalSetSize + 1));
            }
        }

        /**
         * Find all the ordinals that match the given prefix.
         *
         * @param prefix
         * @return
         */
        private HollowOrdinalIterator query(String prefix) {
            if (prefix == null) throw new IllegalArgumentException("Cannot query null prefix");
            final Set<Integer> ordinals = new HashSet<>();

            prefix = prefix.toLowerCase();
            boolean matchFound = false;
            boolean atRoot = true;
            long currentNodeIndex = 0;
            int keyIndex = 0;

            while (true) {
                if (currentNodeIndex == 0 && !atRoot) break;
                long currentValue = getKey(currentNodeIndex);
                char ch = prefix.charAt(keyIndex);
                if (ch < currentValue) currentNodeIndex = getIndex(currentNodeIndex, NodeType.Left);
                else if (ch > currentValue) currentNodeIndex = getIndex(currentNodeIndex, NodeType.Right);
                else {
                    if (keyIndex == (prefix.length() - 1)) {
                        matchFound = true;
                        break;
                    }
                    currentNodeIndex = getIndex(currentNodeIndex, NodeType.Middle);
                    keyIndex++;
                }
                if (atRoot) atRoot = false;// after first iteration, this should reset.
            }

            if (matchFound) {

                long ordinalSetIndex = nodes.getElementValue(currentNodeIndex * bitsPerNode + ordinalSetPointerOffset, bitsForOrdinalSetPointer);
                int ordinalSetSize = (int) nodes.getElementValue(currentNodeIndex * bitsPerNode + ordinalSetSizeOffset, bitsForOrdinalSetSize);
                if (ordinalSetSize != 0) {
                    int i = 0;
                    do {
                        int ordinal = (int) ordinalSet.getElementValue((ordinalSetIndex * bitsPerOrdinalSet) + (i * bitsPerOrdinal), bitsPerOrdinal);
                        ordinals.add(ordinal);
                        i++;
                    } while (i < (ordinalSetSize - 1));
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
