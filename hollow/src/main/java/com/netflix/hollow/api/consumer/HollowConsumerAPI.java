/*
 *  Copyright 2017 Netflix, Inc.
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
 */
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.core.type.HBoolean;
import com.netflix.hollow.core.type.HDouble;
import com.netflix.hollow.core.type.HFloat;
import com.netflix.hollow.core.type.HInteger;
import com.netflix.hollow.core.type.HLong;
import com.netflix.hollow.core.type.HString;
import java.util.Collection;

public interface HollowConsumerAPI {

    public Collection<HBoolean> getAllHBoolean();

    public HBoolean getHBoolean(int ordinal);

    public Collection<HDouble> getAllHDouble();

    public HDouble getHDouble(int ordinal);

    public Collection<HFloat> getAllHFloat();

    public HFloat getHFloat(int ordinal);

    public Collection<HInteger> getAllHInteger();

    public HInteger getHInteger(int ordinal);

    public Collection<HLong> getAllHLong();

    public HLong getHLong(int ordinal);

    public Collection<HString> getAllHString();

    public HString getHString(int ordinal);
}
