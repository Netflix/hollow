package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * This class builds a prefix index. A prefix index can be used to build applications like auto-complete, spell checker.
 */
public class HollowPrefixIndex implements HollowTypeStateListener {

    private HollowReadStateEngine readStateEngine;

    private String type;
    private String fieldPath;
    private Tst prefixIndex;

    private String[] fields;
    private int[] fieldPositions;
    private HollowObjectSchema.FieldType[] fieldTypes;

    /**
     * Constructor to create a prefix index for a type using the key defined in one of the fields in that type.
     * The fieldPath could be a reference to another type and path ultimately leads to a string type fieldPath.
     * <p>
     * Example: class "Movie" with a fieldPath String name or class "Movie" with a reference fieldPath of type "Name" which has a string fieldPath "movieName".
     * The former example should provide type as "Movie" and fieldPath path as "name.value".
     * The latter example should provide type as "Movie" and fieldPath path as "name.movieName.value".
     * Note if using HollowInline annotation appending .value to field path is not needed.
     *
     * @param readStateEngine state engine to read data from
     * @param type            type in the read state engine. Ordinals for this type will be returned when queried for a prefix.
     * @param fieldPath       fieldPath should ultimately lead to a string fieldPath, which will be the keys to build the prefix index.
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String fieldPath) {
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.fieldPath = fieldPath;
        initialize();
        build();
    }

    // initialize field positions and field paths.
    private void initialize() {
        // split the field path with "." char
        fields = fieldPath.split("\\.");

        // arrays to save data for field path
        fieldPositions = new int[fields.length];
        fieldTypes = new HollowObjectSchema.FieldType[fields.length];

        // traverse through the field path to save field position and types.
        Set<String> typeSeen = new HashSet<>();
        String tempType = type;
        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess(type);
        for (int i = 0; i < fields.length; i++) {
            typeSeen.add(tempType);
            HollowSchema schema = readStateEngine.getSchema(tempType);

            if (schema == null) throw new IllegalArgumentException("Null schema found for type " + tempType);
            if (!schema.getSchemaType().equals(HollowSchema.SchemaType.OBJECT))
                throw new IllegalArgumentException("Field path should be defined in type Objects only, " +
                        "found field " + fields[i] + " in path defined in schema " + schema.getSchemaType().toString());

            HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
            int position = objectSchema.getPosition(fields[i]);
            if (position < 0)
                throw new IllegalArgumentException("Invalid field position for field " + fields[i] + " in type " + tempType);

            HollowObjectSchema.FieldType fieldType = objectSchema.getFieldType(fields[i]);
            if (!(fieldType.equals(HollowObjectSchema.FieldType.REFERENCE) || fieldType.equals(HollowObjectSchema.FieldType.STRING)))
                throw new IllegalArgumentException("Invalid field type for the field " + fields[i]);

            fieldPositions[i] = position;
            fieldTypes[i] = fieldType;
            if (fieldType.equals(HollowObjectSchema.FieldType.REFERENCE)) {
                tempType = objectSchema.getReferencedType(fields[i]);
                if (typeSeen.contains(tempType)) throw new IllegalStateException("Circular reference found in fieldPath for type " + tempType);
            }
            readState = (HollowObjectTypeReadState) readStateEngine.getTypeState(tempType);
        }

        int cardinalityOfKeyField = readState.getPopulatedOrdinals().cardinality();

        HollowObjectTypeReadState valueState = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess(type);
        int cardinalityOfType = valueState.getPopulatedOrdinals().cardinality();
        int maxOrdinalOfType = valueState.maxOrdinal();

        // initialize the prefix index.
        this.prefixIndex = new Tst(maxOrdinalOfType, cardinalityOfType, cardinalityOfKeyField);
    }

    private String getKey(int ordinal) {

        HollowObjectTypeReadState readState = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);
        int refOrdinal = ordinal;
        int position = 0;
        String tempType = type;

        for (int i = 0; i < fields.length; i++) {
            position = fieldPositions[i];
            readState = (HollowObjectTypeReadState) readStateEngine.getTypeState(tempType);

            if (fieldTypes[i].equals(HollowObjectSchema.FieldType.REFERENCE)) {
                tempType = readState.getSchema().getReferencedType(fields[i]);
                refOrdinal = readState.readOrdinal(refOrdinal, position);
            }
        }

        return readState.readString(refOrdinal, position);
    }

    private void build() {
        BitSet ordinals = readStateEngine.getTypeState(type).getPopulatedOrdinals();
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {
            String key = getKey(ordinal);
            put(key, ordinal);
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
    }

    /**
     * Add the given key to the prefix index. To have a custom tokens of the key, override this method.
     *
     * @param key
     * @param ordinal
     */
    protected void put(String key, int ordinal) {
        prefixIndex.insert(key, ordinal);
    }

