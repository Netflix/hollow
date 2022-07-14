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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;

public class HollowHistoricalListDataAccess extends HollowHistoricalTypeDataAccess implements HollowListTypeDataAccess {

    public HollowHistoricalListDataAccess(HollowHistoricalStateDataAccess dataAccess, HollowTypeReadState typeState) {
        super(dataAccess, typeState, new HollowListSampler(typeState.getSchema().getName(), DisabledSamplingDirector.INSTANCE));
    }

    @Override
    public HollowListSchema getSchema() {
        return removedRecords().getSchema();
    }

    @Override
    public int getElementOrdinal(int ordinal, int listIndex) {
        sampler().recordGet();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowListTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).getElementOrdinal(ordinal, listIndex);

        return removedRecords().getElementOrdinal(getMappedOrdinal(ordinal), listIndex);
    }

    @Override
    public int size(int ordinal) {
        sampler().recordSize();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowListTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).size(ordinal);

        return removedRecords().size(getMappedOrdinal(ordinal));
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        sampler().recordIterator();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowListTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).ordinalIterator(ordinal);

        return removedRecords().ordinalIterator(getMappedOrdinal(ordinal));
    }

    private HollowListTypeReadState removedRecords() {
        return (HollowListTypeReadState) removedRecords;
    }

    private HollowListSampler sampler() {
        return (HollowListSampler) sampler;
    }

}
