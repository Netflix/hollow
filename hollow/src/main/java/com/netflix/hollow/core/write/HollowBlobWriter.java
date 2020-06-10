/*
 *  Copyright 2016-2020 Netflix, Inc.
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

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link HollowBlobWriter} is used to serialize snapshot, delta, and reverse delta blobs based on the data state
 * contained in a {@link HollowWriteStateEngine}. 
 */
public class HollowBlobWriter {

    private final HollowWriteStateEngine stateEngine;
    private final HollowBlobHeaderWriter headerWriter;

    public HollowBlobWriter(HollowWriteStateEngine stateEngine) {
        this.stateEngine = stateEngine;
        this.headerWriter = new HollowBlobHeaderWriter();
    }

    /**
     * Write the current state as a snapshot blob.
     * @param os the output stream to write the snapshot blob
     * @throws IOException if the snapshot blob could not be written
     */
    public void writeSnapshot(OutputStream os) throws IOException {
        stateEngine.prepareForWrite();

        DataOutputStream dos = new DataOutputStream(os);
        writeHeader(dos, stateEngine.getSchemas(), BlobHeaderType.SNAPSHOT);

        VarInt.writeVInt(dos, stateEngine.getOrderedTypeStates().size());

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-snapshot");

        for(final HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    typeState.calculateSnapshot();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            HollowSchema schema = typeState.getSchema();
            schema.writeTo(dos);

            writeNumShards(dos, typeState.getNumShards());

            typeState.writeSnapshot(dos);
        }
        os.flush();
    }

    /**
     * Serialize the changes necessary to transition a consumer from the previous state
     * to the current state as a delta blob.
     *
     * @param os the output stream to write the delta blob
     * @throws IOException if the delta blob could not be written
     * @throws IllegalStateException if the current state is restored from the previous state
     * and current state contains unrestored state for one or more types.  This indicates those
     * types have not been declared to the producer as part its initialized data model.
     * @see com.netflix.hollow.api.producer.HollowProducer#initializeDataModel(Class[])
     */
    public void writeDelta(OutputStream os) throws IOException {
        stateEngine.prepareForWrite();
        
        if(stateEngine.isRestored())
            stateEngine.ensureAllNecessaryStatesRestored();

        List<HollowSchema> changedTypes = changedTypes();
        
        DataOutputStream dos = new DataOutputStream(os);
        writeHeader(dos, changedTypes, BlobHeaderType.DELTA);

        VarInt.writeVInt(dos, changedTypes.size());

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-delta");

        for(final HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    if(typeState.hasChangedSinceLastCycle())
                        typeState.calculateDelta();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            if(typeState.hasChangedSinceLastCycle()) {
                HollowSchema schema = typeState.getSchema();
                schema.writeTo(dos);

                writeNumShards(dos, typeState.getNumShards());

                typeState.writeDelta(dos);
            }
        }
        os.flush();
    }

    /**
     * Serialize the changes necessary to transition a consumer from the current state to the
     * previous state as a delta blob.
     *
     * @param os the output stream to write the reverse delta blob
     * @throws IOException if the reverse delta blob could not be written
     * @throws IllegalStateException if the current state is restored from the previous state
     * and current state contains unrestored state for one or more types.  This indicates those
     * types have not been declared to the producer as part its initialized data model.
     * @see com.netflix.hollow.api.producer.HollowProducer#initializeDataModel(Class[])
     */
    public void writeReverseDelta(OutputStream os) throws IOException {
        stateEngine.prepareForWrite();
        
        if(stateEngine.isRestored())
            stateEngine.ensureAllNecessaryStatesRestored();
        
        List<HollowSchema> changedTypes = changedTypes();

        DataOutputStream dos = new DataOutputStream(os);
        writeHeader(dos, changedTypes, BlobHeaderType.REVERSE_DELTA);

        VarInt.writeVInt(dos, changedTypes.size());

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-reverse-delta");

        for(final HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    if(typeState.hasChangedSinceLastCycle())
                        typeState.calculateReverseDelta();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            if(typeState.hasChangedSinceLastCycle()) {
                HollowSchema schema = typeState.getSchema();
                schema.writeTo(dos);

                writeNumShards(dos, typeState.getNumShards());

                typeState.writeReverseDelta(dos);
            }
        }
        os.flush();
    }
    
