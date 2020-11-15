package com.netflix.hollow.tools.util;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.BitSet;

public class SearchUtils {

    /**
     * Parse a colon-separated string into a primary key, throw exception if format exception for eg. if primary key was
     * expecting an integer but keyString didn't contain a parseable integer at the right spot
     */
    public static Object[] parseKey(HollowReadStateEngine readStateEngine, PrimaryKey primaryKey, String keyString) {
        // Split by the number of fields of the primary key
        // This ensures correct extraction of an empty value for the last field
        String fields[] = keyString.split(":", primaryKey.numFields());

        Object key[] = new Object[fields.length];

        for(int i=0;i<fields.length;i++) {
            switch(primaryKey.getFieldType(readStateEngine, i)) {
                case BOOLEAN:
                    key[i] = Boolean.parseBoolean(fields[i]);
                    break;
                case STRING:
                    key[i] = fields[i];
                    break;
                case INT:
                case REFERENCE:
                    key[i] = Integer.parseInt(fields[i]);
                    break;
                case LONG:
                    key[i] = Long.parseLong(fields[i]);
                    break;
                case DOUBLE:
                    key[i] = Double.parseDouble(fields[i]);
                    break;
                case FLOAT:
                    key[i] = Float.parseFloat(fields[i]);
                    break;
                case BYTES:
                    key[i] = null; //TODO
            }
        }
        return key;
    }

    /**
     * Return field index in object schema for each field comprising primary key
     */
    public static int[][] getFieldPathIndexes(HollowReadStateEngine readStateEngine, PrimaryKey primaryKey) {
        if(primaryKey != null) {
            int fieldPathIndexes[][] = new int[primaryKey.numFields()][];
            for(int i=0;i<primaryKey.numFields();i++) {
                fieldPathIndexes[i] = primaryKey.getFieldPathIndex(readStateEngine, i);
            }
            return fieldPathIndexes;
        }

        return null;
    }

    /**
     * Returns primary key index for a given type if it exists
     */
    public static HollowPrimaryKeyIndex findPrimaryKeyIndex(HollowTypeReadState typeState) {
        if(getPrimaryKey(typeState.getSchema()) == null)
            return null;

        for(HollowTypeStateListener listener : typeState.getListeners()) {
            if(listener instanceof HollowPrimaryKeyIndex) {
                if(((HollowPrimaryKeyIndex) listener).getPrimaryKey().equals(getPrimaryKey(typeState.getSchema())))
                    return (HollowPrimaryKeyIndex)listener;
            }
        }

        return null;
    }

    /**
     * Get the primary key for an object schema
     */
    public static PrimaryKey getPrimaryKey(HollowSchema schema) {
        if(schema.getSchemaType() == HollowSchema.SchemaType.OBJECT)
            return ((HollowObjectSchema)schema).getPrimaryKey();
        return null;
    }

    /**
     * Returns the ordinal corresponding to the search result of searching by primary key
     */
    public static Integer getOrdinalToDisplay(HollowReadStateEngine readStateEngine, String query, Object[] parsedKey,
            int ordinal, BitSet selectedOrdinals, int[][] fieldPathIndexes, HollowTypeReadState keyTypeState) {

        if ("".equals(query) && ordinal != -1) { // trust ordinal if query is empty
            return ordinal;
        } else if (!"".equals(query)) {
            // verify ordinal key matches parsed key
            if (ordinal != -1 && selectedOrdinals.get(ordinal)
                    && recordKeyEquals(keyTypeState, ordinal, parsedKey, fieldPathIndexes)) {
                return ordinal;
            } else {
                HollowPrimaryKeyIndex idx = findPrimaryKeyIndex(keyTypeState);
                if (idx != null) {
                    // N.B. - findOrdinal can return -1, the caller deals with it
                    return findOrdinal(readStateEngine, idx, query);
                } else {
                    // no index, scan through records
                    ordinal = selectedOrdinals.nextSetBit(0);
                    while (ordinal != -1) {
                        if (recordKeyEquals(keyTypeState, ordinal, parsedKey, fieldPathIndexes)) {
                            return ordinal;
                        }
                        ordinal = selectedOrdinals.nextSetBit(ordinal + 1);
                    }
                }
            }
        }
        return -1;
    }


    private static boolean recordKeyEquals(HollowTypeReadState typeState, int ordinal, Object[] key, int[][] fieldPathIndexes) {
        HollowObjectTypeReadState objState = (HollowObjectTypeReadState)typeState;

        for(int i=0;i<fieldPathIndexes.length;i++) {
            int curOrdinal = ordinal;
            HollowObjectTypeReadState curState = objState;

            for(int j=0;j<fieldPathIndexes[i].length - 1;j++) {
                curOrdinal = curState.readOrdinal(curOrdinal, fieldPathIndexes[i][j]);
                curState = (HollowObjectTypeReadState) curState.getSchema().getReferencedTypeState(fieldPathIndexes[i][j]);
            }

            if(!HollowReadFieldUtils.fieldValueEquals(curState, curOrdinal, fieldPathIndexes[i][fieldPathIndexes[i].length-1], key[i]))
                return false;
        }

        return true;
    }

    private static int findOrdinal(HollowReadStateEngine readStateEngine, HollowPrimaryKeyIndex idx, String keyString) {
        Object[] key = parseKey(readStateEngine, idx.getPrimaryKey(), keyString);

        return idx.getMatchingOrdinal(key);
    }

}
