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

import com.netflix.hollow.api.custom.HollowSetTypeAPI;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for SET type records.
 * 
 * @see HollowRecordDelegate
 */
public interface HollowSetDelegate<T> extends HollowRecordDelegate {

    public int size(int ordinal);

    public boolean contains(HollowSet<T> set, int ordinal, Object o);

    public T findElement(HollowSet<T> set, int ordinal, Object... keys);

    public HollowOrdinalIterator iterator(int ordinal);

    public HollowSetSchema getSchema();

    public HollowSetTypeDataAccess getTypeDataAccess();

    public HollowSetTypeAPI getTypeAPI();
}
