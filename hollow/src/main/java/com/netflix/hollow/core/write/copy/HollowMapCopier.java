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

import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIteratorImpl;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.tools.combine.OrdinalRemapper;

public class HollowMapCopier extends HollowRecordCopier {

    public HollowMapCopier(HollowMapTypeReadState readTypeState, OrdinalRemapper ordinalRemapper, boolean preserveHashPositions) {
        super(readTypeState, new HollowMapWriteRecord(preserveHashPositions ? UNMIXED_HASHES : MIXED_HASHES), ordinalRemapper, preserveHashPositions);
    }

    @Override
    public HollowWriteRecord copy(int ordinal) {
        HollowMapWriteRecord rec = rec();
        rec.reset();

        HollowMapEntryOrdinalIterator iter = readState().ordinalIterator(ordinal);
        String keyType = readState().getSchema().getKeyType();
        String valueType = readState().getSchema().getValueType();

        while(iter.next()) {
            int remappedKeyOrdinal = ordinalRemapper.getMappedOrdinal(keyType, iter.getKey());
            int remappedValueOrdinal = ordinalRemapper.getMappedOrdinal(valueType, iter.getValue());
            int hashCode = preserveHashPositions ? ((HollowMapEntryOrdinalIteratorImpl) iter).getCurrentBucket() : remappedKeyOrdinal;
            rec.addEntry(remappedKeyOrdinal, remappedValueOrdinal, hashCode);
        }

        return rec;
    }

    private HollowMapTypeReadState readState() {
        return (HollowMapTypeReadState) readTypeState;
    }

    private HollowMapWriteRecord rec() {
        return (HollowMapWriteRecord) writeRecord;
    }

}
