package com.netflix.hollow.tools.util;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

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

    public static final String MULTI_FIELD_KEY_DELIMITER = ":";
    public static final String REGEX_MATCH_DELIMITER = "\\:";
    public static final String ESCAPED_MULTI_FIELD_KEY_DELIMITER = "\\\\:";

    /**
     * Parse a colon-separated string into a primary key based on a delimiter (for .eg. ':'), and throw exception if
     * format unexpected for eg. if primary key was expecting an integer but keyString didn't contain a parse-able
     * integer at the right spot.
     *
     * If the the value of the field itself contains the delimiter character, the value can be escaped using backslash
     * in order to perform search.
     */
    public static Object[] parseKey(HollowReadStateEngine readStateEngine, PrimaryKey primaryKey, String keyString) {
        /**
         * Split by the number of fields of the primary key. This ensures correct extraction of an empty value for the last field.
         * Escape the delimiter if it is preceded by a backslash.
         */
        String fields[] = keyString.split("(?<!\\\\)" + MULTI_FIELD_KEY_DELIMITER, primaryKey.numFields());

        Object key[] = new Object[fields.length];

        for(int i = 0; i < fields.length; i++) {
            switch(primaryKey.getFieldType(readStateEngine, i)) {
                case BOOLEAN:
                    key[i] = Boolean.parseBoolean(fields[i]);
                    break;
                case STRING:
                    key[i] = fields[i].replaceAll(ESCAPED_MULTI_FIELD_KEY_DELIMITER, MULTI_FIELD_KEY_DELIMITER);
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
                    throw new IllegalArgumentException("Primary key contains a field of type BYTES");
            }
        }
        return key;
    }

    /**
     * Return field index in object schema for each field comprising primary key.
     */
    public static int[][] getFieldPathIndexes(HollowReadStateEngine readStateEngine, PrimaryKey primaryKey) {
        if(primaryKey != null) {
            int fieldPathIndexes[][] = new int[primaryKey.numFields()][];
            for(int i = 0; i < primaryKey.numFields(); i++) {
                fieldPathIndexes[i] = primaryKey.getFieldPathIndex(readStateEngine, i);
            }
            return fieldPathIndexes;
        }

        return null;
    }

    /**
     * Returns primary key index for a given type if it exists.
     */
    public static HollowPrimaryKeyIndex findPrimaryKeyIndex(HollowTypeReadState typeState) {
        PrimaryKey pkey = getPrimaryKey(typeState.getSchema());
        if(pkey == null)
            return null;

        for(HollowTypeStateListener listener : typeState.getListeners()) {
            if(listener instanceof HollowPrimaryKeyIndex) {
                if(((HollowPrimaryKeyIndex) listener).getPrimaryKey().equals(pkey))
                    return (HollowPrimaryKeyIndex) listener;
            }
        }

        return null;
    }

    /**
     * Get the primary key for an object schema.
     */
    public static PrimaryKey getPrimaryKey(HollowSchema schema) {
        if(schema.getSchemaType() == HollowSchema.SchemaType.OBJECT)
            return ((HollowObjectSchema) schema).getPrimaryKey();
        return null;
    }

    /**
     * Returns the ordinal corresponding to the search result of searching by primary key.
     */
    public static Integer getOrdinalToDisplay(HollowReadStateEngine readStateEngine, String query, Object[] parsedKey,
            int ordinal, BitSet selectedOrdinals, int[][] fieldPathIndexes, HollowTypeReadState keyTypeState) {

        if("".equals(query) && ordinal != ORDINAL_NONE) { // trust ordinal if query is empty
            return ordinal;
        } else if(!"".equals(query)) {
            // verify ordinal key matches parsed key
            if(ordinal != ORDINAL_NONE && selectedOrdinals.get(ordinal)
                    && recordKeyEquals(keyTypeState, ordinal, parsedKey, fieldPathIndexes)) {
                return ordinal;
            } else {
                HollowPrimaryKeyIndex idx = findPrimaryKeyIndex(keyTypeState);
                if(idx != null) {
                    // N.B. - findOrdinal can return ORDINAL_NONE, the caller deals with it
                    return idx.getMatchingOrdinal(parsedKey);
                } else {
                    // no index, scan through records
                    ordinal = selectedOrdinals.nextSetBit(0);
                    while(ordinal != ORDINAL_NONE) {
                        if(recordKeyEquals(keyTypeState, ordinal, parsedKey, fieldPathIndexes)) {
                            return ordinal;
                        }
                        ordinal = selectedOrdinals.nextSetBit(ordinal + 1);
                    }
                }
            }
        }
        return ORDINAL_NONE;
    }

    private static boolean recordKeyEquals(HollowTypeReadState typeState, int ordinal, Object[] key, int[][] fieldPathIndexes) {
        HollowObjectTypeReadState objState = (HollowObjectTypeReadState) typeState;

        for(int i = 0; i < fieldPathIndexes.length; i++) {
            int curOrdinal = ordinal;
            HollowObjectTypeReadState curState = objState;

            for(int j = 0; j < fieldPathIndexes[i].length - 1; j++) {
                curOrdinal = curState.readOrdinal(curOrdinal, fieldPathIndexes[i][j]);
                curState = (HollowObjectTypeReadState) curState.getSchema().getReferencedTypeState(fieldPathIndexes[i][j]);
            }

            if(!HollowReadFieldUtils.fieldValueEquals(curState, curOrdinal, fieldPathIndexes[i][fieldPathIndexes[i].length - 1], key[i]))
                return false;
        }

        return true;
    }
}
