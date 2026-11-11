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

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;

final class HollowHeldListDataAccess extends HollowHeldTypeDataAccess implements HollowListTypeDataAccess {
    private final HollowListTypeReadState engine;

    HollowHeldListDataAccess(HollowHeldStateDataAccess dataAccess, DirectLookup.TypeLookup lookup, long position) {
        super(dataAccess, lookup, position,
                new HollowListSampler(lookup.engine.getSchema().getName(), DisabledSamplingDirector.INSTANCE));
        this.engine = (HollowListTypeReadState) lookup.engine;
    }

    @Override
    public HollowListSchema getSchema() {
        return engine.getSchema();
    }

    @Override
    public int size(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.size(ordinal) : copy(entry).size(entry.copyOrdinal);
    }

    @Override
    public int getElementOrdinal(int ordinal, int listIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.getElementOrdinal(ordinal, listIndex)
                : copy(entry).getElementOrdinal(entry.copyOrdinal, listIndex);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.ordinalIterator(ordinal) : copy(entry).ordinalIterator(entry.copyOrdinal);
    }

    private static HollowListTypeReadState copy(DirectLookup.Entry entry) {
        return (HollowListTypeReadState) entry.copy;
    }
}
