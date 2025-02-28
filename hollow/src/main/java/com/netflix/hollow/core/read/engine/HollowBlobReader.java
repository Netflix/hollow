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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.HollowBlobOptionalPartHeader;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.OptionalBlobPartInput;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.TypeFilter;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A HollowBlobReader is used to populate and update data in a {@link HollowReadStateEngine}, via the consumption
 * of snapshot and delta blobs. Caller can choose between on-heap or shared-memory mode; defaults to (and for
 * backwards compatibility) on-heap mode.
 */
public class HollowBlobReader {

    private final Logger log = Logger.getLogger(HollowBlobReader.class.getName());
    private final HollowReadStateEngine stateEngine;
    private final MemoryMode memoryMode;
    private final HollowBlobHeaderReader headerReader;

    public HollowBlobReader(HollowReadStateEngine stateEngine) {
        this(stateEngine, new HollowBlobHeaderReader());
    }

    public HollowBlobReader(HollowReadStateEngine stateEngine, HollowBlobHeaderReader headerReader) {
        this(stateEngine, headerReader, MemoryMode.ON_HEAP);
    }

    public HollowBlobReader(HollowReadStateEngine stateEngine, MemoryMode memoryMode) {
        this(stateEngine, new HollowBlobHeaderReader(), memoryMode);
    }

    public HollowBlobReader(HollowReadStateEngine stateEngine, HollowBlobHeaderReader headerReader, MemoryMode memoryMode) {
        this.stateEngine = stateEngine;
        this.headerReader = headerReader;
        this.memoryMode = memoryMode;
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided input stream.
     *
     * @param is the input stream to read the snapshot from
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(InputStream is) throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(is);
        readSnapshot(hbi);
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided HollowBlobInput.
     *
     * @param in the Hollow blob input to read the snapshot from
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(HollowBlobInput in) throws IOException {
        readSnapshot(in, new HollowFilterConfig(true));
    }

    /**
     * Initialize the file engine using a snapshot from the provided RandomAccessFile.
     * <p>
     * Apply the provided {@link HollowFilterConfig} to the state.
     *
     * @param is the input stream to read the snapshot from
     * @param filter the filtering configuration to filter the snapshot
     * @throws IOException if the snapshot could not be read
     *
     * @deprecated use {@link #readSnapshot(InputStream, TypeFilter)}
     */
    @Deprecated
    public void readSnapshot(InputStream is, HollowFilterConfig filter) throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(is);
        readSnapshot(hbi, (TypeFilter) filter);
    }

    /**
     * Initialize the file engine using a snapshot from the provided input stream.
     * <p>
     * Apply the provided {@link TypeFilter} to the state.
     *
     * @param is the input stream to read the snapshot from
     * @param filter the filtering configuration to filter the snapshot
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(InputStream is, TypeFilter filter) throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(is);
        readSnapshot(hbi, filter);
    }

    /**
     * Initialize the file engine using a snapshot from the provided Hollow Blob Input.
     * <p>
     * Apply the provided {@link TypeFilter} to the state.
     *
     * @param in the Hollow blob input to read the snapshot from
     * @param filter the filtering configuration to filter the snapshot
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(HollowBlobInput in, TypeFilter filter) throws IOException {
        readSnapshot(in, null, filter);
    }

    public void readSnapshot(HollowBlobInput in, OptionalBlobPartInput optionalParts) throws IOException {
        readSnapshot(in, optionalParts, new HollowFilterConfig(true));
    }

    public void readSnapshot(HollowBlobInput in, OptionalBlobPartInput optionalParts, TypeFilter filter) throws IOException {
        validateMemoryMode(in.getMemoryMode());
        Map<String, HollowBlobInput> optionalPartInputs = null;
        if(optionalParts != null)
            optionalPartInputs = optionalParts.getInputsByPartName(in.getMemoryMode());

        HollowBlobHeader header = readHeader(in, false);
        List<HollowBlobOptionalPartHeader> partHeaders = readPartHeaders(header, optionalPartInputs, in.getMemoryMode());
        List<HollowSchema> allSchemas = combineSchemas(header, partHeaders);

        filter = filter.resolve(allSchemas);

        notifyBeginUpdate();

        long startTime = System.currentTimeMillis();

        int numStates = VarInt.readVInt(in);

        Collection<String> typeNames = new TreeSet<>();
        for(int i=0;i<numStates;i++) {
            String typeName = readTypeStateSnapshot(in, filter);
            typeNames.add(typeName);
        }

        if(optionalPartInputs != null) {
            for(Map.Entry<String, HollowBlobInput> optionalPartEntry : optionalPartInputs.entrySet()) {
                numStates = VarInt.readVInt(optionalPartEntry.getValue());

                for(int i=0;i<numStates;i++) {
                    String typeName = readTypeStateSnapshot(optionalPartEntry.getValue(), filter);
                    typeNames.add(typeName);
                }
            }
        }

        stateEngine.wireTypeStatesToSchemas();

        long endTime = System.currentTimeMillis();

        log.info("SNAPSHOT COMPLETED IN " + (endTime - startTime) + "ms");
        log.info("TYPES: " + typeNames);

        notifyEndUpdate();

        stateEngine.afterInitialization();
    }

    /**
     * Update the state engine using a delta (or reverse delta) blob from the provided input stream.
     * <p>
     * If a {@link HollowFilterConfig} was applied at the time the {@link HollowReadStateEngine} was initialized
     * with a snapshot, it will continue to be in effect after the state is updated.
     *
     * @param in the input stream to read the delta from
     * @throws IOException if the delta could not be applied
     */
    public void applyDelta(InputStream in) throws IOException {
        HollowBlobInput hbi = HollowBlobInput.serial(in);
        applyDelta(hbi);
    }

