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

import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSchema;

/**
 * A MissingDataHandler specifies what to do when a generated API contains methods to access fields which do not
 * exist in the loaded Hollow dataset.
 */
public interface MissingDataHandler {

    ///// OBJECT /////

    public boolean handleIsNull(String type, int ordinal, String field);

    public Boolean handleBoolean(String type, int ordinal, String field);

    public int handleReferencedOrdinal(String type, int ordinal, String field);

    public int handleInt(String type, int ordinal, String field);

    public long handleLong(String type, int ordinal, String field);

    public float handleFloat(String type, int ordinal, String field);

    public double handleDouble(String type, int ordinal, String field);

    public String handleString(String type, int ordinal, String field);

    public boolean handleStringEquals(String type, int ordinal, String field, String testValue);

    public byte[] handleBytes(String type, int ordinal, String field);

    ///// LIST /////

    public int handleListSize(String type, int ordinal);

    public int handleListElementOrdinal(String type, int ordinal, int idx);

    public HollowOrdinalIterator handleListIterator(String type, int ordinal);

    ///// SET /////

    public int handleSetSize(String type, int ordinal);

    public HollowOrdinalIterator handleSetIterator(String type, int ordinal);

    public HollowOrdinalIterator handleSetPotentialMatchIterator(String type, int ordinal, int hashCode);

    public boolean handleSetContainsElement(String type, int ordinal, int elementOrdinal, int elementOrdinalHashCode);

    public int handleSetFindElement(String type, int ordinal, Object... keys);

    ///// MAP /////

    public int handleMapSize(String type, int ordinal);

    public HollowMapEntryOrdinalIterator handleMapOrdinalIterator(String type, int ordinal);

    public HollowMapEntryOrdinalIterator handleMapPotentialMatchOrdinalIterator(String type, int ordinal, int keyHashCode);

    public int handleMapGet(String type, int ordinal, int keyOrdinal, int keyOrdinalHashCode);

    public int handleMapFindKey(String type, int ordinal, Object... keys);

    public int handleMapFindValue(String type, int ordinal, Object... keys);

    public long handleMapFindEntry(String type, int ordinal, Object... keys);


    ///// SCHEMA /////

    public HollowSchema handleSchema(String type);

}
