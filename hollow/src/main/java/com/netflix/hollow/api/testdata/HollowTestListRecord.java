/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.testdata;

import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.List;

public abstract class HollowTestListRecord<T> extends HollowTestRecord<T> {

    private final List<HollowTestRecord<?>> elements = new ArrayList<>();

    protected HollowTestListRecord(T parent) {
        super(parent);
    }

    protected void addElement(HollowTestRecord<?> element) {
        elements.add(element);
    }

    @SuppressWarnings({"hiding", "unchecked"})
    public <T extends HollowTestRecord<?>> T getRecord(int idx) {
        return (T) elements.get(idx);
    }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(HollowTestRecord<?> e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }
}