    /**
     * Update the state engine using a delta (or reverse delta) blob from the provided HollowBlobInput.
     * <p>
     * If a {@link HollowFilterConfig} was applied at the time the {@link HollowReadStateEngine} was initialized
     * with a snapshot, it will continue to be in effect after the state is updated.
     *
     * @param in the Hollow blob input to read the delta from
     * @throws IOException if the delta could not be applied
     */
    public void applyDelta(HollowBlobInput in) throws IOException {
        applyDelta(in, null);
    }

    public void applyDelta(HollowBlobInput in, OptionalBlobPartInput optionalParts) throws IOException {
        validateMemoryMode(in.getMemoryMode());
        Map<String, HollowBlobInput> optionalPartInputs = null;
        if(optionalParts != null)
            optionalPartInputs = optionalParts.getInputsByPartName(in.getMemoryMode());

        HollowBlobHeader header = readHeader(in, true);
        List<HollowBlobOptionalPartHeader> partHeaders = readPartHeaders(header, optionalPartInputs, in.getMemoryMode());
        notifyBeginUpdate();

        long startTime = System.currentTimeMillis();

        int numStates = VarInt.readVInt(in);

        Collection<String> typeNames = new TreeSet<String>();
        for(int i=0;i<numStates;i++) {
            String typeName = readTypeStateDelta(in);
            typeNames.add(typeName);
            stateEngine.getMemoryRecycler().swap();
        }

        if(optionalPartInputs != null) {
            for(Map.Entry<String, HollowBlobInput> optionalPartEntry : optionalPartInputs.entrySet()) {
                numStates = VarInt.readVInt(optionalPartEntry.getValue());

                for(int i=0;i<numStates;i++) {
                    String typeName = readTypeStateDelta(optionalPartEntry.getValue());
                    typeNames.add(typeName);
                    stateEngine.getMemoryRecycler().swap();
                }
            }
        }

        long endTime = System.currentTimeMillis();

        log.info("DELTA COMPLETED IN " + (endTime - startTime) + "ms");
        log.info("TYPES: " + typeNames);

        notifyEndUpdate();
    }

    private HollowBlobHeader readHeader(HollowBlobInput in, boolean isDelta) throws IOException {
        HollowBlobHeader header = headerReader.readHeader(in);

        if(isDelta && header.getOriginRandomizedTag() != stateEngine.getCurrentRandomizedTag())
            throw new IOException("Attempting to apply a delta to a state from which it was not originated!");

        stateEngine.setCurrentRandomizedTag(header.getDestinationRandomizedTag());
        stateEngine.setOriginRandomizedTag(header.getOriginRandomizedTag());
        stateEngine.setHeaderTags(header.getHeaderTags());
        return header;
    }

    private List<HollowBlobOptionalPartHeader> readPartHeaders(HollowBlobHeader header, Map<String, HollowBlobInput> inputsByPartName, MemoryMode mode) throws IOException {
        if(inputsByPartName == null)
            return Collections.emptyList();

        List<HollowBlobOptionalPartHeader> list = new ArrayList<>(inputsByPartName.size());
        for(Map.Entry<String, HollowBlobInput> entry : inputsByPartName.entrySet()) {
            HollowBlobOptionalPartHeader partHeader = headerReader.readPartHeader(entry.getValue());
            if(!partHeader.getPartName().equals(entry.getKey()))
                throw new IllegalArgumentException("Optional blob part expected name " + entry.getKey() + " but was " + partHeader.getPartName());
            if(partHeader.getOriginRandomizedTag() != header.getOriginRandomizedTag()
                    || partHeader.getDestinationRandomizedTag() != header.getDestinationRandomizedTag())
                throw new IllegalArgumentException("Optional blob part " + entry.getKey() + " does not appear to be matched with the main input");

            list.add(partHeader);
        }

        return list;
    }

    private List<HollowSchema> combineSchemas(HollowBlobHeader header, List<HollowBlobOptionalPartHeader> partHeaders) throws IOException {
        if(partHeaders.isEmpty())
            return header.getSchemas();

        List<HollowSchema> schemas = new ArrayList<>(header.getSchemas());

        for(HollowBlobOptionalPartHeader partHeader : partHeaders) {
            schemas.addAll(partHeader.getSchemas());
        }

        return schemas;
    }

