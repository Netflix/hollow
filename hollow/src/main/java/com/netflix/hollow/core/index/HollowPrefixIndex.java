package com.netflix.hollow.core.index;

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
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
        this.prefixTree = new Tst();// todo if using FixedElementArray then read the type state to find the size of max ordinal and pass that in initialization of the tst.
        this.readStateEngine = readStateEngine;
        this.type = type;
        this.field = field;
        build();
    }

    private int getFieldPosition() {
        HollowTypeDataAccess typeDataAccess = readStateEngine.getTypeDataAccess(type);
        HollowObjectSchema objectSchema = (HollowObjectSchema) typeDataAccess.getSchema();
        int position = objectSchema.getPosition(field);
        if (position < 0)
            throw new IllegalArgumentException("Cannot find the field:" + field + " in the given type:" + type);
        return position;
    }


    private void build() {
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

        // iterate through the populated bitset to build the tree
        int ordinal = ordinals.nextSetBit(0);
        while (ordinal != -1) {

            // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
            updateIndex(ordinal, true);
            ordinal = ordinals.nextSetBit(ordinal + 1);
        }
    }

    private void updateIndex(int ordinal, boolean toAdd) {
        // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
        String key;
        if (isKeyReferenceType) {
            int referenceOrdinal = readStateForValue.readOrdinal(ordinal, fieldPositionInValueObject);
            key = readStateForKey.readString(referenceOrdinal, fieldPositionInKeyObject);
        } else {
            key = readStateForValue.readString(ordinal, fieldPositionInKeyObject);
        }
        if (toAdd) prefixTree.insert(key, ordinal);
        else prefixTree.remove(key, ordinal);
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
        updateIndex(ordinal, true);
    }

    @Override
    public void removedOrdinal(int ordinal) {
        // for each ordinal and field fieldPositionInValueObject -> get key and ordinal of the object
        updateIndex(ordinal, false);
    }

    @Override
    public void endUpdate() {
        // after delta is applied -> no action to be taken
    }

    // data structure to match keys (prefix) with values. TST is very space efficient compared to a trie, with minute trade-off in cost to perform look-ups.
    private class Tst {

        // class to represent a node in the ternary search tree.
        private class Node {

            private char key;
            private Node left, middle, right; // todo need reference to parent, if delete operation involves removing the node from the tree.
            private Set<Integer> ordinals;// todo replace with compact FixedLengthElementArray

            protected Node(char key) {
                this.key = key;
                this.ordinals = new HashSet<>();
            }

        }

        private Node root;  // root of the tree
        private int size;   // size of the tree -> needed?
        private Object lock;

        // private constructor
        protected Tst() {
            this.size = 0;
            this.root = null;
            this.lock = new Object();
        }

        /**
         * get total number of keys inserted in the tree.
         */
        private int getSize() {
            return size;
        }

        /**
         * Insert into ternary search tree for the given key and ordinal.
         *
         * @param key
         * @param ordinal
         */
        private synchronized void insert(String key, int ordinal) {
            if (key == null) throw new IllegalArgumentException("Null key cannot be indexed");
            synchronized (lock) {
                Node current = root;
                for (char ch : key.toLowerCase().toCharArray()) {
                    if (current != null) {
                        // pick the correct child by traversing the tree and creating new nodes along with path
                        while (current.key != ch) {
                            Node child;
                            if (ch < current.key) {
                                if (current.left == null) current.left = new Node(ch);
                                child = current.left;
                            } else if (ch > current.key) {
                                if (current.right == null) current.right = new Node(ch);
                                child = current.right;
                            } else {
                                if (current.middle == null) current.middle = new Node(ch);
                                child = current.middle;
                            }
                            current = child;
                        }
                        // add the ordinal to every prefix of the key.
                        current.ordinals.add(ordinal);
                    } else {
                        // this part is entered only if root is not initialized.
                        if (this.root == null) {
                            this.root = new Node(ch);
                        }
                        current = this.root;
                        current.ordinals.add(ordinal);
                    }
                }
                // increment the size once key is inserted in the tree.
                size++;
            }
        }

        /**
         * Find all the ordinals that match the given prefix.
         *
         * @param prefix
         * @return
         */
        private Set<Integer> query(String prefix) {
            if (prefix == null) throw new IllegalArgumentException("Cannot query null prefix");
            Node current = this.root;
            Set<Integer> ordinals = new HashSet<>();
            for (char ch : prefix.toLowerCase().toCharArray()) {
                while (current != null && current.key != ch) {
                    if (ch < current.key) current = current.left;
                    else if (ch > current.key) current = current.right;
                    else current = current.middle;
                }
            }
            if (current != null) {
                ordinals = current.ordinals;
            }
            return ordinals;
        }

        /**
         * for the given prefix, remove all the references of the ordinal in the tree.
         *
         * @param prefix
         * @param ordinal
         */
        private synchronized void remove(String prefix, int ordinal) {
            if (prefix == null) throw new IllegalArgumentException("Cannot remove null prefix from the tree");
            synchronized (lock) {
                Node current = this.root;
                for (char ch : prefix.toLowerCase().toCharArray()) {
                    while (current != null && current.key != ch) {
                        if (ch < current.key) current = current.left;
                        else if (ch > current.key) current = current.right;
                        else current = current.middle;
                    }
                    if (current.key == ch)
                        current.ordinals.remove(ordinal);//todo remove the node from the tree if the set of ordinals is empty.
                }
            }
        }
    }
}
