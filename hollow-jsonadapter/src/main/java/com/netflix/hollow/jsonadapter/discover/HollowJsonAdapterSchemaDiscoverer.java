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
package com.netflix.hollow.jsonadapter.discover;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.jsonadapter.AbstractHollowJsonAdaptorTask;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HollowJsonAdapterSchemaDiscoverer extends AbstractHollowJsonAdaptorTask {

    private final Set<String> mapTypes;
    private final Map<String, HollowDiscoveredSchema> discoveredSchemas;
    private final HollowSchemaNamer schemaNamer;

    public HollowJsonAdapterSchemaDiscoverer(String typeName) {
        super(typeName, "scan");
        this.mapTypes = new HashSet<String>();
        this.discoveredSchemas = new ConcurrentHashMap<String, HollowDiscoveredSchema>();
        this.schemaNamer = new HollowSchemaNamer();
    }

    public void addMapTypes(String... types) {
        for(String type : types) {
            String mapType = schemaNamer.schemaNameFromPropertyPath(type);
            mapTypes.add(mapType);
        }
    }

    public void addMapTypes(Set<String> types) {
        for(String type : types) {
            String mapType = schemaNamer.schemaNameFromPropertyPath(type);
            mapTypes.add(mapType);
        }
    }

    @Override
    protected int processRecord(JsonParser parser) throws IOException {
        if(isDebug) System.out.println("\nProcessRecord: " + typeName);
        final HollowDiscoveredSchema schema = discoveredSchema(typeName, DiscoveredSchemaType.OBJECT, null);
        parser.nextToken();
        discoverSchemas(parser, schema);
        return -1;
    }

    public Collection<HollowDiscoveredSchema> discoverSchemas(File jsonFile, Integer maxSample) throws Exception {
        processFile(jsonFile, maxSample);
        return discoveredSchemas.values();
    }

    public Collection<HollowDiscoveredSchema> discoverSchemas(Reader jsonReader, Integer maxSample) throws Exception {
        processFile(jsonReader, maxSample);
        return discoveredSchemas.values();
    }

    private void discoverSchemas(JsonParser parser, HollowDiscoveredSchema schema) throws IOException {
        JsonToken token = parser.nextToken();

        while(token != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            //if (isDebug) parser = print("discoverSchemas fieldName=" + fieldName, token, parser);

            discoverSchemaField(parser, token, fieldName, schema);
            token = parser.nextToken();
        }
    }

    private void discoverSchemaField(JsonParser parser, JsonToken token, String fieldName, HollowDiscoveredSchema schema) throws IOException {

        if(token != JsonToken.FIELD_NAME) {
            switch(token) {
                case START_ARRAY:
                    String listName = schemaNamer.subCollectionName(schema.schemaName, "ArrayOf", fieldName);
                    String elementName = schemaNamer.subObjectName(schema.schemaName, "", fieldName);
                    if(isDebug) System.out.println(String.format("\t ARR[START] token=%s schemaName=%s fieldName=%s listName=%s elementName=%s", token, schema.schemaName, fieldName, listName, elementName));

                    discoveredSchema(listName, DiscoveredSchemaType.LIST, elementName);
                    schema.addField(fieldName, FieldType.REFERENCE, listName);

                    HollowDiscoveredSchema elementSchema = discoveredSchema(elementName, DiscoveredSchemaType.OBJECT, null);
                    discoverSubArraySchemas(parser, elementSchema);
                    if(isDebug) System.out.println(String.format("\t ARR[END] token=%s schemaName=%s fieldName=%s listName=%s elementName=%s elementSchema=%s", token, schema.schemaName, fieldName, listName, elementName, elementSchema));

                    break;
                case START_OBJECT:
                    String subObjectName = schemaNamer.subObjectName(schema.schemaName, "", fieldName);
                    //if (isDebug) System.out.println("\t\t [MAP CHECK] subObjectName=" + subObjectName + "\t" + mapTypes.contains(subObjectName) + "\t" + mapTypes);
                    if(mapTypes.contains(subObjectName)) {
                        String subMapName = schemaNamer.subCollectionName(schema.schemaName, "MapOf", fieldName);
                        if(isDebug) System.out.println(String.format("\t MAP[START] token=%s schemaName=%s fieldName=%s subMapName=%s subObjectName=%s", token, schema.schemaName, fieldName, subMapName, subObjectName));

                        discoveredSchema(subMapName, DiscoveredSchemaType.MAP, subObjectName);
                        schema.addField(fieldName, FieldType.REFERENCE, subMapName);

                        HollowDiscoveredSchema valueSchema = discoveredSchema(subObjectName, DiscoveredSchemaType.OBJECT, null);
                        discoverSubMapSchemas(parser, valueSchema);
                        if(isDebug) System.out.println(String.format("\t MAP[END] token=%s schemaName=%s fieldName=%s subMapName=%s subObjectName=%s valueSchema=%s", token, schema.schemaName, fieldName, subMapName, subObjectName, valueSchema));
                    } else {
                        if(isDebug) System.out.println(String.format("\t OBJ[START] token=%s schemaName=%s fieldName=%s subObjectName=%s", token, schema.schemaName, fieldName, subObjectName));
                        HollowDiscoveredSchema subObjectSchema = discoveredSchema(subObjectName, DiscoveredSchemaType.OBJECT, null);
                        if(fieldName != null) schema.addField(fieldName, FieldType.REFERENCE, subObjectName);

                        discoverSchemas(parser, subObjectSchema);
                        if(isDebug) System.out.println(String.format("\t OBJ[END] token=%s schemaName=%s fieldName=%s subObjectName=%s subObjectSchema=%s", token, schema.schemaName, fieldName, subObjectName, subObjectSchema));
                    }

                    break;
                case VALUE_NUMBER_INT:
                    if(isDebug) System.out.println(String.format("\t FIELD token=%s schemaName=%s fieldName=%s value=%s", token, schema.schemaName, fieldName, parser.getLongValue()));
                    schema.addField(fieldName, FieldType.LONG);
                    break;
                case VALUE_NUMBER_FLOAT:
                    if(isDebug) System.out.println(String.format("\t FIELD token=%s schemaName=%s fieldName=%s value=%s", token, schema.schemaName, fieldName, parser.getDoubleValue()));
                    schema.addField(fieldName, FieldType.DOUBLE);
                    break;
                case VALUE_NULL:
                    if(isDebug) System.out.println(String.format("\t FIELD token=%s schemaName=%s fieldName=%s", token, schema.schemaName, fieldName));
                    break;
                case VALUE_STRING:
                    if(isDebug) System.out.println(String.format("\t FIELD token=%s schemaName=%s fieldName=%s value=%s", token, schema.schemaName, fieldName, parser.getValueAsString()));
                    schema.addField(fieldName, FieldType.STRING);
                    break;
                case VALUE_FALSE:
                case VALUE_TRUE:
                    if(isDebug) System.out.println(String.format("\t FIELD token=%s schemaName=%s fieldName=%s value=%s", token, schema.schemaName, fieldName, parser.getBooleanValue()));
                    schema.addField(fieldName, FieldType.BOOLEAN);
                    break;
                default:
            }
        }
    }

    private void discoverSubArraySchemas(JsonParser parser, HollowDiscoveredSchema objectSchema) throws IOException {
        JsonToken token = parser.nextToken();

        while(token != JsonToken.END_ARRAY) {
            if(token == JsonToken.START_OBJECT) {
                discoverSchemas(parser, objectSchema);
            } else {
                discoverSchemaField(parser, token, "value", objectSchema);
            }
            token = parser.nextToken();
        }
    }

    private void discoverSubMapSchemas(JsonParser parser, HollowDiscoveredSchema objectSchema) throws IOException {
        JsonToken token = parser.nextToken();
        if(isDebug) System.out.println("discoverSubMapSchemas[START]: token=" + token + ", fieldname=" + parser.getCurrentName());

        while(token != JsonToken.END_OBJECT) {
            if(isDebug) System.out.println("discoverSubMapSchemas[LOOP]: token=" + token + ", fieldname=" + parser.getCurrentName());
            if(token != JsonToken.FIELD_NAME) {
                if(token == JsonToken.START_OBJECT) {
                    if(isDebug) System.out.println("discoverSubMapSchemas[LOOP] discoverSchemas: token=" + token + ", fieldname=" + parser.getCurrentName());
                    discoverSchemas(parser, objectSchema);
                } else {
                    if(isDebug) System.out.println("discoverSubMapSchemas[LOOP] discoverSchemaField: token=" + token + ", fieldname=" + parser.getCurrentName());
                    discoverSchemaField(parser, token, "value", objectSchema);
                }
            }
            token = parser.nextToken();
        }

        if(isDebug) System.out.println("discoverSubMapSchemas[END]: token=" + token);
    }

    private HollowDiscoveredSchema discoveredSchema(String schemaName, DiscoveredSchemaType type, String listSubType) {
        HollowDiscoveredSchema schema = discoveredSchemas.get(schemaName);
        if(schema == null) {
            synchronized (discoveredSchemas) {
                schema = discoveredSchemas.get(schemaName);
                if(schema == null) {
                    schema = new HollowDiscoveredSchema(schemaName, type, listSubType);
                    discoveredSchemas.put(schemaName, schema);
                }
            }
        }

        if(schema.type != type)
            throw new RuntimeException(schemaName + ": Expected schema of type " + type + " but was " + schema.type);

        return schema;
    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static void analyzeSchemas(Collection<HollowDiscoveredSchema> schemas, int largeNumOfFieldsThreshold) {
        for(HollowDiscoveredSchema schema : schemas) {
            boolean isObjectWithLargeNumOfFieldsFields = (schema.type == DiscoveredSchemaType.OBJECT) && (schema.fields.size() > largeNumOfFieldsThreshold);
            System.out.print("\t");
            System.out.println(schema);

            // Fields
            int fieldCount = schema.fields.size();
            if(fieldCount == 0) continue;

            int i = 0;
            int maxKeyLen = 0;
            String fieldHeaderTemplate = isObjectWithLargeNumOfFieldsFields ? "[***] Object with lots of fields: %s" : "Field Count: %s";
            StringBuilder builder = new StringBuilder("\t  - ");
            builder.append(String.format(fieldHeaderTemplate, fieldCount)).append("\n");
            for(String key : schema.fields.keySet()) {
                if(key.length() > maxKeyLen) maxKeyLen = key.length();
            } // find max key len
            for(Map.Entry<String, HollowDiscoveredField> entry : schema.fields.entrySet()) {
                builder.append("\t\t").append(++i).append(": fieldname=").append(padRight(entry.getKey(), maxKeyLen)).append("\t -> ").append(entry.getValue()).append("\n");
            }
            System.out.print(builder);

        }
    }

}
