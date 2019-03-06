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
package com.netflix.hollow.core.type;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.type.delegate.FloatDelegate;

public class HFloat extends HollowObject {

    public HFloat(FloatDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public float getValue() {
        return delegate().getValue(ordinal);
    }

    public Float getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public FloatTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FloatDelegate delegate() {
        return (FloatDelegate)delegate;
    }
}