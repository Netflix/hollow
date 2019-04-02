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
 */
package com.netflix.hollow.api.consumer.data;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;

/**
 * Provide generic way to access data per type to retrieve All, Added, Updated and Removed Records
 * <p>
 * It manages data as GenericHollowObject
 *
 * @deprecated use {@code ObjectDataAccessor<GenericHollowObject>}
 */
@Deprecated
public class GenericHollowRecordDataAccessor extends AbstractHollowDataAccessor<GenericHollowObject> {

    public GenericHollowRecordDataAccessor(HollowConsumer consumer, String type) {
        super(consumer.getStateEngine(), type);
    }

    public GenericHollowRecordDataAccessor(HollowReadStateEngine rStateEngine, String type) {
        super(rStateEngine, type, (PrimaryKey) null);
    }

    public GenericHollowRecordDataAccessor(HollowReadStateEngine rStateEngine, String type, String... fieldPaths) {
        super(rStateEngine, type, new PrimaryKey(type, fieldPaths));
    }

    public GenericHollowRecordDataAccessor(HollowReadStateEngine rStateEngine, String type, PrimaryKey primaryKey) {
        super(rStateEngine, type, primaryKey);
    }

    @Override
    public GenericHollowObject getRecord(int ordinal) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) rStateEngine.getTypeDataAccess(type).getTypeState();
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);
        return obj;
    }
}
