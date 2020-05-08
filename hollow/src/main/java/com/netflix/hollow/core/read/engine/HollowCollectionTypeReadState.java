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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.dataaccess.HollowCollectionTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.schema.HollowSchema;

/**
 * The parent class for {@link HollowTypeReadState}s for SET or LIST records. 
 */
public abstract class HollowCollectionTypeReadState extends HollowTypeReadState implements HollowCollectionTypeDataAccess {

    public HollowCollectionTypeReadState(HollowReadStateEngine stateEngine, MemoryMode memoryMode, HollowSchema schema) {
        super(stateEngine, memoryMode, schema);
    }

    public abstract int size(int ordinal);
    public abstract HollowOrdinalIterator ordinalIterator(int ordinal);

    public abstract HollowCollectionSchema getSchema();

}
