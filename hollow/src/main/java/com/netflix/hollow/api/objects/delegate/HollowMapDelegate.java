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
package com.netflix.hollow.api.objects.delegate;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import java.util.Map;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for MAP type records.
 * 
 * @see HollowRecordDelegate
 */
public interface HollowMapDelegate<K, V> extends HollowRecordDelegate {

    public int size(int ordinal);

    public V get(HollowMap<K, V> map, int ordinal, Object key);

    public boolean containsKey(HollowMap<K, V> map, int ordinal, Object key);

    public boolean containsValue(HollowMap<K, V> map, int ordinal, Object value);

    public K findKey(HollowMap<K, V> map, int ordinal, Object... hashKey);

    public V findValue(HollowMap<K, V> map, int ordinal, Object... hashKey);

    public Map.Entry<K, V> findEntry(HollowMap<K, V> map, int ordinal, Object... hashKey);

    public HollowMapEntryOrdinalIterator iterator(int ordinal);

    public HollowMapSchema getSchema();

    public HollowMapTypeDataAccess getTypeDataAccess();

    public HollowMapTypeAPI getTypeAPI();
}
