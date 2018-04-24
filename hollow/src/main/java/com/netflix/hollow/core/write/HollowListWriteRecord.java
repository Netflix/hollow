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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.memory.ByteDataBuffer;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.util.IntList;

public class HollowListWriteRecord implements HollowWriteRecord {

    private final IntList elementOrdinals;

    public HollowListWriteRecord() {
        this.elementOrdinals = new IntList();
    }

    public void addElement(int ordinal) {
        elementOrdinals.add(ordinal);
    }

    @Override
    public void writeDataTo(ByteDataBuffer buf) {
        VarInt.writeVInt(buf, elementOrdinals.size());

        for(int i=0;i<elementOrdinals.size();i++) {
            VarInt.writeVInt(buf, elementOrdinals.get(i));
        }
    }

    @Override
    public void reset() {
        elementOrdinals.clear();
    }

}
