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
package com.netflix.hollow.jsonadapter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts just a user-defined primary key from a json record
 */
public class HollowJsonAdapterPrimaryKeyFinder {

    private final Map<String, HollowSchema> hollowSchemas;

    private final String typeName;
    private final Object[] keyElementArray;
    private final Map<String, Integer> keyFieldPathPositions;

    public HollowJsonAdapterPrimaryKeyFinder(Collection<HollowSchema> schemas, String typeName, String... keyFieldPaths) {
        this.hollowSchemas = new HashMap<String, HollowSchema>();
        this.typeName = typeName;

        for(HollowSchema schema : schemas) {
            hollowSchemas.put(schema.getName(), schema);
        }

        this.keyElementArray = new Object[keyFieldPaths.length];
        this.keyFieldPathPositions = new HashMap<String, Integer>();
        for(int i = 0; i < keyFieldPaths.length; i++) {
            keyFieldPathPositions.put(keyFieldPaths[i], Integer.valueOf(i));
        }
    }

    public Object[] findKey(String json) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(new StringReader(json));
        return Arrays.copyOf(findKey(parser), keyElementArray.length);
    }

    public Object[] findKey(JsonParser parser) throws IOException {
        parser.nextToken();
        HollowObjectSchema schema = (HollowObjectSchema) hollowSchemas.get(typeName);
        addObject(parser, schema, new StringBuilder());
        return keyElementArray;
    }

    private void addObject(JsonParser parser, HollowObjectSchema schema, StringBuilder currentFieldPath) throws IOException {
        JsonToken token = parser.nextToken();

        String fieldName = null;
        try {
            while(token != JsonToken.END_OBJECT) {
                fieldName = parser.getCurrentName();
                addObjectField(parser, token, schema, fieldName, currentFieldPath);
                token = parser.nextToken();
            }
        } catch (Exception ex) {
            throw new IOException("Failed to parse field=" + fieldName + ", schema=" + schema.getName() + ", token=" + token, ex);
        }
    }

    private void addObjectField(JsonParser parser, JsonToken token, HollowObjectSchema schema, String fieldName, StringBuilder currentFieldPath) throws IOException {
        if(token != JsonToken.FIELD_NAME) {
            int fieldPosition = schema.getPosition(fieldName);

            if(fieldPosition == -1) {
                skipObjectField(parser, token);
            } else {
                int parentFieldPathLength = currentFieldPath.length();
                if(parentFieldPathLength > 0)
                    currentFieldPath.append(".");
                currentFieldPath.append(fieldName);
                Integer keyFieldPosition = keyFieldPathPositions.get(currentFieldPath.toString());

                switch(token) {
                    case START_ARRAY:
                        skipSubArray(parser);
                        break;
                    case START_OBJECT:
                        String referencedType = schema.getReferencedType(fieldName);
                        HollowSchema referencedSchema = hollowSchemas.get(referencedType);

                        if(referencedSchema.getSchemaType() == SchemaType.OBJECT)
                            addObject(parser, (HollowObjectSchema) referencedSchema, currentFieldPath);
                        else
                            skipObject(parser);

                        break;
                    case VALUE_FALSE:
                    case VALUE_TRUE:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_STRING:
                        switch(schema.getFieldType(fieldPosition)) {
                            case BOOLEAN:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = Boolean.valueOf(parser.getBooleanValue());
                                break;
                            case INT:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = Integer.valueOf(parser.getIntValue());
                                break;
                            case LONG:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = Long.valueOf(parser.getLongValue());
                                break;
                            case DOUBLE:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = Double.valueOf(parser.getDoubleValue());
                                break;
                            case FLOAT:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = Float.valueOf(parser.getFloatValue());
                                break;
                            case STRING:
                                if(keyFieldPosition != null)
                                    keyElementArray[keyFieldPosition.intValue()] = parser.getValueAsString();
                                break;
                            case REFERENCE:
                                if(keyFieldPosition != null)
                                    throw new IllegalStateException("Key elements must not be REFERENCE");
                                HollowObjectSchema subSchema = (HollowObjectSchema) hollowSchemas.get(schema.getReferencedType(fieldPosition));
                                currentFieldPath.append(".").append(subSchema.getFieldName(0));
                                keyFieldPosition = keyFieldPathPositions.get(currentFieldPath.toString());
                                if(keyFieldPosition != null) {
                                    switch(subSchema.getFieldType(0)) {
                                        case BOOLEAN:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = Boolean.valueOf(parser.getBooleanValue());
                                            break;
                                        case INT:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = Integer.valueOf(parser.getIntValue());
                                            break;
                                        case LONG:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = Long.valueOf(parser.getLongValue());
                                            break;
                                        case DOUBLE:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = Double.valueOf(parser.getDoubleValue());
                                            break;
                                        case FLOAT:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = Float.valueOf(parser.getFloatValue());
                                            break;
                                        case STRING:
                                            if(keyFieldPosition != null)
                                                keyElementArray[keyFieldPosition.intValue()] = parser.getValueAsString();
                                            break;
                                        case REFERENCE:
                                            throw new IllegalStateException("Key elements must not be REFERENCE");
                                        default:
                                    }
                                }

                            default:
                        }
                    case VALUE_NULL:
                        break;
                    default:
                }


                currentFieldPath.setLength(parentFieldPathLength);
            }
        }
    }

    private void skipObject(JsonParser parser) throws IOException {
        JsonToken token = parser.nextToken();

        try {
            while(token != JsonToken.END_OBJECT) {
                skipObjectField(parser, token);
                token = parser.nextToken();
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private void skipSubArray(JsonParser parser) throws IOException {
        JsonToken token = parser.nextToken();

        while(token != JsonToken.END_ARRAY) {

            if(token == JsonToken.START_OBJECT) {
                skipObject(parser);
            } else {
                skipObjectField(parser, token);
            }

            token = parser.nextToken();
        }
    }

    private void skipObjectField(JsonParser parser, JsonToken token) throws IOException {
        switch(token) {
            case START_ARRAY:
                skipSubArray(parser);
                break;
            case START_OBJECT:
                skipObject(parser);
                break;
            case VALUE_FALSE:
            case VALUE_TRUE:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
            case VALUE_STRING:
            case VALUE_NULL:
            default:
        }
    }

}
