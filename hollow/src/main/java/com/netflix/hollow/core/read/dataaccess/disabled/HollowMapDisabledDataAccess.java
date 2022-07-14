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
package com.netflix.hollow.core.read.dataaccess.disabled;

import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;

public class HollowMapDisabledDataAccess implements HollowMapTypeDataAccess {

    public static final HollowMapDisabledDataAccess INSTANCE = new HollowMapDisabledDataAccess();

    private HollowMapDisabledDataAccess() {
    }

    @Override
    public HollowDataAccess getDataAccess() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        throw new IllegalStateException("DataAccess is Disabled");
    }

    @Override
    public HollowSampler getSampler() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowTypeReadState getTypeState() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowMapSchema getSchema() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public int size(int ordinal) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public int findKey(int ordinal, Object... hashKey) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        throw new IllegalStateException("Data Access is Disabled");
    }

}
