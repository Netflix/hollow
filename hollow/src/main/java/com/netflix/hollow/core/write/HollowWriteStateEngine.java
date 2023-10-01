/*
 *  Copyright 2016-2021 Netflix, Inc.
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

import com.netflix.hollow.api.error.HollowWriteStateException;
import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A {@link HollowWriteStateEngine} is our main handle to a Hollow dataset as a data producer.
 * <p>
 * A dataset changes over time.  A core concept in Hollow is that the timeline for a changing dataset can be 
 * broken down into discrete data states, each of which is a complete snapshot of the data at a particular point in time.
 * Data producers handle data states with a HollowWriteStateEngine.
 * <p>
 * A HollowWriteStateEngine cycles back and forth between two-phases:
 * <ol>
 * <li>Adding records</li>
 * <li>Writing the state</li>
 * </ol>
 * <p>
 * During the "adding records" phase, all of the records comprising the dataset are added to the state engine.  During the
 * "writing" phase, we can write snapshot blobs to initialize and/or delta blobs to keep up-to-date consumers of the dataset.
 * <p>
 * Each cycle between the phases will produce a state.  During each cycle, all of the current records in the dataset should
 * be re-added to the write state engine. 
 */
public class HollowWriteStateEngine implements HollowStateEngine {

    private final Logger log = Logger.getLogger(HollowWriteStateEngine.class.getName());

    private final Map<String, HollowTypeWriteState> writeStates;
    private final Map<String, HollowSchema> hollowSchemas;
    private final List<HollowTypeWriteState> orderedTypeStates;
    private final Map<String,String> headerTags = new ConcurrentHashMap<>();
    private final Map<String,String> previousHeaderTags = new ConcurrentHashMap<>();
    private final HollowObjectHashCodeFinder hashCodeFinder;
    
    //// target a maximum shard size to reduce excess memory pool requirement 
    private long targetMaxTypeShardSize = Long.MAX_VALUE;
    //// focus filling ordinal holes in as few shards as possible to make delta application more efficient for consumers
    private boolean focusHoleFillInFewestShards = false;

    private List<String> restoredStates;
    private boolean preparedForNextCycle = true;
    private long previousStateRandomizedTag = -1L;
    private long nextStateRandomizedTag;

    public HollowWriteStateEngine() {
        this(new DefaultHashCodeFinder());
    }

    @Deprecated
    public HollowWriteStateEngine(HollowObjectHashCodeFinder hasher) {
        this.writeStates = new HashMap<String, HollowTypeWriteState>();
        this.hollowSchemas = new HashMap<String, HollowSchema>();
        this.orderedTypeStates = new ArrayList<HollowTypeWriteState>();
        this.hashCodeFinder = hasher;
        this.nextStateRandomizedTag = mintNewRandomizedStateTag();
    }

    /**
     * Add a record to the state. 
     * @param type the type name
     * @param rec the record
     * @return the ordinal of the added record
     */
    public int add(String type, HollowWriteRecord rec) {
        HollowTypeWriteState hollowTypeWriteState = writeStates.get(type);
        if(hollowTypeWriteState == null)
            throw new IllegalArgumentException("Type " + type + " does not exist!");
        return hollowTypeWriteState.add(rec);
    }

    /**
     * Add a type to the dataset.  Should be called during the first cycle, before writing the first state.
     * @param writeState the write state to add
     */
    public synchronized void addTypeState(HollowTypeWriteState writeState) {
        HollowSchema schema = writeState.getSchema();

        if(writeStates.containsKey(schema.getName()))
            throw new IllegalStateException("The state for type " + schema.getName() + " has already been added!");

        hollowSchemas.put(schema.getName(), schema);
        writeStates.put(schema.getName(), writeState);
        orderedTypeStates.add(writeState);
        writeState.setStateEngine(this);
    }

    /**
     * Restore from the data state contained in the provided {@link HollowReadStateEngine}.  This is used to continue
     * a delta chain after a producer is restarted.
     * <p>
     * Before calling this method, the data model should be pre-initialized.  This can be accomplished by:
     * <ul>
     * <li>using the {@link HollowWriteStateCreator}</li>
     * <li>calling {@link HollowObjectMapper#initializeTypeState(Class)} with each of the top-level classes in the data model</li>
     * <li>adding the types via {@link #addTypeState(HollowTypeWriteState)}</li>
     * </ul>
     * @param readStateEngine the read state to restore from
     */
    public void restoreFrom(HollowReadStateEngine readStateEngine) {
        if(!readStateEngine.isListenToAllPopulatedOrdinals())
            throw new IllegalStateException("The specified HollowReadStateEngine must be listening for all populated ordinals!");

        for(HollowTypeReadState readState : readStateEngine.getTypeStates()) {
            String typeName = readState.getSchema().getName();
            HollowTypeWriteState writeState = writeStates.get(typeName);

            if(writeState != null) {
                if(writeState.getNumShards() == -1)
                    writeState.numShards = readState.numShards();
                else if(writeState.getNumShards() != readState.numShards()) {
                    String msg = "Attempting to restore from a HollowReadStateEngine with numShards " + readState.numShards()
                            + " for type " + typeName + " to write state engine with numShards " + writeState.getNumShards();
                    throw new IllegalStateException(msg);
                }
            }
        }
        
        restoredStates = new ArrayList<String>();

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "restore");

