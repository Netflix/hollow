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
package com.netflix.hollow.core.write.copy;

import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.tools.combine.OrdinalRemapper;

public class HollowListCopier extends HollowRecordCopier {

    public HollowListCopier(HollowListTypeReadState readTypeState, OrdinalRemapper ordinalRemapper) {
        super(readTypeState, new HollowListWriteRecord(), ordinalRemapper, false);
    }

    @Override
    public HollowWriteRecord copy(int ordinal) {
        HollowListWriteRecord rec = rec();
        rec.reset();

        String elementType = readState().getSchema().getElementType();

        int size = readState().size(ordinal);

        for(int i = 0; i < size; i++) {
            int elementOrdinal = readState().getElementOrdinal(ordinal, i);
            int remappedElementOrdinal = ordinalRemapper.getMappedOrdinal(elementType, elementOrdinal);
            rec.addElement(remappedElementOrdinal);
        }

        return rec;
    }

    private HollowListTypeReadState readState() {
        return (HollowListTypeReadState) readTypeState;
    }

    private HollowListWriteRecord rec() {
        return (HollowListWriteRecord) writeRecord;
    }
}
