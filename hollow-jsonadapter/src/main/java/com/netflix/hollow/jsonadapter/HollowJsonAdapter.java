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
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.jsonadapter.field.FieldProcessor;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Populate a HollowWriteStateEngine based on data encoded in JSON.
 */
public class HollowJsonAdapter extends AbstractHollowJsonAdaptorTask {

    final HollowWriteStateEngine stateEngine;
    private final Map<String, HollowSchema> hollowSchemas;
    private final ThreadLocal<Map<String, HollowWriteRecord>> hollowWriteRecordsHolder = new ThreadLocal<Map<String, HollowWriteRecord>>();
    private final ThreadLocal<Map<String, ObjectFieldMapping>> objectFieldMappingHolder = new ThreadLocal<Map<String, ObjectFieldMapping>>();

    private final Map<String, ObjectFieldMapping> canonicalObjectFieldMappings;

    private final Set<String> passthroughDecoratedTypes;
    private final ThreadLocal<PassthroughWriteRecords> passthroughRecords;

    /// TODO: Would be nice to be able to take a HollowDataset here, if only producing FlatRecords,
    ///       instead of requiring a HollowWriteStateEngine
    public HollowJsonAdapter(HollowWriteStateEngine stateEngine, String typeName) {
        super(typeName, "populate");
        this.stateEngine = stateEngine;
        this.hollowSchemas = new HashMap<String, HollowSchema>();
        this.canonicalObjectFieldMappings = new HashMap<String, ObjectFieldMapping>();
        this.passthroughDecoratedTypes = new HashSet<String>();

        for(HollowSchema schema : stateEngine.getSchemas()) {
            hollowSchemas.put(schema.getName(), schema);
            if(schema instanceof HollowObjectSchema)
                canonicalObjectFieldMappings.put(schema.getName(), new ObjectFieldMapping(schema.getName(), this));
        }

        ////TODO: Special 'passthrough' processing.
        this.passthroughRecords = new ThreadLocal<PassthroughWriteRecords>();
    }

    @Override
    public void addFieldProcessor(FieldProcessor... processors) {
        super.addFieldProcessor(processors);

        for(FieldProcessor processor : processors) {
            ObjectFieldMapping ofm = canonicalObjectFieldMappings.get(processor.getEntityName());
            if(ofm != null) {
                ofm.addFieldProcessor(processor);
            } else {
                for(Map.Entry<String, ObjectFieldMapping> entry : canonicalObjectFieldMappings.entrySet()) {
                    entry.getValue().addFieldProcessor(processor);
                }
            }
        }
    }

    public void remapFieldPath(String type, String fieldName, String... fieldPaths) {
        canonicalObjectFieldMappings.get(type).addRemappedPath(fieldName, fieldPaths);
    }

    ////TODO: Refactor upstream json data to not require special 'passthrough' processing.
    public void addPassthroughDecoratedType(String type) {
        passthroughDecoratedTypes.add(type);
    }

    public void populate(File jsonFile) throws Exception {
        processFile(jsonFile, Integer.MAX_VALUE);
    }

    public void populate(Reader jsonReader) throws Exception {
        processFile(jsonReader, Integer.MAX_VALUE);
    }

    public int processRecord(String singleRecord) throws IOException {
        return processRecord(singleRecord, null);
    }

