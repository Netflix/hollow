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
package com.netflix.hollow.tools.stringifier;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Produces JSON String representations of Hollow records.
 */
public class HollowRecordJsonStringifier implements HollowStringifier<HollowRecordJsonStringifier> {

    private final Set<String> collapseObjectTypes;
    private final Set<String> excludeObjectTypes = new HashSet<String>();
    private final boolean collapseAllSingleFieldObjects;
    private final boolean prettyPrint;

    public HollowRecordJsonStringifier() {
        this(true, true);
    }

    public HollowRecordJsonStringifier(boolean prettyPrint, boolean collapseAllSingleFieldObjects) {
        this.prettyPrint = prettyPrint;
        this.collapseAllSingleFieldObjects = collapseAllSingleFieldObjects;
        this.collapseObjectTypes = Collections.emptySet();
    }


    public HollowRecordJsonStringifier(boolean indent, String... collapseObjectTypes) {
        this.prettyPrint = indent;
        this.collapseAllSingleFieldObjects = false;
        this.collapseObjectTypes = new HashSet<String>();

        for (String collapseObjectType : collapseObjectTypes) {
            this.collapseObjectTypes.add(collapseObjectType);
        }
    }

    @Override
    public HollowRecordJsonStringifier addExcludeObjectTypes(String... types) {
        for (String type : types) {
            this.excludeObjectTypes.add(type);
        }
        return this;
    }

    @Override
    public String stringify(HollowRecord record) {
        return stringify(record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(), record.getOrdinal());
    }

    @Override
    public void stringify(Writer writer, HollowRecord record) throws IOException {
        stringify(writer, record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(), record.getOrdinal());
    }

    @Override
    public void stringify(Writer writer, Iterable<HollowRecord> records) throws IOException {
        writer.write("[");
        Iterator<HollowRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            stringify(writer, iterator.next());
            if (iterator.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");
    }

    @Override
    public String stringify(HollowDataAccess dataAccess, String type, int ordinal) {
        try {
            StringWriter writer = new StringWriter();
            appendStringify(writer, dataAccess, type, ordinal, 0);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error using StringWriter", e);
        }
    }

    @Override
    public void stringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal) throws IOException {
        appendStringify(writer, dataAccess, type, ordinal, 0);
    }

