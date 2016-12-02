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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HollowPreindexer {
    
    private final HollowReadStateEngine stateEngine;
    private final String type;
    private final String selectField;
    private final String[] matchFields;
    
    private HollowTypeReadState typeState;
    private HollowHashIndexField[] matchFieldSpecs;
    private int numMatchTraverserFields;
    private HollowHashIndexField selectFieldSpec;
    private HollowIndexerValueTraverser traverser;
    
    public HollowPreindexer(HollowReadStateEngine stateEngine, String type, String selectField, String... matchFields) {
        this.stateEngine = stateEngine;
        this.type = type;
        this.selectField = selectField;
        this.matchFields = matchFields;
    }

    public void buildFieldSpecifications() {
        Map<String, Integer> baseFieldToIndexMap = new HashMap<String, Integer>();

        this.typeState = stateEngine.getTypeState(type);

        matchFieldSpecs = new HollowHashIndexField[matchFields.length];

        for(int i=0;i<matchFields.length;i++) {
            matchFieldSpecs[i] = getHollowHashIndexField(typeState, matchFields[i], baseFieldToIndexMap, true);
        }

        numMatchTraverserFields = baseFieldToIndexMap.size();
        selectFieldSpec = getHollowHashIndexField(typeState, selectField, baseFieldToIndexMap, false);

        String baseFields[] = new String[baseFieldToIndexMap.size()];

        for(Map.Entry<String, Integer> entry : baseFieldToIndexMap.entrySet()) {
            baseFields[entry.getValue().intValue()] = entry.getKey();
        }

        traverser = new HollowIndexerValueTraverser(stateEngine, type, baseFields);
    }
    
    private HollowHashIndexField getHollowHashIndexField(HollowTypeReadState originalTypeState, String selectField, Map<String, Integer> baseFieldToIndexMap, boolean truncate) {
        String fieldPaths[] = "".equals(selectField) ? new String[0] : selectField.split("\\.");
        int fieldPathIndexes[] = new int[fieldPaths.length];

        int baseFieldPathIdx = 0;
        int fieldPathIdx = 0;

        HollowTypeReadState typeState = originalTypeState;
        HollowTypeReadState baseTypeState = originalTypeState;

        FieldType fieldType = FieldType.REFERENCE;

        for(int i=0;i<fieldPaths.length;i++) {

            if(typeState instanceof HollowObjectTypeDataAccess) {
                HollowObjectSchema schema = ((HollowObjectTypeDataAccess)typeState).getSchema();
                fieldPathIndexes[fieldPathIdx] = schema.getPosition(fieldPaths[fieldPathIdx]);
                typeState = schema.getReferencedTypeState(fieldPathIndexes[fieldPathIdx]);
                fieldType = schema.getFieldType(fieldPathIndexes[fieldPathIdx]);

                if(!truncate)
                    baseFieldPathIdx = fieldPathIdx + 1;

            } else if(typeState instanceof HollowCollectionTypeDataAccess) {
                HollowCollectionSchema schema = ((HollowCollectionTypeDataAccess)typeState).getSchema();
                typeState = schema.getElementTypeState();
                baseTypeState = typeState;
                baseFieldPathIdx = fieldPathIdx+1;
                fieldType = FieldType.REFERENCE;
            } else {
                HollowMapSchema schema = ((HollowMapTypeDataAccess)typeState).getSchema();
                boolean isKey = "key".equals(fieldPaths[fieldPathIdx]);
                typeState = isKey ? schema.getKeyTypeState() : schema.getValueTypeState();
                baseTypeState = typeState;
                baseFieldPathIdx = fieldPathIdx+1;
                fieldType = FieldType.REFERENCE;
            }

            fieldPathIdx++;
        }

        StringBuilder basePathBuilder = new StringBuilder();
        for(int i=0;i<baseFieldPathIdx;i++) {
            if(i > 0)
                basePathBuilder.append('.');
            basePathBuilder.append(fieldPaths[i]);
        }
        String basePath = basePathBuilder.toString();
        Integer basePathIdx = baseFieldToIndexMap.get(basePath);
        if(basePathIdx == null) {
            basePathIdx = Integer.valueOf(baseFieldToIndexMap.size());
            baseFieldToIndexMap.put(basePath, basePathIdx);
        }

        return new HollowHashIndexField(basePathIdx.intValue(), Arrays.copyOfRange(fieldPathIndexes, baseFieldPathIdx, fieldPathIndexes.length), baseTypeState, fieldType);
    }

    public HollowTypeReadState getTypeState() {
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
