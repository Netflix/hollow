/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.client.longevity;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.schema.HollowSchema;

/** A held version's access to one type, through {@link DirectLookup}. */
abstract class HollowHeldTypeDataAccess implements HollowTypeDataAccess {
    final HollowHeldStateDataAccess dataAccess;
    private final DirectLookup.TypeLookup lookup;
    private final long position;
    private final HollowSampler sampler;

    HollowHeldTypeDataAccess(HollowHeldStateDataAccess dataAccess, DirectLookup.TypeLookup lookup, long position,
            HollowSampler sampler) {
        this.dataAccess = dataAccess;
        this.lookup = lookup;
        this.position = position;
        this.sampler = sampler;
    }

    /**
     * @return the entry of the copy that holds the record of {@code ordinal}, or null if the state engine has it
     */
    final DirectLookup.Entry copyOf(int ordinal) {
        return lookup.find(ordinal, position);
    }

    @Override
    public HollowHeldStateDataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowSchema getSchema() {
        return lookup.engine.getSchema();
    }

    @Override
    public HollowTypeReadState getTypeState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        sampler.setSamplingDirector(director);
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        sampler.setFieldSpecificSamplingDirector(fieldSpec, director);
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        sampler.setUpdateThread(t);
    }

    @Override
    public HollowSampler getSampler() {
        return sampler;
    }
}
