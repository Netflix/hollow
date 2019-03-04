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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.copy.HollowRecordCopier;
import com.netflix.hollow.tools.combine.IdentityOrdinalRemapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
     * Reads a schema file into the provided HollowWriteStateEngine. The schema file must be on the classpath.
     *
     * @param schemaFilePath the path to the schema
     * @param engine the write state engine
     * @throws IOException if the schema could not be read
     */
    public static void readSchemaFileIntoWriteState(String schemaFilePath, HollowWriteStateEngine engine)
            throws IOException {
        InputStream input = null;
        try {
            input = HollowWriteStateCreator.class.getClassLoader().getResourceAsStream(schemaFilePath);
            Collection<HollowSchema> schemas =
                HollowSchemaParser.parseCollectionOfSchemas(new BufferedReader(new InputStreamReader(input)));
            populateStateEngineWithTypeWriteStates(engine, schemas);
        } finally {
            if (input != null) {
                input.close();
            }
        }
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
    
    /**
     * Recreate a {@link HollowWriteStateEngine} which can be used to write a snapshot of or continue
     * a delta chain from the supplied {@link HollowReadStateEngine}.
     * <p>
     * The returned state engine will be ready to write a snapshot which will exactly recreate the data in the supplied {@link HollowReadStateEngine}.
     * A delta chain may be continued from this state by calling {@link HollowWriteStateEngine#prepareForNextCycle()}.
     * 
     * @param readEngine the read state engine
     * @return the write state engine
     */
    public static HollowWriteStateEngine recreateAndPopulateUsingReadEngine(final HollowReadStateEngine readEngine) {
        final HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        
        populateStateEngineWithTypeWriteStates(writeEngine, readEngine.getSchemas());
        populateUsingReadEngine(writeEngine, readEngine);
        
        return writeEngine;
    }

    /**
     * Populate the supplied {@link HollowWriteStateEngine} with all of the records from the supplied {@link HollowReadStateEngine}.
     * <ul>
     *   <li>If fields or types have been removed, then those are ignored when copying records.</li>
     *   <li>If fields have been added to existing types, those fields will be null in the copied records.</li>
     *   <li>If types have been added, those types will have no records.</li>
     * </ul>
     * <p>
     * The supplied HollowWriteStateEngine must be newly created, initialized with a data model, and empty.  After this call, 
     * the write engine will be ready to write a snapshot which will exactly recreate the data in the supplied {@link HollowReadStateEngine}, 
     * except with the data model which was initialized.
     * <p>
     * A delta chain may be continued from this state by calling {@link HollowWriteStateEngine#prepareForNextCycle()}.
     *
     * @param writeEngine the write state engine
     * @param readEngine the read state engine
     */
    public static void populateUsingReadEngine(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) {
        populateUsingReadEngine(writeEngine, readEngine, true);
    }

    public static void populateUsingReadEngine(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, boolean preserveHashPositions) {
        SimultaneousExecutor executor = new SimultaneousExecutor(HollowWriteStateCreator.class, "populate");
        
        for(HollowTypeWriteState writeState : writeEngine.getOrderedTypeStates()) {
            if(writeState.getPopulatedBitSet().cardinality() != 0 || writeState.getPreviousCyclePopulatedBitSet().cardinality() != 0)
                throw new IllegalStateException("The supplied HollowWriteStateEngine is already populated!");
        }
        
        for(final HollowTypeReadState readState : readEngine.getTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    HollowTypeWriteState writeState = writeEngine.getTypeState(readState.getSchema().getName());
                    
                    if(writeState != null) {
                        writeState.setNumShards(readState.numShards());
                        
                        HollowRecordCopier copier = HollowRecordCopier.createCopier(readState, writeState.getSchema(), IdentityOrdinalRemapper.INSTANCE, preserveHashPositions);
                        
                        BitSet populatedOrdinals = readState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

                        writeState.resizeOrdinalMap(populatedOrdinals.cardinality());
                        int ordinal = populatedOrdinals.nextSetBit(0);
                        while(ordinal != -1) {
                            HollowWriteRecord rec = copier.copy(ordinal);
                            writeState.mapOrdinal(rec, ordinal, false, true);
                            
                            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
                        }
                        
                        writeState.recalculateFreeOrdinals();
                    }
                }
            });
        }
        
        try {
            executor.awaitSuccessfulCompletion();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        writeEngine.addHeaderTags(readEngine.getHeaderTags());
        writeEngine.overrideNextStateRandomizedTag(readEngine.getCurrentRandomizedTag());
        writeEngine.prepareForWrite();
    }
}
