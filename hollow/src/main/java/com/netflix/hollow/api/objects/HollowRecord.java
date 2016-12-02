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
package com.netflix.hollow.api.objects;

import com.netflix.hollow.core.schema.HollowSchema;

import com.netflix.hollow.api.objects.delegate.HollowRecordDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

/**
 * A HollowRecord is the base interface for accessing data from any kind of record
 * in a Hollow dataset.
 */
public interface HollowRecord {

    public int getOrdinal();

    public HollowSchema getSchema();

    public HollowTypeDataAccess getTypeDataAccess();

    public HollowRecordDelegate getDelegate();

}
