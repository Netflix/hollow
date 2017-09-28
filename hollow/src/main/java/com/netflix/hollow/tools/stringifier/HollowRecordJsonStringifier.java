/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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


    /**
     * Create a JSON representation of the provided {@link HollowRecord}.
     */
    @Override
    public String stringify(HollowRecord record) {
        return stringify(record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(), record.getOrdinal());
    }


    /**
     * Create a JSON representation of the record in the provided dataset, of the given type, with the specified ordinal.
     */
    @Override
    public String stringify(HollowDataAccess dataAccess, String type, int ordinal) {
        StringWriter builder = new StringWriter();

        try {
            appendStringify(builder, dataAccess, type, ordinal, 0);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    /**
     * Write a JSON representation of the provided {@link HollowRecord} to the provided Writer.
     */
    public void stringify(Writer writer, HollowRecord record) throws IOException {
        stringify(writer, record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(), record.getOrdinal());
    }

    /**
     * Write a JSON representation of the record in the provided dataset, of the given type, with the specified ordinal.
     *
     * The representation will be written to the provided Writer.
     */
    public void stringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal) throws IOException {
        appendStringify(writer, dataAccess, type, ordinal, 0);
    }

    private void appendStringify(Writer builder, HollowDataAccess dataAccess, String type, int ordinal, int indentation) throws IOException {
        if (excludeObjectTypes.contains(type)) {
            builder.append("null");
            return;
        }

        HollowTypeDataAccess typeDataAccess = dataAccess.getTypeDataAccess(type);

        if(typeDataAccess == null) {
            builder.append("{ }");
        } else if (ordinal == -1) {
            builder.append("null");
        } else {
            if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
                appendObjectStringify(builder, dataAccess, (HollowObjectTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowListTypeDataAccess) {
                appendListStringify(builder, dataAccess, (HollowListTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowSetTypeDataAccess) {
                appendSetStringify(builder, dataAccess, (HollowSetTypeDataAccess)typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
                appendMapStringify(builder, dataAccess, (HollowMapTypeDataAccess)typeDataAccess, ordinal, indentation);
            }
        }

    }

    private void appendMapStringify(Writer builder, HollowDataAccess dataAccess, HollowMapTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowMapSchema schema = typeDataAccess.getSchema();

        indentation++;

        int size = typeDataAccess.size(ordinal);

        if(size == 0) {
            builder.append("{ }");
        } else {
            String keyType = schema.getKeyType();
            String valueType = schema.getValueType();
            HollowObjectTypeDataAccess keyTypeDataAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(keyType);
            HollowObjectSchema keySchema = keyTypeDataAccess.getSchema();

            HollowMapEntryOrdinalIterator ordinalIterator = typeDataAccess.ordinalIterator(ordinal);

            if(keySchema.numFields() == 1) {
                builder.append("{");
                if(prettyPrint)
                    builder.append("\n");

                boolean firstEntry = true;

                while(ordinalIterator.next()) {
                    if(firstEntry) {
                        firstEntry = false;
                    } else {
                        builder.append(",");
                        if(prettyPrint)
                            builder.append("\n");
                    }

                    if(prettyPrint)
                        appendIndentation(builder, indentation);

                    boolean needToQuoteKey = keySchema.getFieldType(0) != FieldType.STRING;

                    if(needToQuoteKey)
                        builder.append("\"");

                    int keyOrdinal = ordinalIterator.getKey();
                    appendFieldStringify(builder, dataAccess, indentation, keySchema, keyTypeDataAccess, keyOrdinal, 0);

                    if(needToQuoteKey)
                        builder.append("\"");

                    builder.append(": ");

                    appendStringify(builder, dataAccess, valueType, ordinalIterator.getValue(), indentation);
                }

                if(prettyPrint && !firstEntry) {
                    builder.append("\n");
                    appendIndentation(builder, indentation - 1);
                }
                builder.append("}");
            } else {
                builder.append("[");
                if(prettyPrint)
                    builder.append("\n");

                boolean firstEntry = true;

                while(ordinalIterator.next()) {
                    if(firstEntry) {
                        firstEntry = false;
                    } else {
                        builder.append(",");
                        if(prettyPrint)
                            builder.append("\n");
                    }
                    if(prettyPrint) {
                        appendIndentation(builder, indentation - 1);
                    }

                    builder.append("{");

                    if(prettyPrint) {
                        builder.append("\n");
                        appendIndentation(builder, indentation);
                    }

                    builder.append("\"key\":");
                    appendStringify(builder, dataAccess, keyType, ordinalIterator.getKey(), indentation + 1);
                    builder.append(",");
                    if(prettyPrint) {
                        builder.append("\n");
                        appendIndentation(builder, indentation);
                    }
                    builder.append("\"value\":");
                    appendStringify(builder, dataAccess, valueType, ordinalIterator.getValue(), indentation + 1);

                    if(prettyPrint) {
                        builder.append("\n");
                        appendIndentation(builder, indentation - 1);
                    }

                    builder.append("}");
                }



                builder.append("]");
            }
        }

    }

    private void appendSetStringify(Writer builder, HollowDataAccess dataAccess, HollowSetTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowSetSchema schema = typeDataAccess.getSchema();

        indentation++;

        String elementType = schema.getElementType();

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        if(elementOrdinal == HollowOrdinalIterator.NO_MORE_ORDINALS) {
            builder.append("[]");
        } else {
            boolean firstElement = true;
            builder.append("[");
            if(prettyPrint)
                builder.append("\n");

            while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                if(firstElement)
                    firstElement = false;
                else
                    builder.append(",");

                if(prettyPrint)
                    appendIndentation(builder, indentation);

                appendStringify(builder, dataAccess, elementType, elementOrdinal, indentation);

                elementOrdinal = iter.next();
            }

            if(prettyPrint) {
                builder.append("\n");
                appendIndentation(builder, indentation - 1);
            }

            builder.append("]");
        }
    }

    private void appendListStringify(Writer builder, HollowDataAccess dataAccess, HollowListTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowListSchema schema = typeDataAccess.getSchema();

        indentation++;

        int size = typeDataAccess.size(ordinal);

        if(size == 0) {
            builder.append("[]");
        } else {
            builder.append("[\n");

            String elementType = schema.getElementType();

            for(int i=0;i<size;i++) {
                int elementOrdinal = typeDataAccess.getElementOrdinal(ordinal, i);

                if(prettyPrint)
                    appendIndentation(builder, indentation);

                appendStringify(builder, dataAccess, elementType, elementOrdinal, indentation);

                if(i < size - 1)
                    builder.append(",\n");
            }

            if(prettyPrint) {
                builder.append("\n");
                appendIndentation(builder, indentation - 1);
            }
            builder.append("]");
        }
    }

    private void appendObjectStringify(Writer builder, HollowDataAccess dataAccess, HollowObjectTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowObjectSchema schema = typeDataAccess.getSchema();

        if(schema.numFields() == 1 && (collapseAllSingleFieldObjects || collapseObjectTypes.contains(schema.getName()))) {
            appendFieldStringify(builder, dataAccess, indentation, schema, typeDataAccess, ordinal, 0);
        } else {

            builder.append("{");
            boolean firstField = true;
            indentation++;

            for(int i=0;i<schema.numFields();i++) {
                String fieldName = schema.getFieldName(i);

                if(!typeDataAccess.isNull(ordinal, i)) {
                    if(firstField)
                        firstField = false;
                    else
                        builder.append(",");

                    if(prettyPrint) {
                        builder.append("\n");
                        appendIndentation(builder, indentation);
                    }

                    builder.append("\"").append(fieldName).append("\": ");
                    appendFieldStringify(builder, dataAccess, indentation, schema, typeDataAccess, ordinal, i);
                }

            }

            if(prettyPrint && !firstField) {
                builder.append("\n");
                appendIndentation(builder, indentation - 1);
            }
            builder.append("}");
        }
    }

    private void appendFieldStringify(Writer builder, HollowDataAccess dataAccess, int indentation, HollowObjectSchema schema, HollowObjectTypeDataAccess typeDataAccess, int ordinal, int fieldIdx) throws IOException {
        switch(schema.getFieldType(fieldIdx)) {
            case BOOLEAN:
                builder.append(typeDataAccess.readBoolean(ordinal, fieldIdx).booleanValue() ? "true" : "false");
                return;
            case BYTES:
                builder.append(Arrays.toString(typeDataAccess.readBytes(ordinal, fieldIdx)));
                return;
            case DOUBLE:
                builder.append(String.valueOf(typeDataAccess.readDouble(ordinal, fieldIdx)));
                return;
            case FLOAT:
                builder.append(String.valueOf(typeDataAccess.readFloat(ordinal, fieldIdx)));
                return;
            case INT:
                builder.append(String.valueOf(typeDataAccess.readInt(ordinal, fieldIdx)));
                return;
            case LONG:
                builder.append(String.valueOf(typeDataAccess.readLong(ordinal, fieldIdx)));
                return;
            case STRING:
                builder.append("\"").append(escapeString(typeDataAccess.readString(ordinal, fieldIdx))).append("\"");
                return;
            case REFERENCE:
                int refOrdinal = typeDataAccess.readOrdinal(ordinal, fieldIdx);
                appendStringify(builder, dataAccess, schema.getReferencedType(fieldIdx), refOrdinal, indentation);
                return;
        }
    }

    private String escapeString(String str) {
        if(str.indexOf('\\') == -1 && str.indexOf('\"') == -1)
            return str;
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void appendIndentation(Writer builder, int indentation) throws IOException {
        switch(indentation) {
            case 0:
                return;
            case 1:
                builder.append("  ");
                return;
            case 2:
                builder.append("    ");
                return;
            case 3:
                builder.append("      ");
                return;
            case 4:
                builder.append("        ");
                return;
            case 5:
                builder.append("          ");
                return;
            case 6:
                builder.append("            ");
                return;
            case 7:
                builder.append("              ");
                return;
            case 8:
                builder.append("                ");
                return;
            case 9:
                builder.append("                  ");
                return;
            default:
                for(int i=0;i<indentation;i++) {
                    builder.append("  ");
                }
        }
    }
}
