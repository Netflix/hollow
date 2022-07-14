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

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.jsonadapter.field.FieldProcessor;
import java.util.HashMap;
import java.util.Map;

public class ObjectFieldMapping {

    private final HollowWriteStateEngine stateEngine;
    private final String typeName;
    private final HollowJsonAdapter populator;
    private final Map<String, ObjectMappedFieldPath> mappedFieldPaths;
    private final RemappingBuilderInstruction rootInstruction;
    private final Map<String, HollowObjectWriteRecord> writeRecords;

    public ObjectFieldMapping(String typeName, HollowJsonAdapter populator) {
        this.stateEngine = populator.stateEngine;
        this.typeName = typeName;
        this.populator = populator;
        HollowObjectTypeWriteState typeState = (HollowObjectTypeWriteState) stateEngine.getTypeState(typeName);
        HollowObjectWriteRecord writeRec = new HollowObjectWriteRecord(typeState.getSchema());
        this.rootInstruction = new RemappingBuilderInstruction(writeRec, typeState);
        this.mappedFieldPaths = new HashMap<String, ObjectMappedFieldPath>();
        this.writeRecords = new HashMap<String, HollowObjectWriteRecord>();

        mapAllPaths((HollowObjectSchema) stateEngine.getSchema(typeName));
    }

    private ObjectFieldMapping(String typeName, HollowJsonAdapter populator, Map<String, ObjectMappedFieldPath> mappedFieldPaths, RemappingBuilderInstruction rootInstruction, Map<String, HollowObjectWriteRecord> writeRecords) {
        this.stateEngine = populator.stateEngine;
        this.typeName = typeName;
        this.populator = populator;
        this.rootInstruction = rootInstruction;
        this.mappedFieldPaths = mappedFieldPaths;
        this.writeRecords = writeRecords;
    }

    public int build(int passthroughOrdinal, FlatRecordWriter flatRecordWriter) {
        int ordinal = rootInstruction.executeInstruction(passthroughOrdinal, flatRecordWriter);

        for(Map.Entry<String, HollowObjectWriteRecord> entry : writeRecords.entrySet())
            entry.getValue().reset();

        return ordinal;
    }

    private void mapAllPaths(HollowObjectSchema schema) {
        for(int i = 0; i < schema.numFields(); i++) {
            if(!mappedFieldPaths.containsKey(schema.getFieldName(i))) {
                HollowObjectWriteRecord rec = getWriteRecord(schema);
                mappedFieldPaths.put(schema.getFieldName(i), new ObjectMappedFieldPath(rec, schema.getFieldName(i), schema.getName(), schema.getFieldName(i), i, populator.getFieldProcessor(schema.getName(), schema.getFieldName(i))));
            }
        }
    }

    public void addRemappedPath(String fromFieldName, String... fieldPaths) {
        ObjectMappedFieldPath pathMapping = addPathMapping(fromFieldName, fieldPaths, rootInstruction, 0);
        mappedFieldPaths.put(fromFieldName, pathMapping);
    }

    public ObjectMappedFieldPath getSingleFieldMapping() {
        return mappedFieldPaths.entrySet().iterator().next().getValue();
    }

    public ObjectMappedFieldPath getMappedFieldPath(String fieldName) {
        return mappedFieldPaths.get(fieldName);
    }

    public void addFieldProcessor(FieldProcessor fieldProcessor) {
        for(Map.Entry<String, ObjectMappedFieldPath> entry : mappedFieldPaths.entrySet()) {
            if(fieldProcessor.getEntityName().equals(entry.getValue().getTypeName())
                    && fieldProcessor.getFieldName().equals(entry.getValue().getFieldName())) {
                entry.getValue().setFieldProcessor(fieldProcessor);
                return;
            }

            if(fieldProcessor.getEntityName().equals(entry.getValue().getUnmappedTypeName())
                    && fieldProcessor.getFieldName().equals(entry.getValue().getUnmappedFieldName())) {
                entry.getValue().setFieldProcessor(fieldProcessor);
                return;
            }
        }
    }

    private ObjectMappedFieldPath addPathMapping(String fieldName, String[] fieldPaths, RemappingBuilderInstruction instruction, int idx) {
        if(idx < fieldPaths.length - 1) {
            RemappingBuilderInstruction childInstruction = instruction.childrenRecs.get(fieldPaths[idx]);
            HollowObjectSchema schema = instruction.typeState.getSchema();
            String referencedType = schema.getReferencedType(fieldPaths[idx]);

            if(childInstruction == null) {
                HollowObjectTypeWriteState childTypeState = (HollowObjectTypeWriteState) stateEngine.getTypeState(referencedType);
                HollowObjectWriteRecord childWriteRec = getWriteRecord(childTypeState.getSchema());

                childInstruction = new RemappingBuilderInstruction(childWriteRec, childTypeState);
                instruction.addChildInstruction(fieldPaths[idx], childInstruction);
            }

            return addPathMapping(fieldName, fieldPaths, childInstruction, idx + 1);
        }

        HollowObjectSchema schema = instruction.rec.getSchema();
        String remappedFieldName = fieldPaths[idx];

        return new ObjectMappedFieldPath(instruction.rec, remappedFieldName, typeName, fieldName, schema.getPosition(remappedFieldName), findFieldProcessor(typeName, fieldName, schema.getName(), remappedFieldName));
    }

