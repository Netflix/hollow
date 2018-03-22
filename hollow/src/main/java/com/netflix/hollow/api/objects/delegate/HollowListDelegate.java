/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for LIST type records.
 * 
 * @see HollowRecordDelegate
 */
public interface HollowListDelegate<T> extends HollowRecordDelegate {

    public int size(int ordinal);

    public T get(HollowList<T> list, int ordinal, int index);

    public boolean contains(HollowList<T> list, int ordinal, Object o);

    public int indexOf(HollowList<T> list, int ordinal, Object o);

    public int lastIndexOf(HollowList<T> list, int ordinal, Object o);

    public HollowListSchema getSchema();

    public HollowListTypeDataAccess getTypeDataAccess();

    public HollowListTypeAPI getTypeAPI();

}
