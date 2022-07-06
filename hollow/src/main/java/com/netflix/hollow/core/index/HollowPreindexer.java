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
package com.netflix.hollow.core.index;

import static java.util.stream.Collectors.joining;

import com.netflix.hollow.core.index.HollowHashIndexField.FieldPathElement;
import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HollowPreindexer {
    
    private final HollowDataAccess stateEngine;
    private final String type;
    private final String selectField;
    private final String[] matchFields;
    
    private HollowTypeDataAccess typeState;
    private HollowHashIndexField[] matchFieldSpecs;
    private int numMatchTraverserFields;
    private HollowHashIndexField selectFieldSpec;
    private HollowIndexerValueTraverser traverser;
    
    public HollowPreindexer(HollowDataAccess stateEngine, String type, String selectField, String... matchFields) {
        this.stateEngine = stateEngine;
        this.type = type;
        this.selectField = selectField;
        this.matchFields = matchFields;
    }

    public void buildFieldSpecifications() {
        Map<String, Integer> baseFieldToIndexMap = new HashMap<>();

        this.typeState = stateEngine.getTypeDataAccess(type);

        matchFieldSpecs = new HollowHashIndexField[matchFields.length];

        for(int i=0;i<matchFields.length;i++) {
            matchFieldSpecs[i] = getHollowHashIndexField(typeState, matchFields[i], baseFieldToIndexMap, true);
        }

        numMatchTraverserFields = baseFieldToIndexMap.size();
        selectFieldSpec = getHollowHashIndexField(typeState, selectField, baseFieldToIndexMap, false);

        String[] baseFields = new String[baseFieldToIndexMap.size()];

        for(Map.Entry<String, Integer> entry : baseFieldToIndexMap.entrySet()) {
            baseFields[entry.getValue()] = entry.getKey();
        }

        traverser = new HollowIndexerValueTraverser(stateEngine, type, baseFields);
    }

    private HollowHashIndexField getHollowHashIndexField(HollowTypeDataAccess originalDataAccess, String selectField,
            Map<String, Integer> baseFieldToIndexMap, boolean truncate) {
        FieldPaths.FieldPath<FieldPaths.FieldSegment> path = FieldPaths.createFieldPathForHashIndex(
                stateEngine, type, selectField);

        HollowTypeDataAccess baseTypeState = originalDataAccess;

        int baseFieldPathIdx = 0;

        List<FieldPaths.FieldSegment> segments = path.getSegments();
        FieldPathElement[] fieldPathIndexes = new FieldPathElement[segments.size()];
        FieldType fieldType = FieldType.REFERENCE;

        for (int i = 0; i < segments.size(); i++) {
            FieldPaths.FieldSegment segment = segments.get(i);

            HollowSchema schema = segment.enclosingSchema;
            switch (schema.getSchemaType()) {
                case OBJECT:
                    FieldPaths.ObjectFieldSegment objectSegment = (FieldPaths.ObjectFieldSegment) segment;
                    fieldType = objectSegment.getType();
                    int fieldPosition = objectSegment.getIndex();
                    HollowTypeDataAccess typeDataAccess = originalDataAccess.getDataAccess().getTypeDataAccess(objectSegment.getEnclosingSchema().getName());
                    fieldPathIndexes[i] = new FieldPathElement(fieldPosition, (HollowObjectTypeDataAccess) typeDataAccess);

                    if(!truncate)
                        baseFieldPathIdx = i + 1;
                    break;
                case SET:
                case LIST:
                    fieldType = FieldType.REFERENCE;

                    HollowCollectionSchema collectionSchema = (HollowCollectionSchema) schema;
                    baseTypeState = originalDataAccess.getDataAccess().getTypeDataAccess(collectionSchema.getElementType());

                    baseFieldPathIdx = i + 1;
                    break;
                case MAP:
                    fieldType = FieldType.REFERENCE;

                    HollowMapSchema mapSchema = (HollowMapSchema) schema;
                    boolean isKey = "key".equals(segment.getName());
                    String elementType = isKey ? mapSchema.getKeyType() : mapSchema.getValueType();
                    baseTypeState = originalDataAccess.getDataAccess().getTypeDataAccess(elementType);

                    baseFieldPathIdx = i + 1;
                    break;
            }
        }

        String basePath = segments.stream().limit(baseFieldPathIdx)
                .map(FieldPaths.FieldSegment::getName)
                .collect(joining("."));
        int basePathIdx = baseFieldToIndexMap.computeIfAbsent(basePath, k -> baseFieldToIndexMap.size());

        return new HollowHashIndexField(basePathIdx,
                Arrays.copyOfRange(fieldPathIndexes, baseFieldPathIdx, fieldPathIndexes.length),
                baseTypeState, fieldType);
    }

    public HollowTypeDataAccess getHollowTypeDataAccess() {
        return typeState;
    }

    public HollowHashIndexField[] getMatchFieldSpecs() {
        return matchFieldSpecs;
    }

    public int getNumMatchTraverserFields() {
        return numMatchTraverserFields;
    }

    public HollowHashIndexField getSelectFieldSpec() {
        return selectFieldSpec;
    }

    public HollowIndexerValueTraverser getTraverser() {
        return traverser;
    }
    
}