    private FieldProcessor findFieldProcessor(String typeName, String fieldName, String mappedTypeName, String mappedFieldName) {
        FieldProcessor fp = populator.getFieldProcessor(typeName, fieldName);
        if(fp != null)
            return fp;

        return populator.getFieldProcessor(mappedTypeName, mappedFieldName);
    }

    private HollowObjectWriteRecord getWriteRecord(HollowObjectSchema schema) {
        HollowObjectWriteRecord writeRecord = writeRecords.get(schema.getName());
        if(writeRecord == null) {
            writeRecord = new HollowObjectWriteRecord(schema);
            writeRecords.put(schema.getName(), writeRecord);
        }
        return writeRecord;
    }


    private class RemappingBuilderInstruction {
        private final HollowObjectWriteRecord rec;
        private final HollowObjectTypeWriteState typeState;
        private final Map<String, RemappingBuilderInstruction> childrenRecs;

        public RemappingBuilderInstruction(HollowObjectWriteRecord rec, HollowObjectTypeWriteState typeState) {
            this(rec, typeState, new HashMap<String, RemappingBuilderInstruction>());
        }

        private RemappingBuilderInstruction(HollowObjectWriteRecord rec, HollowObjectTypeWriteState typeState, Map<String, RemappingBuilderInstruction> childrenRecs) {
            this.rec = rec;
            this.typeState = typeState;
            this.childrenRecs = childrenRecs;
        }

        public void addChildInstruction(String fieldName, RemappingBuilderInstruction instruction) {
            childrenRecs.put(fieldName, instruction);
        }

        public int executeInstruction(int passthroughOrdinal, FlatRecordWriter flatRecordWriter) {
            for(Map.Entry<String, RemappingBuilderInstruction> childEntry : childrenRecs.entrySet()) {
                int childOrdinal = childEntry.getValue().executeInstruction(-1, flatRecordWriter);
                rec.setReference(childEntry.getKey(), childOrdinal);
            }

            if(passthroughOrdinal != -1)
                rec.setReference("passthrough", passthroughOrdinal);

            if(flatRecordWriter != null)
                return flatRecordWriter.write(typeState.getSchema(), rec);
            return typeState.add(rec);
        }

        public RemappingBuilderInstruction clone(Map<String, HollowObjectWriteRecord> clonedWriteRecords) {
            Map<String, RemappingBuilderInstruction> childClones = new HashMap<String, ObjectFieldMapping.RemappingBuilderInstruction>();
            for(Map.Entry<String, RemappingBuilderInstruction> childEntry : childrenRecs.entrySet())
                childClones.put(childEntry.getKey(), childEntry.getValue().clone(clonedWriteRecords));

            HollowObjectWriteRecord clonedRec = clonedWriteRecords.get(rec.getSchema().getName());

            return new RemappingBuilderInstruction(clonedRec, typeState, childClones);
        }

    }

    @Override
    public ObjectFieldMapping clone() {
        Map<String, HollowObjectWriteRecord> clonedWriteRecords = new HashMap<String, HollowObjectWriteRecord>();
        for(Map.Entry<String, HollowObjectWriteRecord> recEntry : writeRecords.entrySet()) {
            clonedWriteRecords.put(recEntry.getKey(), new HollowObjectWriteRecord(recEntry.getValue().getSchema()));
        }

        Map<String, ObjectMappedFieldPath> clonedMappedFieldPaths = new HashMap<String, ObjectMappedFieldPath>();
        for(Map.Entry<String, ObjectMappedFieldPath> fieldEntry : mappedFieldPaths.entrySet()) {
            ObjectMappedFieldPath original = fieldEntry.getValue();
            HollowObjectWriteRecord clonedWriteRecord = clonedWriteRecords.get(original.getWriteRecord().getSchema().getName());
            clonedMappedFieldPaths.put(fieldEntry.getKey(), new ObjectMappedFieldPath(clonedWriteRecord, original.getFieldName(), original.getUnmappedTypeName(), original.getUnmappedFieldName(), original.getFieldPosition(), original.getFieldProcessor()));
        }

        return new ObjectFieldMapping(typeName, populator, clonedMappedFieldPaths, rootInstruction.clone(clonedWriteRecords), clonedWriteRecords);
    }
}
