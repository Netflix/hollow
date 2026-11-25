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
import com.netflix.hollow.api.sampling.HollowObjectSampler;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;

final class HollowHeldObjectDataAccess extends HollowHeldTypeDataAccess implements HollowObjectTypeDataAccess {
    private final HollowObjectTypeReadState engine;

    HollowHeldObjectDataAccess(HollowHeldStateDataAccess dataAccess, DirectLookup.TypeLookup lookup, long position) {
        super(dataAccess, lookup, position, new HollowObjectSampler((HollowObjectSchema) lookup.engine.getSchema(),
                DisabledSamplingDirector.INSTANCE));
        this.engine = (HollowObjectTypeReadState) lookup.engine;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return engine.getSchema();
    }

    @Override
    public boolean isNull(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.isNull(ordinal, fieldIndex) : copy(entry).isNull(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public int readOrdinal(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readOrdinal(ordinal, fieldIndex)
                : copy(entry).readOrdinal(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public int readInt(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.readInt(ordinal, fieldIndex) : copy(entry).readInt(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public float readFloat(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readFloat(ordinal, fieldIndex)
                : copy(entry).readFloat(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public double readDouble(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readDouble(ordinal, fieldIndex)
                : copy(entry).readDouble(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public long readLong(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readLong(ordinal, fieldIndex)
                : copy(entry).readLong(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public Boolean readBoolean(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readBoolean(ordinal, fieldIndex)
                : copy(entry).readBoolean(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public byte[] readBytes(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readBytes(ordinal, fieldIndex)
                : copy(entry).readBytes(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public String readString(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.readString(ordinal, fieldIndex)
                : copy(entry).readString(entry.copyOrdinal, fieldIndex);
    }

    @Override
    public boolean isStringFieldEqual(int ordinal, int fieldIndex, String testValue) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.isStringFieldEqual(ordinal, fieldIndex, testValue)
                : copy(entry).isStringFieldEqual(entry.copyOrdinal, fieldIndex, testValue);
    }

    @Override
    public int findVarLengthFieldHashCode(int ordinal, int fieldIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.findVarLengthFieldHashCode(ordinal, fieldIndex)
                : copy(entry).findVarLengthFieldHashCode(entry.copyOrdinal, fieldIndex);
    }

    private static HollowObjectTypeReadState copy(DirectLookup.Entry entry) {
        return (HollowObjectTypeReadState) entry.copy;
    }
}
