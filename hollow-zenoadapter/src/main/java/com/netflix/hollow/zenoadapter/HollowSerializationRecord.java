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
package com.netflix.hollow.zenoadapter;

import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.zeno.serializer.NFSerializationRecord;

public class HollowSerializationRecord extends NFSerializationRecord {

    private final String typeName;
    private final HollowWriteRecord hollowWriteRecord;

    public HollowSerializationRecord(HollowWriteRecord rec, String typeName) {
        this.hollowWriteRecord = rec;
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public HollowWriteRecord getHollowWriteRecord() {
        return hollowWriteRecord;
    }

    public void reset() {
        hollowWriteRecord.reset();
    }

}
