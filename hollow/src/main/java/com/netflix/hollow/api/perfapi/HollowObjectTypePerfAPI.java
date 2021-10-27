/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.perfapi;

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import java.util.Arrays;

public abstract class HollowObjectTypePerfAPI extends HollowTypePerfAPI {
    
    protected final HollowObjectTypeDataAccess typeAccess;

    protected final int[] fieldIdx;
    protected final long[] refMaskedTypeIdx;
    
    public HollowObjectTypePerfAPI(HollowDataAccess dataAccess, String typeName, HollowPerformanceAPI api, String[] fieldNames) {
        super(typeName, api);
        
        HollowObjectTypeDataAccess typeAccess = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(typeName);
        
        this.fieldIdx = new int[fieldNames.length];
        this.refMaskedTypeIdx = new long[fieldNames.length];
        
        if(typeAccess != null) {
            HollowObjectSchema schema = typeAccess.getSchema();
            for(int i=0;i<fieldNames.length;i++) {
                fieldIdx[i] = schema.getPosition(fieldNames[i]);
                if(fieldIdx[i] != -1 && schema.getFieldType(fieldIdx[i]) == FieldType.REFERENCE) {
                    refMaskedTypeIdx[i] = Ref.toTypeMasked(api.types.getIdx(schema.getReferencedType(fieldIdx[i])));
                }
            }
        } else {
            Arrays.fill(fieldIdx, -1);
            Arrays.fill(refMaskedTypeIdx, Ref.toTypeMasked(Ref.TYPE_ABSENT));
        }

        if(typeAccess == null)
            typeAccess = new HollowObjectMissingDataAccess(dataAccess, typeName);
        this.typeAccess = typeAccess;
    }

    public HollowObjectTypeDataAccess typeAccess() {
        return typeAccess;
    }
}
