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
package com.netflix.hollow.core.read.dataaccess.missing;

import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * Used when an entire LIST type, which is expected by a Generated Hollow API, is missing from the actual data.  
 */
public class HollowListMissingDataAccess implements HollowListTypeDataAccess {

    private final HollowDataAccess dataAccess;
    private final String typeName;

    public HollowListMissingDataAccess(HollowDataAccess dataAccess, String typeName) {
        this.dataAccess = dataAccess;
        this.typeName = typeName;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowListSchema getSchema() {
        return (HollowListSchema) missingDataHandler().handleSchema(typeName);
    }

    @Override
    public int getElementOrdinal(int ordinal, int listIndex) {
        return missingDataHandler().handleListElementOrdinal(typeName, ordinal, listIndex);
    }

    @Override
    public int size(int ordinal) {
        return missingDataHandler().handleListSize(typeName, ordinal);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        return missingDataHandler().handleListIterator(typeName, ordinal);
    }

    private MissingDataHandler missingDataHandler() {
        return dataAccess.getMissingDataHandler();
    }

    @Override
    public HollowTypeReadState getTypeState() {
        throw new UnsupportedOperationException("No HollowTypeReadState exists for " + typeName);
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
    }

    @Override
    public HollowSampler getSampler() {
        return HollowListSampler.NULL_SAMPLER;
    }

}