    public int processRecord(String singleRecord, FlatRecordWriter flatRecordWriter) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(new StringReader(singleRecord));
        return processRecord(parser, flatRecordWriter);
    }

    @Override
    protected int processRecord(JsonParser parser) throws IOException {
        return processRecord(parser, null);
    }

    protected int processRecord(JsonParser parser, FlatRecordWriter flatRecordWriter) throws IOException {
        initHollowWriteRecordsIfNecessary();
        //parser.nextToken();
        return parseSubType(parser, flatRecordWriter, parser.nextToken(), typeName);
    }

    private int parseSubType(JsonParser parser, FlatRecordWriter flatRecordWriter, JsonToken currentToken, String subType) throws IOException {
        HollowSchema subTypeSchema = hollowSchemas.get(subType);
        switch(subTypeSchema.getSchemaType()) {
            case OBJECT:
                if(currentToken != JsonToken.START_OBJECT)
                    throw new IOException("Expecting to parse a " + subType + ", which is a " + subTypeSchema.getSchemaType() + ", expected JsonToken.START_OBJECT but instead found a " + currentToken.toString());

                return addObject(parser, flatRecordWriter, subType);

            case LIST:
            case SET:
                if(currentToken != JsonToken.START_ARRAY)
                    throw new IOException("Expecting to parse a " + subType + ", which is a " + subTypeSchema.getSchemaType() + ", expected JsonToken.START_ARRAY but instead found a " + currentToken.toString());

                return addSubArray(parser, flatRecordWriter, subType, getWriteRecord(subType));

            case MAP:
                switch(currentToken) {
                    case START_ARRAY:
                        return addStructuredMap(parser, flatRecordWriter, subType, (HollowMapWriteRecord) getWriteRecord(subType));
                    case START_OBJECT:
                        return addUnstructuredMap(parser, flatRecordWriter, subType, (HollowMapWriteRecord) getWriteRecord(subType));
                    default:
                        throw new IOException("Expecting to parse a " + subType + ", which is a " + subTypeSchema.getSchemaType() + ", expected JsonToken.START_ARRAY or JsonToken.START_OBJECT but instead found a " + currentToken.toString());
                }
        }
        throw new IOException();
    }


    private int addObject(JsonParser parser, FlatRecordWriter flatRecordWriter, String typeName) throws IOException {
        ObjectFieldMapping objectMapping = getObjectFieldMapping(typeName);

        Boolean passthroughDecoratedTypes = null;

        JsonToken token = parser.nextToken();

        PassthroughWriteRecords rec = null;

        String fieldName = null;
        try {
            while(token != JsonToken.END_OBJECT) {
                if(token != JsonToken.FIELD_NAME) {
                    fieldName = parser.getCurrentName();
                    ObjectMappedFieldPath mappedFieldPath = objectMapping.getMappedFieldPath(fieldName);

                    if(mappedFieldPath != null) {
                        addObjectField(parser, flatRecordWriter, token, mappedFieldPath);
                    } else {
                        if(passthroughDecoratedTypes == null) {
                            passthroughDecoratedTypes = Boolean.valueOf(this.passthroughDecoratedTypes.contains(typeName));

                            if(passthroughDecoratedTypes.booleanValue()) {
                                rec = getPassthroughWriteRecords();
                            }
                        }
                        if(passthroughDecoratedTypes.booleanValue()) {
                            addPassthroughField(parser, flatRecordWriter, token, fieldName, rec);
                        } else {
                            skipObjectField(parser, token);
                        }
                    }
                }

                token = parser.nextToken();
            }
        } catch (Exception ex) {
            throw new IOException("Failed to parse field=" + fieldName + ", schema=" + typeName + ", token=" + token, ex);
        }

        if(passthroughDecoratedTypes != null && passthroughDecoratedTypes.booleanValue()) {
            rec.passthroughRec.setReference("singleValues", addRecord("SingleValuePassthroughMap", rec.singleValuePassthroughMapRec, flatRecordWriter));
            rec.passthroughRec.setReference("multiValues", addRecord("MultiValuePassthroughMap", rec.multiValuePassthroughMapRec, flatRecordWriter));

            int passthroughOrdinal = addRecord("PassthroughData", rec.passthroughRec, flatRecordWriter);

            return objectMapping.build(passthroughOrdinal, flatRecordWriter);
        }

        return objectMapping.build(-1, flatRecordWriter);
    }

    private void addPassthroughField(JsonParser parser, FlatRecordWriter flatRecordWriter, JsonToken token, String fieldName, PassthroughWriteRecords rec) throws IOException {
        rec.passthroughMapKeyWriteRecord.reset();
        rec.passthroughMapKeyWriteRecord.setString("value", fieldName);
        int keyOrdinal = addRecord("MapKey", rec.passthroughMapKeyWriteRecord, flatRecordWriter);

        switch(token) {
            case START_ARRAY:
                rec.multiValuePassthroughListRec.reset();

                while(token != JsonToken.END_ARRAY) {
                    switch(token) {
                        case VALUE_FALSE:
                        case VALUE_TRUE:
                        case VALUE_NUMBER_INT:
                        case VALUE_NUMBER_FLOAT:
                        case VALUE_STRING:
                            rec.passthroughMapValueWriteRecord.reset();
                            rec.passthroughMapValueWriteRecord.setString("value", parser.getValueAsString());
                            int elementOrdinal = addRecord("String", rec.passthroughMapValueWriteRecord, flatRecordWriter);
                            rec.multiValuePassthroughListRec.addElement(elementOrdinal);
                            break;
                        default:
                            break;
                    }

                    token = parser.nextToken();
                }

                int valueListOrdinal = addRecord("ListOfString", rec.multiValuePassthroughListRec, flatRecordWriter);
                rec.multiValuePassthroughMapRec.addEntry(keyOrdinal, valueListOrdinal);
                break;
            case VALUE_FALSE:
            case VALUE_TRUE:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
            case VALUE_STRING:
                rec.passthroughMapValueWriteRecord.reset();
                rec.passthroughMapValueWriteRecord.setString("value", parser.getValueAsString());
                int valueOrdinal = addRecord("String", rec.passthroughMapValueWriteRecord, flatRecordWriter);
                rec.singleValuePassthroughMapRec.addEntry(keyOrdinal, valueOrdinal);
                break;
            case VALUE_NULL:
                break;
            case START_OBJECT:
                skipObject(parser);
                break;
            default:
                break;
        }
    }

    private void addObjectField(JsonParser parser, FlatRecordWriter flatRecordWriter, JsonToken token, ObjectMappedFieldPath mappedFieldPath) throws IOException {
        if(mappedFieldPath == null) {
            skipObjectField(parser, token);
        } else {

            HollowObjectWriteRecord writeRec = mappedFieldPath.getWriteRecord();
            HollowObjectSchema schema = writeRec.getSchema();
            String fieldName = mappedFieldPath.getFieldName();
            int fieldPosition = mappedFieldPath.getFieldPosition();

            FieldProcessor processor = mappedFieldPath.getFieldProcessor();
            if(processor != null && token != JsonToken.VALUE_NULL) {
                processor.processField(parser, stateEngine, writeRec);
                return;
            }

            switch(token) {
                case START_ARRAY:
                case START_OBJECT:
                    int refOrdinal = parseSubType(parser, flatRecordWriter, token, schema.getReferencedType(fieldPosition));
                    writeRec.setReference(fieldName, refOrdinal);
                    break;
                case VALUE_FALSE:
                case VALUE_TRUE:
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_STRING:
                    switch(schema.getFieldType(fieldPosition)) {
                        case BOOLEAN:
                            writeRec.setBoolean(fieldName, parser.getBooleanValue());
                            break;
                        case INT:
                            writeRec.setInt(fieldName, parser.getIntValue());
                            break;
                        case LONG:
                            writeRec.setLong(fieldName, parser.getLongValue());
                            break;
                        case DOUBLE:
                            writeRec.setDouble(fieldName, parser.getDoubleValue());
                            break;
                        case FLOAT:
                            writeRec.setFloat(fieldName, parser.getFloatValue());
                            break;
                        case STRING:
                            writeRec.setString(fieldName, parser.getValueAsString());
                            break;
                        case REFERENCE:
                            HollowObjectWriteRecord referencedRec = (HollowObjectWriteRecord) getWriteRecord(schema.getReferencedType(fieldPosition));
                            referencedRec.reset();
                            String refFieldName = referencedRec.getSchema().getFieldName(0);
                            switch(referencedRec.getSchema().getFieldType(0)) {
                                case BOOLEAN:
                                    referencedRec.setBoolean(refFieldName, parser.getBooleanValue());
                                    break;
                                case INT:
                                    referencedRec.setInt(refFieldName, parser.getIntValue());
                                    break;
                                case LONG:
                                    referencedRec.setLong(refFieldName, parser.getLongValue());
                                    break;
                                case DOUBLE:
                                    referencedRec.setDouble(refFieldName, parser.getDoubleValue());
                                    break;
                                case FLOAT:
                                    referencedRec.setFloat(refFieldName, parser.getFloatValue());
                                    break;
                                case STRING:
                                    referencedRec.setString(refFieldName, parser.getValueAsString());
                                    break;
                                default:
                            }

                            int referencedOrdinal = addRecord(schema.getReferencedType(fieldPosition), referencedRec, flatRecordWriter);
                            writeRec.setReference(fieldName, referencedOrdinal);
                            break;
                        default:
                    }
                case VALUE_NULL:
                    break;
                default:
            }
        }
    }

    private int addSubArray(JsonParser parser, FlatRecordWriter flatRecordWriter, String arrayType, HollowWriteRecord arrayRec) throws IOException {
        JsonToken token = parser.nextToken();
        arrayRec.reset();

        HollowCollectionSchema schema = (HollowCollectionSchema) hollowSchemas.get(arrayType);
        ObjectFieldMapping valueRec = null;
        ObjectMappedFieldPath fieldMapping = null;

        while(token != JsonToken.END_ARRAY) {

            int elementOrdinal;

            if(token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                elementOrdinal = parseSubType(parser, flatRecordWriter, token, schema.getElementType());
            } else {
                if(valueRec == null) {
                    valueRec = getObjectFieldMapping(schema.getElementType());
                    fieldMapping = valueRec.getSingleFieldMapping();
                }

                addObjectField(parser, flatRecordWriter, token, fieldMapping);
                elementOrdinal = valueRec.build(-1, flatRecordWriter);
            }

            if(arrayRec instanceof HollowListWriteRecord) {
                ((HollowListWriteRecord) arrayRec).addElement(elementOrdinal);
            } else {
                ((HollowSetWriteRecord) arrayRec).addElement(elementOrdinal);
            }

            token = parser.nextToken();
        }

        return addRecord(arrayType, arrayRec, flatRecordWriter);
    }

    private int addStructuredMap(JsonParser parser, FlatRecordWriter flatRecordWriter, String mapTypeName, HollowMapWriteRecord mapRec) throws IOException {
        JsonToken token = parser.nextToken();
        mapRec.reset();

        HollowMapSchema schema = (HollowMapSchema) hollowSchemas.get(mapTypeName);

        while(token != JsonToken.END_ARRAY) {
            if(token == JsonToken.START_OBJECT) {
                int keyOrdinal = -1, valueOrdinal = -1;
                while(token != JsonToken.END_OBJECT) {

                    if(token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                        if("key".equals(parser.getCurrentName()))
                            keyOrdinal = parseSubType(parser, flatRecordWriter, token, schema.getKeyType());
                        else if("value".equals(parser.getCurrentName()))
                            valueOrdinal = parseSubType(parser, flatRecordWriter, token, schema.getValueType());
                    }

                    token = parser.nextToken();
                }

                mapRec.addEntry(keyOrdinal, valueOrdinal);
            }

            token = parser.nextToken();
        }

        return addRecord(schema.getName(), mapRec, flatRecordWriter);
    }

    private int addUnstructuredMap(JsonParser parser, FlatRecordWriter flatRecordWriter, String mapTypeName, HollowMapWriteRecord mapRec) throws IOException {
        mapRec.reset();

        HollowMapSchema schema = (HollowMapSchema) hollowSchemas.get(mapTypeName);
        ObjectFieldMapping valueRec = null;
        ObjectMappedFieldPath fieldMapping = null;

        JsonToken token = parser.nextToken();

        while(token != JsonToken.END_OBJECT) {
            if(token != JsonToken.FIELD_NAME) {
                HollowObjectWriteRecord mapKeyWriteRecord = (HollowObjectWriteRecord) getWriteRecord(schema.getKeyType());
                String fieldName = mapKeyWriteRecord.getSchema().getFieldName(0);
                mapKeyWriteRecord.reset();

                switch(mapKeyWriteRecord.getSchema().getFieldType(0)) {
                    case STRING:
                        mapKeyWriteRecord.setString(fieldName, parser.getCurrentName());
                        break;
                    case BOOLEAN:
                        mapKeyWriteRecord.setBoolean(fieldName, Boolean.valueOf(parser.getCurrentName()));
                        break;
                    case INT:
                        mapKeyWriteRecord.setInt(fieldName, Integer.parseInt(parser.getCurrentName()));
                        break;
                    case LONG:
                        mapKeyWriteRecord.setLong(fieldName, Long.parseLong(parser.getCurrentName()));
                        break;
                    case DOUBLE:
                        mapKeyWriteRecord.setDouble(fieldName, Double.parseDouble(parser.getCurrentName()));
                        break;
                    case FLOAT:
                        mapKeyWriteRecord.setFloat(fieldName, Float.parseFloat(parser.getCurrentName()));
                        break;
                    default:
                        throw new IOException("Cannot parse type " + mapKeyWriteRecord.getSchema().getFieldType(0) + " as key in map (" + mapKeyWriteRecord.getSchema().getName() + ")");
                }

                int keyOrdinal = addRecord(schema.getKeyType(), mapKeyWriteRecord, flatRecordWriter);

                int valueOrdinal;

                if(token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                    valueOrdinal = parseSubType(parser, flatRecordWriter, token, schema.getValueType());
                } else {
                    if(valueRec == null) {
                        valueRec = getObjectFieldMapping(schema.getValueType());
                        fieldMapping = valueRec.getSingleFieldMapping();
                    }
                    addObjectField(parser, flatRecordWriter, token, fieldMapping);
                    valueOrdinal = valueRec.build(-1, flatRecordWriter);
                }

                mapRec.addEntry(keyOrdinal, valueOrdinal);
            }
            token = parser.nextToken();
        }

        return addRecord(schema.getName(), mapRec, flatRecordWriter);
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

    private int addRecord(String type, HollowWriteRecord rec, FlatRecordWriter flatRecordWriter) {
        if(flatRecordWriter != null) {
            HollowSchema schema = stateEngine.getSchema(type);
            return flatRecordWriter.write(schema, rec);
        }
        return stateEngine.add(type, rec);
    }

    private void initHollowWriteRecordsIfNecessary() {
        if(hollowWriteRecordsHolder.get() == null) {
            synchronized (this) {
                if(hollowWriteRecordsHolder.get() == null) {
                    Map<String, HollowWriteRecord> lookupMap = createWriteRecords(stateEngine);
                    hollowWriteRecordsHolder.set(lookupMap);
                    objectFieldMappingHolder.set(cloneFieldMappings());
                }
            }
        }
    }

    private static Map<String, HollowWriteRecord> createWriteRecords(HollowWriteStateEngine stateEngine) {
        Map<String, HollowWriteRecord> hollowWriteRecords = new HashMap<>();

        for(HollowSchema schema : stateEngine.getSchemas()) {
            switch(schema.getSchemaType()) {
                case LIST:
                    hollowWriteRecords.put(schema.getName(), new HollowListWriteRecord());
                    break;
                case MAP:
                    hollowWriteRecords.put(schema.getName(), new HollowMapWriteRecord());
                    break;
                case OBJECT:
                    hollowWriteRecords.put(schema.getName(), new HollowObjectWriteRecord((HollowObjectSchema) schema));
                    break;
                case SET:
                    hollowWriteRecords.put(schema.getName(), new HollowSetWriteRecord());
                    break;
            }

        }

        return hollowWriteRecords;
    }

    private Map<String, ObjectFieldMapping> cloneFieldMappings() {
        Map<String, ObjectFieldMapping> clonedMap = new HashMap<String, ObjectFieldMapping>();
        for(Map.Entry<String, ObjectFieldMapping> entry : canonicalObjectFieldMappings.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    ObjectFieldMapping getObjectFieldMapping(String type) throws IOException {
        Map<String, ObjectFieldMapping> objectFieldMappings = objectFieldMappingHolder.get();
        ObjectFieldMapping mapping = objectFieldMappings.get(type);
        if(mapping == null) {
            throw new IOException("WriteRecord for " + type + " not found.  Make sure Schema Discovery is done correctly.");
        }
        return mapping;
    }

    HollowWriteRecord getWriteRecord(String type) throws IOException {
        Map<String, HollowWriteRecord> hollowWriteRecords = hollowWriteRecordsHolder.get();
        HollowWriteRecord wRec = hollowWriteRecords.get(type);
        if(wRec == null) {
            throw new IOException("WriteRecord for " + type + " not found.  Make sure Schema Discovery is done correctly.");
        }
        return wRec;
    }

    private PassthroughWriteRecords getPassthroughWriteRecords() {
        PassthroughWriteRecords rec;
        rec = passthroughRecords.get();
        if(rec == null) {
            rec = new PassthroughWriteRecords();
            passthroughRecords.set(rec);
        }
        rec.passthroughRec.reset();
        rec.multiValuePassthroughMapRec.reset();
        rec.singleValuePassthroughMapRec.reset();
        return rec;
    }


    ////TODO: Special 'passthrough' processing.
    private class PassthroughWriteRecords {
        final HollowObjectWriteRecord passthroughRec;
        final HollowObjectWriteRecord passthroughMapKeyWriteRecord;
        final HollowObjectWriteRecord passthroughMapValueWriteRecord;
        final HollowMapWriteRecord singleValuePassthroughMapRec;
        final HollowMapWriteRecord multiValuePassthroughMapRec;
        final HollowListWriteRecord multiValuePassthroughListRec;

        public PassthroughWriteRecords() {
            ////TODO: Special 'passthrough' processing.
            this.passthroughRec = hollowSchemas.get("PassthroughData") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("PassthroughData")) : null;
            this.passthroughMapKeyWriteRecord = hollowSchemas.get("MapKey") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("MapKey")) : null;
            this.passthroughMapValueWriteRecord = hollowSchemas.get("String") != null ? new HollowObjectWriteRecord((HollowObjectSchema) hollowSchemas.get("String")) : null;
            this.singleValuePassthroughMapRec = new HollowMapWriteRecord();
            this.multiValuePassthroughMapRec = new HollowMapWriteRecord();
            this.multiValuePassthroughListRec = new HollowListWriteRecord();
        }
    }
}
