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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.BitSet;
import java.util.Collection;

/**
 * Use to pre-populate, or create a {@link HollowWriteStateEngine} which is pre-populated, with a particular data model.  
 */
public class HollowWriteStateCreator {

    /**
     * @param schemas The schemas from the data model
     * @return a write state engine which is pre-populated with the specified data model. 
     */
    public static HollowWriteStateEngine createWithSchemas(Collection<HollowSchema> schemas) {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();

        populateStateEngineWithTypeWriteStates(stateEngine, schemas);

        return stateEngine;
    }

    /**
     * Pre-populate a {@link HollowWriteStateEngine} with a specified data model.
     * 
     * @param stateEngine The state engine to pre-populate
     * @param schemas The schemas from the data model.
     */
    public static void populateStateEngineWithTypeWriteStates(HollowWriteStateEngine stateEngine, Collection<HollowSchema> schemas) {

        for(HollowSchema schema : schemas) {
            if(stateEngine.getTypeState(schema.getName()) == null) {
                switch(schema.getSchemaType()) {
                case OBJECT:
                    stateEngine.addTypeState(new HollowObjectTypeWriteState((HollowObjectSchema)schema));
                    break;
                case LIST:
                    stateEngine.addTypeState(new HollowListTypeWriteState((HollowListSchema)schema));
                    break;
                case SET:
                    stateEngine.addTypeState(new HollowSetTypeWriteState((HollowSetSchema)schema));
                    break;
                case MAP:
                    stateEngine.addTypeState(new HollowMapTypeWriteState((HollowMapSchema)schema));
                    break;
                }
            }
        }
    }
    
    public static HollowWriteStateEngine recreateAndPopulateUsingReadEngine(final HollowReadStateEngine readEngine) {
        final HollowWriteStateEngine writeEngine = createWithSchemas(readEngine.getSchemas());
        
        SimultaneousExecutor executor = new SimultaneousExecutor();
        
        for(final HollowTypeReadState readState : readEngine.getTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    HollowTypeWriteState writeState = writeEngine.getTypeState(readState.getSchema().getName());
                    
                    HollowRecordCopier copier = HollowRecordCopier.createCopier(readState);
                    
                    BitSet populatedOrdinals = readState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
                    
                    int ordinal = populatedOrdinals.nextSetBit(0);
                    while(ordinal != -1) {
                        HollowWriteRecord rec = copier.copy(ordinal);
                        writeState.mapOrdinal(rec, ordinal, false, true);
                        
                        ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
                    }
                    
                    writeState.recalculateFreeOrdinals();
                }
            });
        }
        
        try {
            executor.awaitSuccessfulCompletion();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        writeEngine.overrideNextStateRandomizedTag(readEngine.getCurrentRandomizedTag());
        writeEngine.prepareForWrite();
        
        return writeEngine;
    }

}
