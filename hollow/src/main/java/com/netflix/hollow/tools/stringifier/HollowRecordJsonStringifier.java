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
    private boolean expandMapTypes = false;

    public HollowRecordJsonStringifier() {
        this(true, true);
    }

    public HollowRecordJsonStringifier(boolean prettyPrint, boolean collapseAllSingleFieldObjects) {
        this.prettyPrint = prettyPrint;
        this.collapseAllSingleFieldObjects = collapseAllSingleFieldObjects;
        this.collapseObjectTypes = Collections.emptySet();
    }

    public HollowRecordJsonStringifier(boolean prettyPrint, boolean collapseAllSingleFieldObjects, boolean expandMapTypes) {
        this.prettyPrint = prettyPrint;
        this.collapseAllSingleFieldObjects = collapseAllSingleFieldObjects;
        this.collapseObjectTypes = Collections.emptySet();
        this.expandMapTypes = expandMapTypes;
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
        HollowObjectTypeDataAccess keyTypeDataAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(keyType);
        HollowObjectSchema keySchema = keyTypeDataAccess.getSchema();

        HollowMapEntryOrdinalIterator ordinalIterator = typeDataAccess.ordinalIterator(ordinal);

        if (size == 0) {
            writer.append("{ }");
        } else {
            if(!expandMapTypes) {
                if (keySchema.numFields() == 1) {
                    keyValueAsObject(writer, dataAccess, indentation, keyTypeDataAccess, valueType, ordinalIterator);
                } else {
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

    private String escapeString(String str) {
        if (str.indexOf('\\') == -1 && str.indexOf('\"') == -1)
            return str;
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
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
}
