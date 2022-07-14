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

import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.MIXED_HASHES;
import static com.netflix.hollow.core.write.HollowHashableWriteRecord.HashBehavior.UNMIXED_HASHES;

import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowSetOrdinalIterator;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.tools.combine.OrdinalRemapper;

public class HollowSetCopier extends HollowRecordCopier {

    public HollowSetCopier(HollowSetTypeReadState typeState, OrdinalRemapper ordinalRemapper, boolean preserveHashPositions) {
        super(typeState, new HollowSetWriteRecord(preserveHashPositions ? UNMIXED_HASHES : MIXED_HASHES), ordinalRemapper, preserveHashPositions);
    }

    @Override
    public HollowWriteRecord copy(int ordinal) {
        HollowSetWriteRecord rec = rec();
        rec.reset();

        String elementType = readState().getSchema().getElementType();

        HollowOrdinalIterator ordinalIterator = readState().ordinalIterator(ordinal);

        int elementOrdinal = ordinalIterator.next();

        while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            int remappedElementOrdinal = ordinalRemapper.getMappedOrdinal(elementType, elementOrdinal);
            int hashCode = preserveHashPositions ? ((HollowSetOrdinalIterator) ordinalIterator).getCurrentBucket() : remappedElementOrdinal;
            rec.addElement(remappedElementOrdinal, hashCode);
            elementOrdinal = ordinalIterator.next();
        }

        return rec;
    }

    private HollowSetTypeReadState readState() {
        return (HollowSetTypeReadState) readTypeState;
    }

    private HollowSetWriteRecord rec() {
        return (HollowSetWriteRecord) writeRecord;
    }

}