        for(final HollowTypeReadState readState : readStateEngine.getTypeStates()) {
            final String typeName = readState.getSchema().getName();
            final HollowTypeWriteState writeState = writeStates.get(typeName);
            
            restoredStates.add(typeName);
            
            if(writeState != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        log.info("RESTORE: " + typeName);
                        writeState.restoreFrom(readState);
                    }
                });
            }
        }

        previousStateRandomizedTag = readStateEngine.getCurrentRandomizedTag();
        nextStateRandomizedTag = mintNewRandomizedStateTag();
        overridePreviousHeaderTags(readStateEngine.getHeaderTags());

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Exception e){
            throw new HollowWriteStateException("Unable to restore write state from read state engine", e);
        }
    }

    /**
     * Transition from the "adding records" phase of a cycle to the "writing" phase of a cycle.
     */
    public void prepareForWrite() {
        if(!preparedForNextCycle)  // this call should be a no-op if we are already prepared for write
            return;

        addTypeNamesWithDefinedHashCodesToHeader();

        try {
            SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "prepare-for-write");

            for(final Map.Entry<String, HollowTypeWriteState> typeStateEntry : writeStates.entrySet()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        typeStateEntry.getValue().prepareForWrite();
                    }
                });
            }

            executor.awaitSuccessfulCompletion();
        } catch(Exception ex) {
            throw new HollowWriteStateException("Failed to prepare for write", ex);
        }

        preparedForNextCycle = false;
    }

    /**
     * Transition from the "writing" phase of a cycle to the "adding records" phase of the next cycle.
     */
    public void prepareForNextCycle() {
        if(preparedForNextCycle)  // this call should be a no-op if we are already prepared for the next cycle
            return;

        previousStateRandomizedTag = nextStateRandomizedTag;
        nextStateRandomizedTag = mintNewRandomizedStateTag();
        overridePreviousHeaderTags(headerTags);

        try {
            SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "prepare-for-next-cycle");

            for(final Map.Entry<String, HollowTypeWriteState> typeStateEntry : writeStates.entrySet()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        typeStateEntry.getValue().prepareForNextCycle();
                    }
                });
            }

            executor.awaitSuccessfulCompletion();
        } catch(Exception ex) {
            throw new HollowWriteStateException("Failed to prepare for next cycle", ex);
        }

        preparedForNextCycle = true;
        restoredStates = null;
    }

    /**
     * Add all of the objects from the previous cycle, exactly as they were in the previous cycle.
     */
    public void addAllObjectsFromPreviousCycle() {
        for(HollowTypeWriteState typeState : orderedTypeStates) {
            typeState.addAllObjectsFromPreviousCycle();
        }
    }

    /**
     * If a state was partially constructed after the last call to prepareForNextCycle(), this call
     * will remove all of those objects from the state engine and reset to the state it was in at the
     * last prepareForNextCycle() call.
     * <p>
     * This method can be called at any time, and will leave the state engine in the same state it was in immediately
     * after the last call to {@link #prepareForNextCycle()}
     */
    public void resetToLastPrepareForNextCycle() {
        
        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "reset-to-last-prepare-for-next-cycle");

        for(final Map.Entry<String, HollowTypeWriteState> typeStateEntry : writeStates.entrySet()) {
            executor.execute(new Runnable() {
                public void run() {
                    typeStateEntry.getValue().resetToLastPrepareForNextCycle();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Exception ex) {
            throw new HollowWriteStateException("Unable to reset to the prior version of the write state", ex);
        }
        
        /// recreate a new randomized tag, to avoid any potential conflict with aborted versions
        nextStateRandomizedTag = mintNewRandomizedStateTag();
        preparedForNextCycle = true;
        
    }

    /**
     * @return whether or not there are differences between the current cycle and the previous cycle.
     */
    public boolean hasChangedSinceLastCycle() {
        for(Map.Entry<String, HollowTypeWriteState> typeStateEntry : writeStates.entrySet()) {
            if(typeStateEntry.getValue().hasChangedSinceLastCycle())
                return true;
        }
        return false;
    }
    
    public boolean isRestored() {
        return restoredStates != null;
    }
    
    void ensureAllNecessaryStatesRestored() {
        if(!isRestored())
            return;
        
        List<String> unrestoredStates = new ArrayList<String>();
        
        for(HollowTypeWriteState typeState : orderedTypeStates) {
            if(restoredStates.contains(typeState.getSchema().getName())) {
                if(!typeState.isRestored())
                    unrestoredStates.add(typeState.getSchema().getName());
            }
        }
        
        if(!unrestoredStates.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Current state was restored but contains unrestored state for top-level types %s. " +
                    "Those types need to be registered with the producer (see HollowProducer.initializeDataModel)",
                    unrestoredStates));
        }
    }

    public List<HollowTypeWriteState> getOrderedTypeStates() {
        return orderedTypeStates;
    }

    /**
     * @param typeName the type name
     * @return the specified {@link HollowTypeWriteState}
     */
    public HollowTypeWriteState getTypeState(String typeName) {
        return writeStates.get(typeName);
    }

    @Override
    public List<HollowSchema> getSchemas() {
        List<HollowSchema> schemas = new ArrayList<HollowSchema>();

        for(HollowTypeWriteState typeState : orderedTypeStates) {
            schemas.add(typeState.getSchema());
        }

        return schemas;
    }

    @Override
    public HollowSchema getSchema(String schemaName) {
        return hollowSchemas.get(schemaName);
    }

    @Override
    public HollowSchema getNonNullSchema(String schemaName) {
        HollowSchema schema = getSchema(schemaName);
        if (schema == null) {
            List<String> schemas = new ArrayList<>();
            for (HollowSchema s : getSchemas()) {
                schemas.add(s.getName());
            }
            throw new SchemaNotFoundException(schemaName, schemas);
        }
        return schema;
    }

    @Override
    public Map<String, String> getHeaderTags() {
        return headerTags;
    }

    public void addHeaderTag(String name, String value) {
        headerTags.put(name, value);
    }

    public void addHeaderTags(Map<String,String> headerTags) {
        this.headerTags.putAll(headerTags);
    }

    public Map<String, String> getPreviousHeaderTags() {
        return previousHeaderTags;
    }

    @Override
    public String getHeaderTag(String name) {
        return headerTags.get(name);
    }

    @Deprecated
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        return hashCodeFinder;
    }

    public long getPreviousStateRandomizedTag() {
        return previousStateRandomizedTag;
    }
    
    public void overridePreviousStateRandomizedTag(long previousStateRandomizedTag) {
        this.previousStateRandomizedTag = previousStateRandomizedTag;
    }

    public void overridePreviousHeaderTags(Map<String, String> previousHeaderTags) {
        this.previousHeaderTags.clear();
        this.previousHeaderTags.putAll(previousHeaderTags);
    }
    
    public long getNextStateRandomizedTag() {
        return nextStateRandomizedTag;
    }
    
    public void overrideNextStateRandomizedTag(long nextStateRandomizedTag) {
    	this.nextStateRandomizedTag = nextStateRandomizedTag;
    }
    
    /**
     * Setting a target max type shard size (specified in bytes) will limit the excess memory pool required to perform delta transitions.
     * 
     * This value defaults to  (16 * 1024 * 1024).
     *
     * @param targetMaxTypeShardSize the target max type shard size, in bytes
     */
    public void setTargetMaxTypeShardSize(long targetMaxTypeShardSize) {
        this.targetMaxTypeShardSize = targetMaxTypeShardSize;
    }
    
    long getTargetMaxTypeShardSize() {
        return targetMaxTypeShardSize;
    }
    
    /**
     * Experimental: Setting this will focus the holes returned by the FreeOrdinalTracker for each state into as few shards as possible.
     *
     * This can be used by the consumers to reduce the work necessary to apply a delta, by skipping recreation of shards where no records are added.
     */
    public void setFocusHoleFillInFewestShards(boolean focusHoleFillInFewestShards) {
        this.focusHoleFillInFewestShards = focusHoleFillInFewestShards;
    }

    boolean isFocusHoleFillInFewestShards() {
        return focusHoleFillInFewestShards;
    }

    private long mintNewRandomizedStateTag() {
        Random rand = new Random();
        
        long newTag = rand.nextLong();
        while((newTag & HollowTypeMapper.ASSIGNED_ORDINAL_CYCLE_MASK) == 0 ||
              (newTag & HollowTypeMapper.ASSIGNED_ORDINAL_CYCLE_MASK) == HollowTypeMapper.ASSIGNED_ORDINAL_CYCLE_MASK ||
              (newTag & HollowTypeMapper.ASSIGNED_ORDINAL_CYCLE_MASK) == (previousStateRandomizedTag & HollowTypeMapper.ASSIGNED_ORDINAL_CYCLE_MASK))
            newTag = rand.nextLong();
        
        return newTag;
    }

    private void addTypeNamesWithDefinedHashCodesToHeader() {
        Set<String> typeNames = hashCodeFinder.getTypesWithDefinedHashCodes();
        if(typeNames != null && !typeNames.isEmpty()) {
            StringBuilder typeNamesBuilder = new StringBuilder();
            int counter = 0;

            // Sort to be consistent between cycle
            Set<String> sortedNames = new TreeSet<String>(typeNames);
            for (String typeName : sortedNames) {
                if(counter++ != 0)
                    typeNamesBuilder.append(",");
                typeNamesBuilder.append(typeName);
            }

            addHeaderTag(HollowObjectHashCodeFinder.DEFINED_HASH_CODES_HEADER_NAME, typeNamesBuilder.toString());
        }
    }
}