    /**
     * Query the index to find all the ordinals that match the given prefix.
     *
     * @param prefix
     * @return ordinals of the parent object.
     */
    public Set<Integer> query(String prefix) {
        return prefixIndex.query(prefix);
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
        // after delta is applied -> no action to be taken
    }

    // data structure to match keys (prefix) with ordinalSet.
    // TST is very space efficient compared to a trie, with minute trade-off in cost to perform look-ups.
    // TST plus FixedLengthElementArray is super duper compact.
    private static class Tst {

        enum NodeType {
            Left, Right, Middle
        }

        // each node segment can be thought of as 16 bit key, bits to hold index of its children, bits to get size of ordinal set, and finally bits to point to ordinal set.
        private int bitsPerNode;
        private int bitsPerKey;
        private int bitsForChildPointer;
        private int bitsForOrdinalSetPointer;
        private int bitsForOrdinalSetSize;

        // helper offsets
        private int leftChildOffset;
        private int middleChildOffset;
        private int rightChildOffset;
        private int ordinalSetPointerOffset;
        private int ordinalSetSizeOffset;

        // bits for ordinal sets
        private int bitsPerOrdinal;
        private int bitsPerOrdinalSet;

        private int maxNodes;
        private FixedLengthElementArray nodes, ordinalSet;
        private int indexTracker;// where to create new node

        /**
         * Create new prefix index. Represents a ternary search tree.
         *
         * @param maxOrdinalValue   max ordinal value for value type stored.
         * @param cardinalityValues total cardinality of the values
         * @param cardinalityKeys   total cardinality of the keys
         */
        private Tst(int maxOrdinalValue, int cardinalityValues, int cardinalityKeys) {

            // if there are 100 keys to be indexed, assuming each key has 256 chars.
            maxNodes = cardinalityKeys * 256;

            // bits needed to hold a value ordinal
            bitsPerOrdinal = 32 - Integer.numberOfLeadingZeros(maxOrdinalValue);
            // total bits to hold all ordinals for a given key
            bitsPerOrdinalSet = cardinalityValues * bitsPerOrdinal;

            // bits for pointers in a single node:
            bitsPerKey = 16;// key
            bitsForChildPointer = 32 - Integer.numberOfLeadingZeros(maxNodes);// a child pointer
            bitsForOrdinalSetPointer = 32 - Integer.numberOfLeadingZeros(maxNodes);// ordinal set pointer
            bitsForOrdinalSetSize = 32 - Integer.numberOfLeadingZeros(bitsPerOrdinalSet);// ordinal set size pointer

            // bits to represent one node
            bitsPerNode = bitsPerKey + (3 * bitsForChildPointer) + bitsForOrdinalSetSize + bitsForOrdinalSetPointer;

            nodes = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, bitsPerNode * maxNodes);
            ordinalSet = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, bitsPerOrdinalSet * maxNodes);
            indexTracker = 0;

