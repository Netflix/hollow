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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.VarInt;
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
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A HollowBlobReader is used to populate and update data in a {@link HollowReadStateEngine}, via the consumption
 * of snapshot and delta blobs. 
 */
public class HollowBlobReader {

    private final Logger log = Logger.getLogger(HollowBlobReader.class.getName());
    private final HollowReadStateEngine stateEngine;
    private final HollowBlobHeaderReader headerReader;

    public HollowBlobReader(HollowReadStateEngine stateEngine) {
        this(stateEngine, new HollowBlobHeaderReader());
    }

    public HollowBlobReader(HollowReadStateEngine stateEngine, HollowBlobHeaderReader headerReader) {
        this.stateEngine = stateEngine;
        this.headerReader = headerReader;
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided InputStream.
     *
     * @param is the input stream to read the snapshot from
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(InputStream is) throws IOException {
        readSnapshot(is, new HollowFilterConfig(true));
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided InputStream.
     * <p>
     * Apply the provided {@link HollowFilterConfig} to the state.
     *
     * @param is the input stream to read the snaptshot
     * @param filter the filtering configuration to filter the snapshot
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(InputStream is, HollowFilterConfig filter) throws IOException {
        /*
         * This method is preserved for binary compat from before TypeFilter was introduced.
         */

        readSnapshot(is, (TypeFilter)filter);
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided InputStream.
     * <p>
     * Apply the provided {@link TypeFilter} to the state.
     *
     * @param is the input stream to read the snaptshot
     * @param filter the type filter to filter the snapshot
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(InputStream is, TypeFilter filter) throws IOException {
        HollowBlobHeader header = readHeader(is, false);
        filter = filter.resolve(header.getSchemas());

        notifyBeginUpdate();

        long startTime = System.currentTimeMillis();

        DataInputStream dis = new DataInputStream(is);

        int numStates = VarInt.readVInt(dis);

        Collection<String> typeNames = new TreeSet<String>();
        for(int i=0;i<numStates;i++) {
            String typeName = readTypeStateSnapshot(dis, header, filter);
            typeNames.add(typeName);
        }

        stateEngine.wireTypeStatesToSchemas();

        long endTime = System.currentTimeMillis();

        log.info("SNAPSHOT COMPLETED IN " + (endTime - startTime) + "ms");
        log.info("TYPES: " + typeNames);

        notifyEndUpdate();

        stateEngine.afterInitialization();
    }

    /**
     * Update the state engine using a delta (or reverse delta) blob from the provided InputStream.
     * <p>
     * If a {@link HollowFilterConfig} was applied at the time the {@link HollowReadStateEngine} was initialized
     * with a snapshot, it will continue to be in effect after the state is updated.
     *
     * @param is the input stream to read the delta from
     * @throws IOException if the delta could not be applied
     */
    public void applyDelta(InputStream is) throws IOException {
        HollowBlobHeader header = readHeader(is, true);
        notifyBeginUpdate();

        long startTime = System.currentTimeMillis();

        DataInputStream dis = new DataInputStream(is);

        int numStates = VarInt.readVInt(dis);

        Collection<String> typeNames = new TreeSet<String>();
        for(int i=0;i<numStates;i++) {
            String typeName = readTypeStateDelta(dis, header);
            typeNames.add(typeName);
            stateEngine.getMemoryRecycler().swap();
        }

        long endTime = System.currentTimeMillis();

        log.info("DELTA COMPLETED IN " + (endTime - startTime) + "ms");
        log.info("TYPES: " + typeNames);

        notifyEndUpdate();

    }

    private HollowBlobHeader readHeader(InputStream is, boolean isDelta) throws IOException {
        HollowBlobHeader header = headerReader.readHeader(is);

        if(isDelta && header.getOriginRandomizedTag() != stateEngine.getCurrentRandomizedTag())
            throw new IOException("Attempting to apply a delta to a state from which it was not originated!");

        stateEngine.setCurrentRandomizedTag(header.getDestinationRandomizedTag());
        stateEngine.setHeaderTags(header.getHeaderTags());
        return header;
    }

    private void notifyBeginUpdate() {
        for(HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            for(HollowTypeStateListener listener : typeState.getListeners()) {
                listener.beginUpdate();
            }
        }
    }

    private void notifyEndUpdate() {
        for(HollowTypeReadState typeState : stateEngine.getTypeStates()) {
            for(HollowTypeStateListener listener : typeState.getListeners()) {
                listener.endUpdate();
            }
        }
    }

    private String readTypeStateSnapshot(DataInputStream is, HollowBlobHeader header, TypeFilter filter) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(is);
        int numShards = readNumShards(is);
        String typeName = schema.getName();

        if(schema instanceof HollowObjectSchema) {
            if(!filter.includes(typeName)) {
                HollowObjectTypeReadState.discardSnapshot(is, (HollowObjectSchema)schema, numShards);
            } else {
                HollowObjectSchema unfilteredSchema = (HollowObjectSchema)schema;
                HollowObjectSchema filteredSchema = unfilteredSchema.filterSchema(filter);
                populateTypeStateSnapshot(is, new HollowObjectTypeReadState(stateEngine, filteredSchema, unfilteredSchema, numShards));
            }
        } else if (schema instanceof HollowListSchema) {
            if(!filter.includes(typeName)) {
                HollowListTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(is, new HollowListTypeReadState(stateEngine, (HollowListSchema)schema, numShards));
            }
        } else if(schema instanceof HollowSetSchema) {
            if(!filter.includes(typeName)) {
                HollowSetTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(is, new HollowSetTypeReadState(stateEngine, (HollowSetSchema)schema, numShards));
            }
        } else if(schema instanceof HollowMapSchema) {
            if(!filter.includes(typeName)) {
                HollowMapTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(is, new HollowMapTypeReadState(stateEngine, (HollowMapSchema)schema, numShards));
            }
        }
        
        return typeName;
    }

    private void populateTypeStateSnapshot(DataInputStream is, HollowTypeReadState typeState) throws IOException {
        stateEngine.addTypeState(typeState);
        typeState.readSnapshot(is, stateEngine.getMemoryRecycler());
    }

    private String readTypeStateDelta(DataInputStream is, HollowBlobHeader header) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(is);

        int numShards = readNumShards(is);

        HollowTypeReadState typeState = stateEngine.getTypeState(schema.getName());
        if(typeState != null) {
            typeState.applyDelta(is, schema, stateEngine.getMemoryRecycler());
        } else {
            discardDelta(is, schema, numShards);
        }
        
        return schema.getName();
    }

    private int readNumShards(DataInputStream is) throws IOException {
        int backwardsCompatibilityBytes = VarInt.readVInt(is);
        
        if(backwardsCompatibilityBytes == 0)
            return 1;  /// produced by a version of hollow prior to 2.1.0, always only 1 shard.
        
        skipForwardsCompatibilityBytes(is);
        
        return VarInt.readVInt(is);
    }
        
    private void skipForwardsCompatibilityBytes(DataInputStream is) throws IOException {
        int bytesToSkip = VarInt.readVInt(is);
        while(bytesToSkip > 0) {
            int skippedBytes = (int)is.skip(bytesToSkip);
            if(skippedBytes < 0)
                throw new EOFException();
            bytesToSkip -= skippedBytes;
        }
    }


    private void discardDelta(DataInputStream dis, HollowSchema schema, int numShards) throws IOException {
        if(schema instanceof HollowObjectSchema)
            HollowObjectTypeReadState.discardDelta(dis, (HollowObjectSchema)schema, numShards);
        else if(schema instanceof HollowListSchema)
            HollowListTypeReadState.discardDelta(dis, numShards);
        else if(schema instanceof HollowSetSchema)
            HollowSetTypeReadState.discardDelta(dis, numShards);
        else if(schema instanceof HollowMapSchema)
            HollowMapTypeReadState.discardDelta(dis, numShards);
    }

}
