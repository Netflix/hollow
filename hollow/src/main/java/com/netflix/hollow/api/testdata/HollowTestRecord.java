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
package com.netflix.hollow.api.testdata;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

public abstract class HollowTestRecord<T> {

    private final T parent;
    private int assignedOrdinal = -1;

    protected HollowTestRecord(T parent) {
        this.parent = parent;
    }

    public T up() {
        return parent;
    }

    @SuppressWarnings({"hiding", "unchecked"})
    public <T> T upTop() {
        HollowTestRecord<?> root = this;
        while(root.up() != null) {
            root = (HollowTestRecord<?>) root.up();
        }
        return (T) root;
    }

    int addTo(HollowWriteStateEngine writeEngine) {
        HollowSchema schema = getSchema();
        HollowTypeWriteState typeState = writeEngine.getTypeState(schema.getName());
        if(typeState == null) {
            switch(schema.getSchemaType()) {
                case OBJECT:
                    typeState = new HollowObjectTypeWriteState((HollowObjectSchema) schema);
                    break;
                case LIST:
                    typeState = new HollowListTypeWriteState((HollowListSchema) schema);
                    break;
                case SET:
                    typeState = new HollowSetTypeWriteState((HollowSetSchema) schema);
                    break;
                case MAP:
                    typeState = new HollowMapTypeWriteState((HollowMapSchema) schema);
                    break;
            }
            writeEngine.addTypeState(typeState);
        }

        assignedOrdinal = typeState.add(toWriteRecord(writeEngine));
        return assignedOrdinal;
    }

    public int getOrdinal() {
        return assignedOrdinal;
    }

    protected abstract HollowSchema getSchema();

    protected abstract HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine);

}
