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

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
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
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Produces human-readable String representations of Hollow records.
 */
public class HollowRecordStringifier implements HollowStringifier<HollowRecordStringifier> {
    private final Set<String> excludeObjectTypes = new HashSet<String>();
    private final boolean showOrdinals;
    private final boolean showTypes;
    private final boolean collapseSingleFieldObjects;

    public HollowRecordStringifier() {
        this(false, false, true);
    }

    public HollowRecordStringifier(boolean showOrdinals, boolean showTypes, boolean collapseSingleFieldObjects) {
        this.showOrdinals = showOrdinals;
        this.showTypes = showTypes;
        this.collapseSingleFieldObjects = collapseSingleFieldObjects;
    }

    @Override
    public HollowRecordStringifier addExcludeObjectTypes(String... types) {
        for(String type : types) {
            this.excludeObjectTypes.add(type);
        }
        return this;
    }


    @Override
    public String stringify(HollowRecord record) {
        return stringify(record.getTypeDataAccess().getDataAccess(),
                record.getSchema().getName(), record.getOrdinal());
    }

    @Override
    public void stringify(Writer writer, HollowRecord record) throws IOException {
        stringify(writer, record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(),
                record.getOrdinal());
    }

    @Override
    public void stringify(Writer writer, Iterable<HollowRecord> records) throws IOException {
        writer.write("[");
        Iterator<HollowRecord> iterator = records.iterator();
        while(iterator.hasNext()) {
            stringify(writer, iterator.next());
            if(iterator.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("\n]");
    }

    @Override
    public String stringify(HollowDataAccess dataAccess, String type, int ordinal) {
        try {
            StringWriter writer = new StringWriter();
            stringify(writer, dataAccess, type, ordinal);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception using StringWriter", e);
        }
    }

    @Override
    public void stringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal) throws IOException {
        appendStringify(writer, dataAccess, type, ordinal, 0);
    }

    private void appendStringify(Writer writer, HollowDataAccess dataAccess, String type, int ordinal,
            int indentation) throws IOException {
        if(excludeObjectTypes.contains(type)) {
            writer.append("null");
            return;
        }

        HollowTypeDataAccess typeDataAccess = dataAccess.getTypeDataAccess(type);

        if(typeDataAccess == null) {
            writer.append("[missing type " + type + "]");
        } else if(ordinal == -1) {
            writer.append("null");
        } else {
            if(typeDataAccess instanceof HollowObjectTypeDataAccess) {
                appendObjectStringify(writer, dataAccess, (HollowObjectTypeDataAccess) typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowListTypeDataAccess) {
                appendListStringify(writer, dataAccess, (HollowListTypeDataAccess) typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowSetTypeDataAccess) {
                appendSetStringify(writer, dataAccess, (HollowSetTypeDataAccess) typeDataAccess, ordinal, indentation);
            } else if(typeDataAccess instanceof HollowMapTypeDataAccess) {
                appendMapStringify(writer, dataAccess, (HollowMapTypeDataAccess) typeDataAccess, ordinal, indentation);
            }
        }

    }

    private void appendMapStringify(Writer writer, HollowDataAccess dataAccess,
            HollowMapTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowMapSchema schema = typeDataAccess.getSchema();

        if(showTypes)
            writer.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            writer.append(" (ordinal ").append(Integer.toString(ordinal)).append(")");

        indentation++;

        String keyType = schema.getKeyType();
        String valueType = schema.getValueType();

        HollowMapEntryOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        while(iter.next()) {
            writer.append(NEWLINE);

            appendIndentation(writer, indentation);
            writer.append("k: ");
            appendStringify(writer, dataAccess, keyType, iter.getKey(), indentation);
            writer.append(NEWLINE);
            appendIndentation(writer, indentation);
            writer.append("v: ");
            appendStringify(writer, dataAccess, valueType, iter.getValue(), indentation);
        }

    }

    private void appendSetStringify(Writer writer, HollowDataAccess dataAccess,
            HollowSetTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowSetSchema schema = typeDataAccess.getSchema();
        if(showTypes)
            writer.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            writer.append(" (ordinal ").append(Integer.toString(ordinal)).append(")");

        indentation++;

        String elementType = schema.getElementType();

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        while(elementOrdinal != NO_MORE_ORDINALS) {
            writer.append(NEWLINE);

            appendIndentation(writer, indentation);
            writer.append("e: ");

            appendStringify(writer, dataAccess, elementType, elementOrdinal, indentation);

            elementOrdinal = iter.next();
        }
    }

    private void appendListStringify(Writer writer, HollowDataAccess dataAccess,
            HollowListTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowListSchema schema = typeDataAccess.getSchema();
        if(showTypes)
            writer.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            writer.append(" (ordinal ").append(Integer.toString(ordinal)).append(")");

        indentation++;

        int size = typeDataAccess.size(ordinal);

        String elementType = schema.getElementType();

        for(int i = 0; i < size; i++) {
            writer.append(NEWLINE);

            int elementOrdinal = typeDataAccess.getElementOrdinal(ordinal, i);

            appendIndentation(writer, indentation);
            writer.append("e" + i + ": ");

            appendStringify(writer, dataAccess, elementType, elementOrdinal, indentation);
        }
    }

    private void appendObjectStringify(Writer writer, HollowDataAccess dataAccess,
            HollowObjectTypeDataAccess typeDataAccess, int ordinal, int indentation) throws IOException {
        HollowObjectSchema schema = typeDataAccess.getSchema();

        GenericHollowObject obj = new GenericHollowObject(typeDataAccess, ordinal);

        if(collapseSingleFieldObjects && typeDataAccess.getSchema().numFields() == 1) {
            appendFieldStringify(writer, dataAccess, indentation, schema, obj, 0, schema.getFieldName(0));
        } else {
            if(showTypes)
                writer.append("(").append(schema.getName()).append(")");

            if(showOrdinals)
                writer.append(" (ordinal ").append(Integer.toString(ordinal)).append(")");

            indentation++;

            for(int i = 0; i < schema.numFields(); i++) {
                writer.append(NEWLINE);

                String fieldName = schema.getFieldName(i);

                appendIndentation(writer, indentation);
                writer.append(fieldName).append(": ");

                appendFieldStringify(writer, dataAccess, indentation, schema, obj, i, fieldName);
            }
        }
    }

    private void appendFieldStringify(Writer writer, HollowDataAccess dataAccess, int indentation,
            HollowObjectSchema schema, GenericHollowObject obj, int i, String fieldName) throws IOException {
        if(obj.isNull(fieldName)) {
            writer.append("null");
        } else {
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                    writer.append(Boolean.toString(obj.getBoolean(fieldName)));
                    break;
                case BYTES:
                    writer.append(Arrays.toString(obj.getBytes(fieldName)));
                    break;
                case DOUBLE:
                    writer.append(Double.toString(obj.getDouble(fieldName)));
                    break;
                case FLOAT:
                    writer.append(Float.toString(obj.getFloat(fieldName)));
                    break;
                case INT:
                    writer.append(Integer.toString(obj.getInt(fieldName)));
                    break;
                case LONG:
                    writer.append(Long.toString(obj.getLong(fieldName)));
                    break;
                case STRING:
                    writer.append(obj.getString(fieldName));
                    break;
                case REFERENCE:
                    int refOrdinal = obj.getOrdinal(fieldName);
                    appendStringify(writer, dataAccess, schema.getReferencedType(i), refOrdinal, indentation);
                    break;
            }
        }
    }

    private void appendIndentation(Writer writer, int indentation) throws IOException {
        for(int i = 0; i < indentation; i++) {
            writer.append(INDENT);
        }
    }


}