    /**
     * Serialize the changes necessary to transition a consumer from the latest "hub" state to the
     * current state as a radial delta blob.
     *
     * @param os the output stream to write the radial delta blob
     * @throws IOException if the radial delta blob could not be written
     * @throws IllegalStateException if the current state is restored from the previous state
     * and current state contains unrestored state for one or more types.  This indicates those
     * types have not been declared to the producer as part its initialized data model.
     * @see com.netflix.hollow.api.producer.HollowProducer#initializeDataModel(Class[])
     */
    public void writeRadialDelta(OutputStream os) throws IOException {
        stateEngine.prepareForWrite();
        
        if(stateEngine.isRestored())
            stateEngine.ensureAllNecessaryStatesRestored();
        
        List<HollowSchema> changedTypes = changedTypesSinceHubState();

        DataOutputStream dos = new DataOutputStream(os);
        writeHeader(dos, changedTypes, BlobHeaderType.RADIAL_DELTA);

        VarInt.writeVInt(dos, changedTypes.size());

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-radial-delta");

        for(final HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    if(typeState.hasChangedSinceHubState())
                        typeState.calculateRadialDelta();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for(HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            if(typeState.hasChangedSinceHubState()) {
                HollowSchema schema = typeState.getSchema();
                schema.writeTo(dos);
    
                writeNumShards(dos, typeState.getNumShards());
    
                typeState.writeRadialDelta(dos);
            }
        }
        os.flush();
    }
    

    private List<HollowSchema> changedTypes() {
        List<HollowSchema> changedTypes = new ArrayList<HollowSchema>();
        
        List<HollowTypeWriteState> orderedTypeStates = stateEngine.getOrderedTypeStates();
        for(int i=0;i<orderedTypeStates.size();i++) {
            HollowTypeWriteState writeState = orderedTypeStates.get(i);
            if(writeState.hasChangedSinceLastCycle())
                changedTypes.add(writeState.getSchema());
        }

        return changedTypes;
    }
    
    private List<HollowSchema> changedTypesSinceHubState() {
        List<HollowSchema> changedTypes = new ArrayList<HollowSchema>();
        
        List<HollowTypeWriteState> orderedTypeStates = stateEngine.getOrderedTypeStates();
        for(int i=0;i<orderedTypeStates.size();i++) {
            HollowTypeWriteState writeState = orderedTypeStates.get(i);
            if(writeState.hasChangedSinceHubState())
                changedTypes.add(writeState.getSchema());
        }

        return changedTypes;
    }    
    
    private void writeNumShards(DataOutputStream dos, int numShards) throws IOException {
        VarInt.writeVInt(dos, 1 + VarInt.sizeOfVInt(numShards)); /// pre 2.1.0 forwards compatibility:
                                                                 /// skip new forwards-compatibility and num shards
        
        VarInt.writeVInt(dos, 0); /// 2.1.0 forwards-compatibility, can write number of bytes for older readers to skip here.
        
        VarInt.writeVInt(dos, numShards);
    }

    private void writeHeader(DataOutputStream os, List<HollowSchema> schemasToInclude, BlobHeaderType type) throws IOException {
        HollowBlobHeader header = new HollowBlobHeader();
        header.setHeaderTags(stateEngine.getHeaderTags());
        
        switch(type) {
        case SNAPSHOT:
        case DELTA:
            header.setOriginRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
            header.setDestinationRandomizedTag(stateEngine.getNextStateRandomizedTag());
            break;
        case REVERSE_DELTA:
            header.setOriginRandomizedTag(stateEngine.getNextStateRandomizedTag());
            header.setDestinationRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
            break;
        case RADIAL_DELTA:
            header.setOriginRandomizedTag(stateEngine.getHubStateRandomizedTag());
            header.setDestinationRandomizedTag(stateEngine.getNextStateRandomizedTag());
            break;
        }
        
        header.setSchemas(schemasToInclude);
        headerWriter.writeHeader(header, os);
    }
    
    private static enum BlobHeaderType {
        SNAPSHOT,
        DELTA,
        REVERSE_DELTA,
        RADIAL_DELTA;
    }
}
