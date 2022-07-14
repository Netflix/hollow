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

import com.netflix.hollow.api.sampling.HollowMapSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowMapSchema;

/**
 * Used when an entire MAP type, which is expected by a Generated Hollow API, is missing from the actual data.  
 */
public class HollowMapMissingDataAccess implements HollowMapTypeDataAccess {

    private final HollowDataAccess dataAccess;
    private final String typeName;

    public HollowMapMissingDataAccess(HollowDataAccess dataAccess, String typeName) {
        this.dataAccess = dataAccess;
        this.typeName = typeName;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema) missingDataHandler().handleSchema(typeName);
    }

    @Override
    public int size(int ordinal) {
        return missingDataHandler().handleMapSize(typeName, ordinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        return missingDataHandler().handleMapGet(typeName, ordinal, keyOrdinal, keyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        return missingDataHandler().handleMapGet(typeName, ordinal, keyOrdinal, hashCode);
    }

    @Override
    public int findKey(int ordinal, Object... hashKey) {
        return missingDataHandler().handleMapFindKey(typeName, ordinal, hashKey);
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        return missingDataHandler().handleMapFindValue(typeName, ordinal, hashKey);
    }

    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        return missingDataHandler().handleMapFindEntry(typeName, ordinal, hashKey);
    }

    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        return missingDataHandler().handleMapPotentialMatchOrdinalIterator(typeName, ordinal, hashCode);
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        return missingDataHandler().handleMapOrdinalIterator(typeName, ordinal);
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        throw new UnsupportedOperationException();
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
        return HollowMapSampler.NULL_SAMPLER;
    }

}
