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

import static com.netflix.hollow.core.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;

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
import java.util.Arrays;
import java.util.HashSet;
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
        for (String type : types) {
            this.excludeObjectTypes.add(type);
        }
        return this;
    }


    /**
     * create a String representation of the specified {@link HollowRecord}.
     */
    @Override
    public String stringify(HollowRecord record) {
        return stringify(record.getTypeDataAccess().getDataAccess(), record.getSchema().getName(), record.getOrdinal());
    }

    /**
     * create a String representation of the record in the provided dataset, of the given type, with the specified ordinal.
     */
    @Override
    public String stringify(HollowDataAccess dataAccess, String type, int ordinal) {
        StringBuilder builder = new StringBuilder();

        appendStringify(builder, dataAccess, type, ordinal, 0);

        return builder.toString();
    }

    private void appendStringify(StringBuilder builder, HollowDataAccess dataAccess, String type, int ordinal, int indentation) {
        if (excludeObjectTypes.contains(type)) {
            builder.append("null");
            return;
        }

        HollowTypeDataAccess typeDataAccess = dataAccess.getTypeDataAccess(type);

        if(typeDataAccess == null) {
            builder.append("[missing type " + type + "]");
        } else if (ordinal == -1) {
            builder.append("[missing data " + type + "]");
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

    private void appendMapStringify(StringBuilder builder, HollowDataAccess dataAccess, HollowMapTypeDataAccess typeDataAccess, int ordinal, int indentation) {
        HollowMapSchema schema = typeDataAccess.getSchema();

        if(showTypes)
            builder.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            builder.append(" (ordinal ").append(ordinal).append(")");

        indentation++;

        String keyType = schema.getKeyType();
        String valueType = schema.getValueType();

        HollowMapEntryOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        while(iter.next()) {
            builder.append("\n");

            appendIndentation(builder, indentation);
            builder.append("k: ");
            appendStringify(builder, dataAccess, keyType, iter.getKey(), indentation);
            builder.append("\n");
            appendIndentation(builder, indentation);
            builder.append("v: ");
            appendStringify(builder, dataAccess, valueType, iter.getValue(), indentation);
        }

    }

    private void appendSetStringify(StringBuilder builder, HollowDataAccess dataAccess, HollowSetTypeDataAccess typeDataAccess, int ordinal, int indentation) {
        HollowSetSchema schema = typeDataAccess.getSchema();
        if(showTypes)
            builder.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            builder.append(" (ordinal ").append(ordinal).append(")");

        indentation++;

        String elementType = schema.getElementType();

        HollowOrdinalIterator iter = typeDataAccess.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        while(elementOrdinal != NO_MORE_ORDINALS) {
            builder.append("\n");

            appendIndentation(builder, indentation);
            builder.append("e: ");

            appendStringify(builder, dataAccess, elementType, elementOrdinal, indentation);

            elementOrdinal = iter.next();
        }
    }

    private void appendListStringify(StringBuilder builder, HollowDataAccess dataAccess, HollowListTypeDataAccess typeDataAccess, int ordinal, int indentation) {
        HollowListSchema schema = typeDataAccess.getSchema();
        if(showTypes)
            builder.append("(").append(schema.getName()).append(")");

        if(showOrdinals)
            builder.append(" (ordinal ").append(ordinal).append(")");

        indentation++;

        int size = typeDataAccess.size(ordinal);

        String elementType = schema.getElementType();

        for(int i=0;i<size;i++) {
            builder.append("\n");

            int elementOrdinal = typeDataAccess.getElementOrdinal(ordinal, i);

            appendIndentation(builder, indentation);
            builder.append("e" + i + ": ");

            appendStringify(builder, dataAccess, elementType, elementOrdinal, indentation);
        }
    }

    private void appendObjectStringify(StringBuilder builder, HollowDataAccess dataAccess, HollowObjectTypeDataAccess typeDataAccess, int ordinal, int indentation) {
        HollowObjectSchema schema = typeDataAccess.getSchema();

        GenericHollowObject obj = new GenericHollowObject(typeDataAccess, ordinal);

        if(collapseSingleFieldObjects && typeDataAccess.getSchema().numFields() == 1) {
            appendFieldStringify(builder, dataAccess, indentation, schema, obj, 0, schema.getFieldName(0));
        } else {
            if(showTypes)
                builder.append("(").append(schema.getName()).append(")");

            if(showOrdinals)
                builder.append(" (ordinal ").append(ordinal).append(")");

            indentation++;

            for(int i=0;i<schema.numFields();i++) {
                builder.append("\n");

                String fieldName = schema.getFieldName(i);

                appendIndentation(builder, indentation);
                builder.append(fieldName).append(": ");

                appendFieldStringify(builder, dataAccess, indentation, schema, obj, i, fieldName);
            }
        }
    }

    private void appendFieldStringify(StringBuilder builder, HollowDataAccess dataAccess, int indentation, HollowObjectSchema schema, GenericHollowObject obj, int i, String fieldName) {
        if(obj.isNull(fieldName)) {
            builder.append("null");
        } else {
            switch(schema.getFieldType(i)) {
                case BOOLEAN:
                    builder.append(obj.getBoolean(fieldName));
                    break;
                case BYTES:
                    builder.append(Arrays.toString(obj.getBytes(fieldName)));
                    break;
                case DOUBLE:
                    builder.append(obj.getDouble(fieldName));
                    break;
                case FLOAT:
                    builder.append(obj.getFloat(fieldName));
                    break;
                case INT:
                    builder.append(obj.getInt(fieldName));
                    break;
                case LONG:
                    builder.append(obj.getLong(fieldName));
                    break;
                case STRING:
                    builder.append(obj.getString(fieldName));
                    break;
                case REFERENCE:
                    int refOrdinal = obj.getOrdinal(fieldName);
                    appendStringify(builder, dataAccess, schema.getReferencedType(i), refOrdinal, indentation);
                    break;
            }
        }
    }

    private void appendIndentation(StringBuilder builder, int indentation) {
        for(int i=0;i<indentation;i++) {
            builder.append("  ");
        }
    }


}
