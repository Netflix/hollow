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
package com.netflix.hollow.tools.split;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.combine.OrdinalRemapper;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HollowSplitterShardCopier {

    private final Logger log = Logger.getLogger(HollowSplitterShardCopier.class.getName());
    private final HollowReadStateEngine input;
    private final HollowWriteStateEngine output;
    private final OrdinalRemapper ordinalRemapper;
    private final HollowSplitterCopyDirector director;
    private final int shardNumber;

    private final Map<String, HollowRecordCopier> copiersPerType;

    public HollowSplitterShardCopier(HollowReadStateEngine input, HollowWriteStateEngine shardOutput, HollowSplitterCopyDirector director, int shardNumber) {
        this.input = input;
        this.output = shardOutput;
        this.director = director;
        this.shardNumber = shardNumber;
        this.ordinalRemapper = new HollowSplitterOrdinalRemapper(input, this);
        this.copiersPerType = new HashMap<String, HollowRecordCopier>();
    }

    public void copy() {
        for(String topLevelType : director.getTopLevelTypes()) {
            HollowTypeReadState inputTypeState = input.getTypeState(topLevelType);

            if(inputTypeState == null) {
                log.warning("Could not find input type state for " + topLevelType);
                continue;
            }

            PopulatedOrdinalListener listener = inputTypeState.getListener(PopulatedOrdinalListener.class);
            BitSet ordinals = listener.getPopulatedOrdinals();

            int ordinal = ordinals.nextSetBit(0);
            while(ordinal != -1) {
                int directedShard = director.getShard(inputTypeState, ordinal);
                if(directedShard == shardNumber || directedShard < 0) {
                    copyRecord(topLevelType, ordinal);
                }

                ordinal = ordinals.nextSetBit(ordinal + 1);
            }
        }
    }

    int copyRecord(String typeName, int ordinal) {
        HollowTypeReadState typeState = input.getTypeState(typeName);
        HollowRecordCopier copier = copiersPerType.get(typeName);
        if(copier == null) {
            copier = HollowRecordCopier.createCopier(typeState, ordinalRemapper, isDefinedHashCode(typeState));
            copiersPerType.put(typeName, copier);
        }

        HollowWriteRecord rec = copier.copy(ordinal);
        return output.add(typeName, rec);
    }

    private boolean isDefinedHashCode(HollowTypeReadState typeState) {
        if(typeState instanceof HollowSetTypeReadState)
            return input.getTypesWithDefinedHashCodes().contains(((HollowSetSchema) typeState.getSchema()).getElementType());
        if(typeState instanceof HollowMapTypeReadState)
            return input.getTypesWithDefinedHashCodes().contains(((HollowMapSchema) typeState.getSchema()).getKeyType());
        return false;
    }

}