    private void appendStringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal, int indentation) throws IOException {
        if (excludeObjectTypes.contains(type)) {
            writer.append("null");
            return;
        }

        HollowTypeDataAccess typeDataAccess = dataAccess.getTypeDataAccess(type);

        if (typeDataAccess == null) {
            writer.append("{ }");
        } else if (ordinal == ORDINAL_NONE) {
            writer.append("null");
        } else {
            if (typeDataAccess instanceof HollowObjectTypeDataAccess) {
                appendObjectStringify(writer, dataAccess, (HollowObjectTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if (typeDataAccess instanceof HollowListTypeDataAccess) {
                appendListStringify(writer, dataAccess, (HollowListTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if (typeDataAccess instanceof HollowSetTypeDataAccess) {
                appendSetStringify(writer, dataAccess, (HollowSetTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if (typeDataAccess instanceof HollowMapTypeDataAccess) {
                appendMapStringify(writer, dataAccess, (HollowMapTypeDataAccess)typeDataAccess, ordinal, indentation);
            }
        }

    }

    private void appendMapStringify(Writer writer, HollowDataAccess dataAccess, HollowMapTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        indentation++;

        int size = typeDataAccess.size(ordinal);

        HollowMapSchema schema = typeDataAccess.getSchema();

        String keyType = schema.getKeyType();
        String valueType = schema.getValueType();

        HollowMapEntryOrdinalIterator ordinalIterator = typeDataAccess.ordinalIterator(ordinal);

        if (size == 0) {
            writer.append("{ }");
        } else {
            if (dataAccess.getTypeDataAccess(keyType) instanceof HollowObjectTypeDataAccess) {
                HollowObjectTypeDataAccess keyTypeDataAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(keyType);
                HollowObjectSchema keySchema = keyTypeDataAccess.getSchema();
                if(isPrimitiveWrapper(keySchema)) {
                    keyValueAsObject(writer, dataAccess, indentation, keyTypeDataAccess, valueType, ordinalIterator);
                }
                else {
                    keyValueAsList(writer, dataAccess, indentation, keyType, valueType, ordinalIterator);
                }
            }
            else {
                keyValueAsList(writer, dataAccess, indentation, keyType, valueType, ordinalIterator);
            }
        }

    }

    private void keyValueAsObject(Writer writer, HollowDataAccess dataAccess, int indentation, HollowObjectTypeDataAccess keyTypeDataAccess, String valueType, HollowMapEntryOrdinalIterator ordinalIterator) throws IOException {
        HollowObjectSchema keySchema = keyTypeDataAccess.getSchema();

        writer.append("{");
        if (prettyPrint) {
            writer.append(NEWLINE);
        }

        boolean firstEntry = true;

        while(ordinalIterator.next()) {
            if (firstEntry) {
                firstEntry = false;
            } else {
                writer.append(",");
                if (prettyPrint) {
                    writer.append(NEWLINE);
                }
            }

            if (prettyPrint) {
                appendIndentation(writer, indentation);
            }

            boolean needToQuoteKey = keySchema.getFieldType(0) != FieldType.STRING;

            if (needToQuoteKey)
                writer.append("\"");

            int keyOrdinal = ordinalIterator.getKey();
            appendFieldStringify(writer, dataAccess, indentation, keySchema, keyTypeDataAccess, keyOrdinal, 0);

            if (needToQuoteKey)
                writer.append("\"");

            writer.append(": ");

            appendStringify(writer, dataAccess, valueType, ordinalIterator.getValue(), indentation);
        }

        if (prettyPrint && !firstEntry) {
            writer.append(NEWLINE);
            appendIndentation(writer, indentation - 1);
        }
        writer.append("}");
    }

    private void keyValueAsList(Writer writer, HollowDataAccess dataAccess, int indentation, String keyType, String valueType, HollowMapEntryOrdinalIterator ordinalIterator) throws IOException {
        writer.append("[");
        if (prettyPrint)
            writer.append(NEWLINE);

        boolean firstEntry = true;

        while(ordinalIterator.next()) {
            if (firstEntry) {
                firstEntry = false;
            } else {
                writer.append(",");
                if (prettyPrint)
                    writer.append(NEWLINE);
            }
            if (prettyPrint) {
                appendIndentation(writer, indentation - 1);
            }

            writer.append("{");

            if (prettyPrint) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation);
            }

            writer.append("\"key\":");
            appendStringify(writer, dataAccess, keyType, ordinalIterator.getKey(), indentation + 1);
            writer.append(",");
            if (prettyPrint) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation);
            }
            writer.append("\"value\":");
            appendStringify(writer, dataAccess, valueType, ordinalIterator.getValue(), indentation + 1);

            if (prettyPrint) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation - 1);
            }

            writer.append("}");
        }


        writer.append("]");
    }

    private void appendSetStringify(Writer writer, HollowDataAccess dataAccess, HollowSetTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowSetSchema schema = typeDataAccess.getSchema();

        indentation++;

        String elementType = schema.getElementType();

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        if (elementOrdinal == HollowOrdinalIterator.NO_MORE_ORDINALS) {
            writer.append("[]");
        } else {
            boolean firstElement = true;
            writer.append("[");
            if (prettyPrint)
                writer.append(NEWLINE);

            while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                if (firstElement)
                    firstElement = false;
                else
                    writer.append(",");

                if (prettyPrint)
                    appendIndentation(writer, indentation);

                appendStringify(writer, dataAccess, elementType, elementOrdinal, indentation);

                elementOrdinal = iter.next();
            }

            if (prettyPrint) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation - 1);
            }

            writer.append("]");
        }
    }

    private void appendListStringify(Writer writer, HollowDataAccess dataAccess, HollowListTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowListSchema schema = typeDataAccess.getSchema();

        indentation++;

        int size = typeDataAccess.size(ordinal);

        if (size == 0) {
            writer.append("[]");
        } else {
            writer.append("[");
            if (prettyPrint) {
                writer.append(NEWLINE);
            }

            String elementType = schema.getElementType();

            for(int i=0;i<size;i++) {
                int elementOrdinal = typeDataAccess.getElementOrdinal(ordinal, i);

                if (prettyPrint) {
                    appendIndentation(writer, indentation);
                }

                appendStringify(writer, dataAccess, elementType, elementOrdinal, indentation);

                if (i < size - 1) {
                    writer.append(",");
                    if (prettyPrint) {
                        writer.append(NEWLINE);
                    }
                }
            }

            if (prettyPrint) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation - 1);
            }
            writer.append("]");
        }
    }

    private void appendObjectStringify(Writer writer, HollowDataAccess dataAccess, HollowObjectTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowObjectSchema schema = typeDataAccess.getSchema();

        if (schema.numFields() == 1 && (collapseAllSingleFieldObjects || collapseObjectTypes.contains(schema.getName()))) {
            appendFieldStringify(writer, dataAccess, indentation, schema, typeDataAccess, ordinal, 0);
        } else {

            writer.append("{");
            boolean firstField = true;
            indentation++;

            for(int i=0;i<schema.numFields();i++) {
                String fieldName = schema.getFieldName(i);

                if (!typeDataAccess.isNull(ordinal, i)) {
                    if (firstField)
                        firstField = false;
                    else
                        writer.append(",");

                    if (prettyPrint) {
                        writer.append(NEWLINE);
                        appendIndentation(writer, indentation);
                    }

                    writer.append("\"").append(fieldName).append("\": ");
                    appendFieldStringify(writer, dataAccess, indentation, schema, typeDataAccess, ordinal, i);
                }

            }

            if (prettyPrint && !firstField) {
                writer.append(NEWLINE);
                appendIndentation(writer, indentation - 1);
            }
            writer.append("}");
        }
    }

    private void appendFieldStringify(Writer writer, HollowDataAccess dataAccess, int indentation, HollowObjectSchema schema, HollowObjectTypeDataAccess typeDataAccess, int ordinal, int fieldIdx) throws IOException {
        switch(schema.getFieldType(fieldIdx)) {
            case BOOLEAN:
                writer.append(typeDataAccess.readBoolean(ordinal, fieldIdx).booleanValue() ? "true" : "false");
                return;
            case BYTES:
                writer.append(Arrays.toString(typeDataAccess.readBytes(ordinal, fieldIdx)));
                return;
            case DOUBLE:
                writer.append(String.valueOf(typeDataAccess.readDouble(ordinal, fieldIdx)));
                return;
            case FLOAT:
                writer.append(String.valueOf(typeDataAccess.readFloat(ordinal, fieldIdx)));
                return;
            case INT:
                writer.append(String.valueOf(typeDataAccess.readInt(ordinal, fieldIdx)));
                return;
            case LONG:
                writer.append(String.valueOf(typeDataAccess.readLong(ordinal, fieldIdx)));
                return;
            case STRING:
                writer.append("\"").append(escapeString(typeDataAccess.readString(ordinal, fieldIdx))).append("\"");
                return;
            case REFERENCE:
                int refOrdinal = typeDataAccess.readOrdinal(ordinal, fieldIdx);
                appendStringify(writer, dataAccess, schema.getReferencedType(fieldIdx), refOrdinal, indentation);
                return;
        }
    }

    private final static long CHARS2DETECT =
            (0x22L << (2*17)) |     // " DQ
            (0x5cL << 17) |         // \ RS
            0x1fL;                  // < 0x20 (non-print)
    private final static long CAT_DQ = 1L << (3*17 - 1);
    private final static long CAT_RS = 1L << (2*17 - 1);
    private final static long CAT_NP = 1L << (17 - 1);
    private final static long CATEGORY_BITS_MASK = CAT_DQ | CAT_RS | CAT_NP;
    private final static long DETECT_MASK = ~CHARS2DETECT & ~CATEGORY_BITS_MASK;
    private final static long DUP3TIMES_WITH_CAT_BIT= 1 | (1 << 17) | (1L << (17*2));
    private final static long CAUSE_CARRY = 0x20 | (1 << 17) | (1L << (17*2));

    /**
     * Returns the category (CAT_*) of ch8 - non-carry bits are garbage
     *
     * Works by making 3 copies of the character every 17 bits leaving a "carry" bit
     * to hold the category in front of each copy.  When detecting a specific char,
     * the xor mask is the 1's complement of the char to detect, which will set the
     * test char to 0xffff when it matches.  Adding 1 to the 0xffff will clear 0xffff to 0,
     * and "carry" over into the category bit recording the test character was detected.
     * (e.g, 0x5c ^ 0xffa3 + 1 = 0x10000).  Detecting < 0x20 is detecting just the upper
     * 11 bits are 0 so, 0x20 is added instead of 1 (e.g., 0x0a ^ ffe0 + 0x20 = 0x1000a).
     */
    private static long categorize(char ch) {
        long cat = ch * DUP3TIMES_WITH_CAT_BIT;

        cat ^= DETECT_MASK;
        cat += CAUSE_CARRY;

        return cat;
}

    /**
     * Returns the categories found in str (or of CAT_*)
     */
    private static long categorize(String string) {
        int len = string.length();
        long cat = 0;

        for(int i = 0; i < len ; i++) {
                cat = cat | categorize(string.charAt(i));
        }
        return cat & CATEGORY_BITS_MASK;
}

    /**
     * Escapes the NP characters in str (eg. \n becomes \u000a)
     */
    private String escapeNP(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(2*len);

        for (int i = 0; i < len; i += 1) {
            char c = str.charAt(i);

            if (c < 0x10)
                sb.append("\\u000" + Integer.toHexString(c));
            else if (c < 0x20)
                sb.append("\\u00" + Integer.toHexString(c));
            else
                sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Escapes JSON's invalid string characters in str
     * Works by categorizing the characters in str and then only
     * escaping the characters based on the categories recorded.
     */
    private String escapeString(String str) {
        long cat = categorize(str);

        if (cat == 0) {
            return str;
        }

        /* Replace reverse solidus first since subsequent substitutions add more */
        if ((cat & CAT_RS) != 0) {
            str = str.replace("\\","\\\\");
        }
        if ((cat & CAT_DQ) != 0) {
            str = str.replace("\"","\\\"");
        }
        if ((cat & CAT_NP) != 0) {
            return escapeNP(str);
        }
        return str;
    }

    private void appendIndentation(Writer writer, int indentation) throws IOException {
        switch(indentation) {
            case 0:
                return;
            case 1:
                writer.append(INDENT);
                return;
            case 2:
                writer.append(INDENT + INDENT);
                return;
            case 3:
                writer.append(INDENT + INDENT + INDENT);
                return;
            case 4:
                writer.append(INDENT + INDENT + INDENT + INDENT);
                return;
            case 5:
                writer.append(INDENT + INDENT + INDENT + INDENT + INDENT);
                return;
            case 6:
                writer.append(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT);
                return;
            case 7:
                writer.append(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT);
                return;
            case 8:
                writer.append(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT);
                return;
            case 9:
                writer.append(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT + INDENT);
                return;
            default:
                for(int i=0;i<indentation;i++) {
                    writer.append(INDENT);
                }
        }
    }

    private static boolean isPrimitiveWrapper(
            HollowSchema schema) {
        if (schema.getSchemaType() != HollowSchema.SchemaType.OBJECT) {
            return false;
        }
        HollowObjectSchema objectSchema = (HollowObjectSchema) schema;
        boolean isSingleFieldObject = objectSchema.numFields() == 1;
        if (isSingleFieldObject
                && objectSchema.getFieldType(0) != HollowObjectSchema.FieldType.REFERENCE) {
            return true;
        }
        return false;
    }
}