    private void notifyBeginUpdate() {
        for(HollowTypeReadState typeFile: stateEngine.getTypeStates()) {
            for(HollowTypeStateListener listener : typeFile.getListeners()) {
                listener.beginUpdate();
            }
        }
    }

    private void notifyEndUpdate() {
        for(HollowTypeReadState typeFile : stateEngine.getTypeStates()) {
            for(HollowTypeStateListener listener : typeFile.getListeners()) {
                listener.endUpdate();
            }
        }
    }

    private String readTypeStateSnapshot(HollowBlobInput in, TypeFilter filter) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(in);
        int numShards = readNumShards(in);
        String typeName = schema.getName();


        if(schema instanceof HollowObjectSchema) {
            if(!filter.includes(typeName)) {
                HollowObjectTypeReadState.discardSnapshot(in, (HollowObjectSchema)schema, numShards);

            } else {
                HollowObjectSchema unfilteredSchema = (HollowObjectSchema)schema;
                HollowObjectSchema filteredSchema = unfilteredSchema.filterSchema(filter);
                populateTypeStateSnapshotWithNumShards(in, new HollowObjectTypeReadState(stateEngine, memoryMode, filteredSchema, unfilteredSchema), numShards);
            }
        } else if (schema instanceof HollowListSchema) {
            if(!filter.includes(typeName)) {
                HollowListTypeReadState.discardSnapshot(in, numShards);
            } else {
                populateTypeStateSnapshotWithNumShards(in, new HollowListTypeReadState(stateEngine, memoryMode, (HollowListSchema)schema), numShards);
            }
        } else if(schema instanceof HollowSetSchema) {
            if(!filter.includes(typeName)) {
                HollowSetTypeReadState.discardSnapshot(in, numShards);
            } else {
                populateTypeStateSnapshotWithNumShards(in, new HollowSetTypeReadState(stateEngine, memoryMode, (HollowSetSchema)schema), numShards);
            }
        } else if(schema instanceof HollowMapSchema) {
            if(!filter.includes(typeName)) {
                HollowMapTypeReadState.discardSnapshot(in, numShards);
            } else {
                populateTypeStateSnapshotWithNumShards(in, new HollowMapTypeReadState(stateEngine, memoryMode, (HollowMapSchema)schema), numShards);
            }
        }

        return typeName;
    }

    private void populateTypeStateSnapshotWithNumShards(HollowBlobInput in, HollowTypeReadState typeState, int numShards) throws IOException {
        if (numShards<=0 || ((numShards&(numShards-1))!=0)) {
            throw new IllegalArgumentException("Number of shards must be a power of 2!");
        }

        stateEngine.addTypeState(typeState);
        typeState.readSnapshot(in, stateEngine.getMemoryRecycler(), numShards);
    }

    private String readTypeStateDelta(HollowBlobInput in) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(in);

        int numShards = readNumShards(in);
        HollowTypeReadState typeState = stateEngine.getTypeState(schema.getName());
        if(typeState != null) {
            if (shouldReshard(typeState.numShards(), numShards)) {
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(typeState);
                reshardingStrategy.reshard(typeState, typeState.numShards(), numShards);
            }
            typeState.applyDelta(in, schema, stateEngine.getMemoryRecycler(), numShards);
        } else {
            discardDelta(in, schema, numShards);
        }

        return schema.getName();
    }

    private boolean shouldReshard(int currNumShards, int deltaNumShards) {
        return currNumShards != 0 && deltaNumShards != 0 && currNumShards != deltaNumShards;
    }

    private int readNumShards(HollowBlobInput in) throws IOException {
        int backwardsCompatibilityBytes = VarInt.readVInt(in);

        if(backwardsCompatibilityBytes == 0)
            return 1;  /// produced by a version of hollow prior to 2.1.0, always only 1 shard.

        skipForwardsCompatibilityBytes(in);

        return VarInt.readVInt(in);
    }
        
    private void skipForwardsCompatibilityBytes(HollowBlobInput in) throws IOException {
        int bytesToSkip = VarInt.readVInt(in);
        while(bytesToSkip > 0) {
            int skippedBytes = (int) in.skipBytes(bytesToSkip);
            if(skippedBytes < 0)
                throw new EOFException();
            bytesToSkip -= skippedBytes;
        }
    }


    private void discardDelta(HollowBlobInput in, HollowSchema schema, int numShards) throws IOException {
        if(schema instanceof HollowObjectSchema)
            HollowObjectTypeReadState.discardDelta(in, (HollowObjectSchema)schema, numShards);
        else if(schema instanceof HollowListSchema)
            HollowListTypeReadState.discardDelta(in, numShards);
        else if(schema instanceof HollowSetSchema)
            HollowSetTypeReadState.discardDelta(in, numShards);
        else if(schema instanceof HollowMapSchema)
            HollowMapTypeReadState.discardDelta(in, numShards);
    }

    private void validateMemoryMode(MemoryMode inputMode) {
        if (!memoryMode.equals(inputMode)) {
            throw new IllegalStateException(String.format("HollowBlobReader is configured for memory mode %s but " +
                    "HollowBlobInput of mode %s was provided", memoryMode, inputMode));
        }
    }
}