            // initialize offsets
            leftChildOffset = bitsPerKey;// after first 16 bits in node is first left child offset.
            middleChildOffset = leftChildOffset + bitsForChildPointer;
            rightChildOffset = middleChildOffset + bitsForChildPointer;
            ordinalSetPointerOffset = bitsPerKey + (3 * bitsForChildPointer);
            ordinalSetSizeOffset = ordinalSetPointerOffset + bitsForOrdinalSetPointer;
        }

        private int getChildOffset(NodeType nodeType) {
            int offset;
            if (nodeType.equals(NodeType.Left)) offset = leftChildOffset;
            else if (nodeType.equals(NodeType.Middle)) offset = middleChildOffset;
            else offset = rightChildOffset;
            return offset;
        }

        // get child index of the given node
        private int getIndex(long currentNode, NodeType nodeType) {
            int offset = getChildOffset(nodeType);
            return (int) nodes.getElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer);
        }

        // create new node at the given index and given key
        private void setDataForNewNode(int index, char ch) {
            // set the key for new node
            nodes.setElementValue(index * bitsPerNode, bitsPerKey, ch);
            // set the ordinal set pointer to use the same index as node index
            nodes.setElementValue((index * bitsPerNode) + ordinalSetPointerOffset, bitsForOrdinalSetPointer, index);
        }

        // returns the index of new child node created for the current node index
        private int createNode(long currentNode, NodeType nodeType, char ch) {
            int indexForNode = indexTracker;

            // set data for new node at above index
            setDataForNewNode(indexForNode, ch);

            // set child index in current node
            int offset = getChildOffset(nodeType);
            nodes.setElementValue((currentNode * bitsPerNode) + offset, bitsForChildPointer, indexForNode);

            // increment tracker for next node index.
            indexTracker++;
            return indexForNode;
        }

        // get key for the given node index
        private long getKey(int nodeIndex) {
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
            int currentIndex = 0;
            for (char ch : key.toLowerCase().toCharArray()) {
                long currentValue = getKey(currentIndex);
                if (currentValue != 0) {
                    // pick the correct child by traversing the tree and creating new nodes along the path
                    while (currentValue != ch) {
                        int childIndex;// checking child index is 0, is equivalent if checking if current.child == null in pojo world
                        if (ch < currentValue) {
                            int leftIndex = getIndex(currentIndex, NodeType.Left);
                            if (leftIndex == 0) leftIndex = createNode(currentIndex, NodeType.Left, ch);
                            childIndex = leftIndex;
                        } else if (ch > currentValue) {
                            int rightIndex = getIndex(currentIndex, NodeType.Right);
                            if (rightIndex == 0) rightIndex = createNode(currentIndex, NodeType.Right, ch);
                            childIndex = rightIndex;
                        } else {
                            int middleIndex = getIndex(currentIndex, NodeType.Middle);
                            if (middleIndex == 0) middleIndex = createNode(currentIndex, NodeType.Middle, ch);
                            childIndex = middleIndex;
                        }
                        currentIndex = childIndex;
                        currentValue = getKey(currentIndex);
                    }
                    // save ordinal
                    addOrdinal(currentIndex, ordinal);

                } else {
                    // this part is entered only if root is not initialized.
                    setDataForNewNode(currentIndex, ch);
                    indexTracker++;
                    addOrdinal(currentIndex, ordinal);
                }
            }
        }

        private void addOrdinal(long nodeIndex, long ordinal) {
            // find index of ordinal set that current node points to
            long ordinalSetIndex = nodes.getElementValue((nodeIndex * bitsPerNode) + ordinalSetPointerOffset, bitsForOrdinalSetPointer);
            int ordinalSetSize = (int) nodes.getElementValue((nodeIndex * bitsPerNode) + ordinalSetSizeOffset, bitsForOrdinalSetSize);

            // add ordinal
            ordinalSet.setElementValue((ordinalSetIndex * bitsPerOrdinalSet) + (ordinalSetSize * bitsPerOrdinal), bitsPerOrdinal, ordinal);

            // increment set size and re-add for the node
            nodes.setElementValue((nodeIndex * bitsPerNode) + ordinalSetSizeOffset, bitsForOrdinalSetSize, (ordinalSetSize + 1));
        }

        /**
         * Find all the ordinals that match the given prefix.
         *
         * @param prefix
         * @return
         */
        private Set<Integer> query(String prefix) {
            if (prefix == null) throw new IllegalArgumentException("Cannot query null prefix");
            Set<Integer> ordinals = new HashSet<>();
            int current = 0;
            for (char ch : prefix.toLowerCase().toCharArray()) {
                long currentValue = getKey(current);
                while (currentValue != 0 && currentValue != ch) {
                    if (ch < currentValue) current = getIndex(current, NodeType.Left);
                    else if (ch > currentValue) current = getIndex(current, NodeType.Right);
                    else current = getIndex(current, NodeType.Middle);
                    currentValue = getKey(current);// update value
                }
            }
            if (getKey(current) != 0) {

                long ordinalSetIndex = nodes.getElementValue(current * bitsPerNode + ordinalSetPointerOffset, bitsForOrdinalSetPointer);
                int ordinalSetSize = (int) nodes.getElementValue(current * bitsPerNode + ordinalSetSizeOffset, bitsForOrdinalSetSize);
                if (ordinalSetSize == 0) return ordinals;

                int i = 0;
                do {
                    int ordinal = (int) ordinalSet.getElementValue((ordinalSetIndex * bitsPerOrdinalSet) + (i * bitsPerOrdinal), bitsPerOrdinal);
                    ordinals.add(ordinal);
                    i++;
                } while (i < (ordinalSetSize - 1));
            }
            return ordinals;
        }
    }
}
