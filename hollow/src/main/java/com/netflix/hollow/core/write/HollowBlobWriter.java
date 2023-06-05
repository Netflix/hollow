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

import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig;
import com.netflix.hollow.api.producer.ProducerOptionalBlobPartConfig.ConfiguredOutputStream;
import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.HollowBlobOptionalPartHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        writeSnapshot(os, null);
    }

    public void writeHeader(OutputStream os, ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams) throws IOException {
        stateEngine.prepareForWrite();

        DataOutputStream dos = new DataOutputStream(os);
        HollowBlobHeaderWrapper hollowBlobHeaderWrapper = buildHeader(partStreams, stateEngine.getSchemas(), false);
        writeHeaders(dos, partStreams, false, hollowBlobHeaderWrapper);

        os.flush();
        if(partStreams != null)
            partStreams.flush();
    }

    public void writeSnapshot(OutputStream os, ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams) throws IOException {
        Map<String, DataOutputStream> partStreamsByType = Collections.emptyMap();
        if(partStreams != null)
            partStreamsByType = partStreams.getStreamsByType();

        stateEngine.prepareForWrite();

        DataOutputStream dos = new DataOutputStream(os);
        HollowBlobHeaderWrapper hollowBlobHeaderWrapper = buildHeader(partStreams, stateEngine.getSchemas(), false);
        writeHeaders(dos, partStreams, false, hollowBlobHeaderWrapper);

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
            DataOutputStream partStream = partStreamsByType.get(typeState.getSchema().getName());
            if(partStream == null)
                partStream = dos;

            HollowSchema schema = typeState.getSchema();
            schema.writeTo(partStream);

            writeNumShards(partStream, typeState.getNumShards());

            typeState.writeSnapshot(partStream);
        }

        os.flush();
        if(partStreams != null)
            partStreams.flush();
    }


    /**
     * Serialize the changes necessary to transition a consumer from the previous state
     * to the current state as a delta blob.
     *
     * @param os the output stream to write the delta blob
     * @throws IOException if the delta blob could not be written
     * @throws IllegalStateException if the current state is restored from the previous state
     * and current state contains unrestored state for one or more types.  This indicates those
     * types have not been declared to the producer as part it's initialized data model.
     * @see com.netflix.hollow.api.producer.HollowProducer#initializeDataModel(Class[])
     */
    public void writeDelta(OutputStream os) throws IOException {
        writeDelta(os, null);
    }

    public void writeDelta(OutputStream os, ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams) throws IOException {
        Map<String, DataOutputStream> partStreamsByType = Collections.emptyMap();
        if(partStreams != null)
            partStreamsByType = partStreams.getStreamsByType();

        stateEngine.prepareForWrite();
        
        if(stateEngine.isRestored())
            stateEngine.ensureAllNecessaryStatesRestored();

        List<HollowSchema> changedTypes = changedTypes();
        
        DataOutputStream dos = new DataOutputStream(os);
        HollowBlobHeaderWrapper hollowBlobHeaderWrapper = buildHeader(partStreams, changedTypes, false);
        writeHeaders(dos, partStreams, false, hollowBlobHeaderWrapper);

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "write-delta");

        for(final HollowTypeWriteState typeState : stateEngine.getOrderedTypeStates()) {
            executor.execute(new Runnable() {
                public void run() {
                    // if(typeState.hasSchemaChangedSinceLastCycle())
                    //     typeState.calculateDeltaWithSchemaChange();
                    // else
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
                DataOutputStream partStream = partStreamsByType.get(typeState.getSchema().getName());
                if(partStream == null)
                    partStream = dos;

                HollowSchema schema = typeState.getSchema();
                schema.writeTo(partStream);

                writeNumShards(partStream, typeState.getNumShards());

                typeState.writeDelta(partStream);
            }
        }

        os.flush();
        if(partStreams != null)
            partStreams.flush();
    }

    /**
     * Serialize the changes necessary to transition a consumer from the current state to the
     * previous state as a delta blob.
     *
     * @param os the output stream to write the reverse delta blob
     * @throws IOException if the reverse delta blob could not be written
     * @throws IllegalStateException if the current state is restored from the previous state
     * and current state contains unrestored state for one or more types.  This indicates those
     * types have not been declared to the producer as part it's initialized data model.
     * @see com.netflix.hollow.api.producer.HollowProducer#initializeDataModel(Class[])
     */
    public void writeReverseDelta(OutputStream os) throws IOException {
        writeReverseDelta(os, null);
    }

    public void writeReverseDelta(OutputStream os, ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams) throws IOException {
        Map<String, DataOutputStream> partStreamsByType = Collections.emptyMap();
        if(partStreams != null)
            partStreamsByType = partStreams.getStreamsByType();

        stateEngine.prepareForWrite();
        
        if(stateEngine.isRestored())
            stateEngine.ensureAllNecessaryStatesRestored();
        
        List<HollowSchema> changedTypes = changedTypes();

        DataOutputStream dos = new DataOutputStream(os);
        HollowBlobHeaderWrapper hollowBlobHeaderWrapper = buildHeader(partStreams, changedTypes, true);
        writeHeaders(dos, partStreams, true, hollowBlobHeaderWrapper);

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
                DataOutputStream partStream = partStreamsByType.get(typeState.getSchema().getName());
                if(partStream == null)
                    partStream = dos;

                HollowSchema schema = typeState.getSchema();
                schema.writeTo(partStream);

                writeNumShards(partStream, typeState.getNumShards());

                typeState.writeReverseDelta(partStream);
            }
        }

        os.flush();
        if(partStreams != null)
            partStreams.flush();
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
    
    private void writeNumShards(DataOutputStream dos, int numShards) throws IOException {
        VarInt.writeVInt(dos, 1 + VarInt.sizeOfVInt(numShards)); /// pre 2.1.0 forwards compatibility:
                                                                 /// skip new forwards-compatibility and num shards
        
        VarInt.writeVInt(dos, 0); /// 2.1.0 forwards-compatibility, can write number of bytes for older readers to skip here.
        
        VarInt.writeVInt(dos, numShards);
    }

    public HollowBlobHeaderWrapper buildHeader(ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams, List<HollowSchema> schemasToInclude, boolean isReverseDelta) {
        HollowBlobHeader header = new HollowBlobHeader();
        /// bucket schemas by part
        List<HollowSchema> mainSchemas = schemasToInclude;
        Map<String, List<HollowSchema>> schemasByPartName = Collections.emptyMap();
        if(partStreams != null) {
            mainSchemas = new ArrayList<>();

            Map<String, String> partNameByType = partStreams.getPartNameByType();
            schemasByPartName = new HashMap<>();

            for(HollowSchema schema : schemasToInclude) {
                String partName = partNameByType.get(schema.getName());
                if(partName == null) {
                    mainSchemas.add(schema);
                } else {
                    List<HollowSchema> partSchemas = schemasByPartName.computeIfAbsent(partName, n -> new ArrayList<>());
                    partSchemas.add(schema);
                }
            }
        }
        /// write main header
        if(isReverseDelta) {
            header.setHeaderTags(stateEngine.getPreviousHeaderTags());  // header tags corresponding to destination state
            header.setOriginRandomizedTag(stateEngine.getNextStateRandomizedTag());
            header.setDestinationRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
        } else {
            header.setHeaderTags(stateEngine.getHeaderTags());
            header.setOriginRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
            header.setDestinationRandomizedTag(stateEngine.getNextStateRandomizedTag());
        }
        header.setSchemas(mainSchemas);
        return new HollowBlobHeaderWrapper(header, schemasByPartName);
    }

    private void writeHeaders(DataOutputStream os, ProducerOptionalBlobPartConfig.OptionalBlobPartOutputStreams partStreams, boolean isReverseDelta, HollowBlobHeaderWrapper hollowBlobHeaderWrapper) throws IOException {
        headerWriter.writeHeader(hollowBlobHeaderWrapper.header, os);
        VarInt.writeVInt(os, hollowBlobHeaderWrapper.header.getSchemas().size());

        if(partStreams != null) {
            /// write part headers
            for(Map.Entry<String, ConfiguredOutputStream> entry : partStreams.getPartStreams().entrySet()) {
                String partName = entry.getKey();
                HollowBlobOptionalPartHeader partHeader = new HollowBlobOptionalPartHeader(partName);
                if(isReverseDelta) {
                    partHeader.setOriginRandomizedTag(stateEngine.getNextStateRandomizedTag());
                    partHeader.setDestinationRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
                } else {
                    partHeader.setOriginRandomizedTag(stateEngine.getPreviousStateRandomizedTag());
                    partHeader.setDestinationRandomizedTag(stateEngine.getNextStateRandomizedTag());
                }

                List<HollowSchema> partSchemas = hollowBlobHeaderWrapper.schemasByPartName.get(partName);
                if(partSchemas == null)
                    partSchemas = Collections.emptyList();

                partHeader.setSchemas(partSchemas);

                headerWriter.writePartHeader(partHeader, entry.getValue().getStream());

                VarInt.writeVInt(entry.getValue().getStream(), partSchemas.size());
            }
        }
    }

    private static class HollowBlobHeaderWrapper {
        private final HollowBlobHeader header;
        private final Map<String, List<HollowSchema>> schemasByPartName;

        HollowBlobHeaderWrapper(HollowBlobHeader header, Map<String, List<HollowSchema>> schemasByPartName) {
            this.header = header;
            this.schemasByPartName = schemasByPartName;
        }
    }
}
