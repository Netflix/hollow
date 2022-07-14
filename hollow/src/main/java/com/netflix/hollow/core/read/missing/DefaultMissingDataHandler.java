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
package com.netflix.hollow.core.read.missing;

import com.netflix.hollow.core.read.iterator.EmptyMapOrdinalIterator;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSchema;

public class DefaultMissingDataHandler implements MissingDataHandler {

    @Override
    public boolean handleIsNull(String type, int ordinal, String field) {
        return true;
    }

    @Override
    public Boolean handleBoolean(String type, int ordinal, String field) {
        return null;
    }

    @Override
    public int handleReferencedOrdinal(String type, int ordinal, String field) {
        return -1;
    }

    @Override
    public int handleInt(String type, int ordinal, String field) {
        return Integer.MIN_VALUE;
    }

    @Override
    public long handleLong(String type, int ordinal, String field) {
        return Long.MIN_VALUE;
    }

    @Override
    public float handleFloat(String type, int ordinal, String field) {
        return Float.NaN;
    }

    @Override
    public double handleDouble(String type, int ordinal, String field) {
        return Double.NaN;
    }

    @Override
    public String handleString(String type, int ordinal, String field) {
        return null;
    }

    @Override
    public boolean handleStringEquals(String type, int ordinal, String field, String testValue) {
        return testValue == null;
    }

    @Override
    public byte[] handleBytes(String type, int ordinal, String field) {
        return null;
    }

    @Override
    public int handleListSize(String type, int ordinal) {
        return 0;
    }

    @Override
    public int handleListElementOrdinal(String type, int ordinal, int idx) {
        return -1;
    }

    @Override
    public HollowOrdinalIterator handleListIterator(String type, int ordinal) {
        return EmptyOrdinalIterator.INSTANCE;
    }

    @Override
    public int handleSetSize(String type, int ordinal) {
        return 0;
    }

    @Override
    public boolean handleSetContainsElement(String type, int ordinal, int elementOrdinal, int elementOrdinalHashCode) {
        return false;
    }

    @Override
    public int handleSetFindElement(String type, int ordinal, Object... keys) {
        return -1;
    }

    @Override
    public HollowOrdinalIterator handleSetIterator(String type, int ordinal) {
        return EmptyOrdinalIterator.INSTANCE;
    }

    @Override
    public HollowOrdinalIterator handleSetPotentialMatchIterator(String type, int ordinal, int hashCode) {
        return EmptyOrdinalIterator.INSTANCE;
    }

    @Override
    public int handleMapSize(String type, int ordinal) {
        return 0;
    }

    @Override
    public HollowMapEntryOrdinalIterator handleMapOrdinalIterator(String type, int ordinal) {
        return EmptyMapOrdinalIterator.INSTANCE;
    }

    @Override
    public HollowMapEntryOrdinalIterator handleMapPotentialMatchOrdinalIterator(String type, int ordinal, int keyHashCode) {
        return EmptyMapOrdinalIterator.INSTANCE;
    }

    @Override
    public int handleMapGet(String type, int ordinal, int keyOrdinal, int keyOrdinalHashCode) {
        return -1;
    }

    @Override
    public int handleMapFindKey(String type, int ordinal, Object... keys) {
        return -1;
    }

    @Override
    public int handleMapFindValue(String type, int ordinal, Object... keys) {
        return -1;
    }

    @Override
    public long handleMapFindEntry(String type, int ordinal, Object... keys) {
        return -1L;
    }

    @Override
    public HollowSchema handleSchema(String type) {
        throw new UnsupportedOperationException("By default, missing types are not handled.");
    }

}
