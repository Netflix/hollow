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
package com.netflix.hollow.core.read.dataaccess;

import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;

/**
 * A handle for all of the records of a specific MAP type in a Hollow dataset.  The most common type of {@link HollowMapTypeDataAccess}
 * is a {@link HollowMapTypeReadState}.
 * 
 * @see HollowMapSchema
 */
public interface HollowMapTypeDataAccess extends HollowTypeDataAccess {

    public HollowMapSchema getSchema();

    public int size(int ordinal);

    public int get(int ordinal, int keyOrdinal);

    public int get(int ordinal, int keyOrdinal, int hashCode);

    public int findKey(int ordinal, Object... hashKey);

    public int findValue(int ordinal, Object... hashKey);

    public long findEntry(int ordinal, Object... hashKey);

    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode);

    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal);

    public long relativeBucket(int ordinal, int bucketIndex);

}
