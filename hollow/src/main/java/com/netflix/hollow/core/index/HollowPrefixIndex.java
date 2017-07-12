package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
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

    HollowObjectTypeReadState readStateForValue;
    HollowObjectTypeReadState readStateForKey;

    private int fieldPositionInValueObject;
    private int fieldPositionInKeyObject;
    private boolean isKeyReferenceType;

    private String type;
    private String field;
    private Tst prefixTree;

    /**
     * Constructor to create a prefix index with the given parameters.
     *
     * @param readStateEngine state engine to read data from
     * @param type            type in the read state engine from which the prefix needs to be built. Schema of the type must be HollowObjectSchema
     * @param field           field to use as keys in prefix index. field must of type String.
     */
    public HollowPrefixIndex(HollowReadStateEngine readStateEngine, String type, String field) {
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.field = field;
        this.prefixTree = build();
    }

    private int getFieldPosition() {
        HollowObjectSchema objectSchema = (HollowObjectSchema) readStateEngine.getTypeState(type).getSchema();
        return objectSchema.getPosition(field);
    }

    private Tst build() {
        // check if the given type is a HollowObject.
        HollowSchema schema = readStateEngine.getSchema(type);
        if (!(schema instanceof HollowObjectSchema))
            throw new IllegalArgumentException("Prefix index can only be built for Object type schema. Given type :" + type + " is " + schema.getClass().toString());

        // check the field type
        HollowObjectSchema.FieldType fieldType = ((HollowObjectSchema) schema).getFieldType(field);
        if (!(fieldType.equals(HollowObjectSchema.FieldType.STRING) || fieldType.equals(HollowObjectSchema.FieldType.REFERENCE)))
            throw new IllegalArgumentException("Prefix index can only be built on field of type String or a reference to String. Given field: " + field + " type is :" + fieldType.toString());

        // get the read state for the given field, two types Reference and inline string.
        if (fieldType.equals(HollowObjectSchema.FieldType.REFERENCE)) {
            // get reference type
            String fieldReferenceType = ((HollowObjectSchema) schema).getReferencedType(field);
            readStateForKey = (HollowObjectTypeReadState) readStateEngine.getTypeDataAccess(fieldReferenceType);
            fieldPositionInKeyObject = ((HollowObjectSchema) (readStateEngine.getSchema(fieldReferenceType))).getPosition("value");
            if (fieldPositionInKeyObject < 0) {
                throw new IllegalArgumentException("Could not find the field: value in reference type :" + field);
            }

            isKeyReferenceType = true;
        } else {
            // inline string, use the same dataAccess state as for value
            readStateForKey = readStateForValue;
            fieldPositionInKeyObject = ((HollowObjectSchema) schema).getPosition(field);
        }

        // get HollowObjectTypeRead state for the given type
        readStateForValue = (HollowObjectTypeReadState) readStateEngine.getTypeState(type);

        // get the field fieldPositionInValueObject in type read state
        fieldPositionInValueObject = getFieldPosition();

        // get the populated bit set from type read state
        BitSet ordinals = readStateForValue.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        int maxOrdinalValue = readStateForValue.maxOrdinal();
        Tst prefixIndex = new Tst(maxOrdinalValue, ordinals.cardinality(), readStateForKey.getPopulatedOrdinals().cardinality());

        // iterate through the populated bitset to build the tree
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {

            // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
            addOrdinal(ordinal, prefixIndex);
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
        return prefixIndex;
    }

    private void addOrdinal(int ordinal, Tst tst) {
        // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
        String key;
        if (isKeyReferenceType) {
            int referenceOrdinal = readStateForValue.readOrdinal(ordinal, fieldPositionInValueObject);
            key = readStateForKey.readString(referenceOrdinal, fieldPositionInKeyObject);
        } else {
            key = readStateForValue.readString(ordinal, fieldPositionInKeyObject);
        }
        tst.insert(key, ordinal);
    }

    /**
     * Query the index to find all the ordinals that match the given prefix.
     *
     * @param prefix
     * @return ordinals of the parent object.
     */
    public Set<Integer> query(String prefix) {
        return prefixTree.query(prefix);
    }

    @Override
    public void beginUpdate() {
        // before delta is applied -> no action to be taken
    }

    @Override
    public void addedOrdinal(int ordinal) {
        // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
    }

    @Override
    public void removedOrdinal(int ordinal) {
        // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
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
        protected Tst(int maxOrdinalValue, int cardinalityValues, int cardinalityKeys) {

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
        int createNode(long currentNode, NodeType nodeType, char ch) {
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

        // get key for the child of given node index
        private long getKey(int nodeIndex, NodeType type) {
            int childIndex = getIndex(nodeIndex, type);
            return (char) getKey(childIndex);
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
