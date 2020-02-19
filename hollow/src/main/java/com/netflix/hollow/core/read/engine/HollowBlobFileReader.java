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
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A HollowBlobReader is used to populate (and update???) references in a {@link HollowReadFileEngine}, via the consumption
 * of snapshot and delta blobs.
 */
public class HollowBlobFileReader {

    private final Logger log = Logger.getLogger(HollowBlobFileReader.class.getName());
    private final HollowReadFileEngine fileEngine;
    private final HollowBlobHeaderReader headerReader;

    public HollowBlobFileReader(HollowReadFileEngine fileEngine) {
        this(fileEngine, new HollowBlobHeaderReader());
    }

    public HollowBlobFileReader(HollowReadFileEngine fileEngine, HollowBlobHeaderReader headerReader) {
        this.fileEngine = fileEngine;
        this.headerReader = headerReader;
    }

    /**
     * Initialize the state engine using a snapshot blob from the provided InputStream.
     *
     * @param f the RandomAccessFile to read the snapshot from
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(RandomAccessFile f) throws IOException {
        readSnapshot(f, new HollowFilterConfig(true));
    }

    /**
     * Initialize the file engine using a snapshot from the provided RandomAccessFile.
     * <p>
     * Apply the provided {@link HollowFilterConfig} to the state.
     *
     * @param raf the RandomAccessFile to read the snaptshot from
     * @param filter the filtering configuration to filter the snapshot
     * @throws IOException if the snapshot could not be read
     */
    public void readSnapshot(RandomAccessFile raf, HollowFilterConfig filter) throws IOException {
        HollowBlobHeader header = readHeader(f, false);

        notifyBeginUpdate();

        long startTime = System.currentTimeMillis();

        int numStates = VarInt.readVInt(raf);

        Collection<String> typeNames = new TreeSet<String>();
        for(int i=0;i<numStates;i++) {
            String typeName = readTypeFileSnapshot(raf, header, filter);
            typeNames.add(typeName);
        }

        fileEngine.wireTypeStatesToSchemas();

        long endTime = System.currentTimeMillis();

        log.info("SNAPSHOT COMPLETED IN " + (endTime - startTime) + "ms");
        log.info("TYPES: " + typeNames);

        notifyEndUpdate();

        fileEngine.afterInitialization();
    }

    /**
     * Update the state engine using a delta (or reverse delta) blob from the provided InputStream.
     * <p>
     * If a {@link HollowFilterConfig} was applied at the time the {@link HollowReadFileEngine} was initialized
     * with a snapshot, it will continue to be in effect after the state is updated.
     *
     * @param is the input stream to read the delta from
     * @throws IOException if the delta could not be applied
     */
//    public void applyDelta(InputStream is) throws IOException {
//        HollowBlobHeader header = readHeader(is, true);
//        notifyBeginUpdate();
//
//        long startTime = System.currentTimeMillis();
//
//        DataInputStream dis = new DataInputStream(is);
//
//        int numStates = VarInt.readVInt(dis);
//
//        Collection<String> typeNames = new TreeSet<String>();
//        for(int i=0;i<numStates;i++) {
//            String typeName = readTypeStateDelta(dis, header);
//            typeNames.add(typeName);
//            fileEngine.getMemoryRecycler().swap();
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        log.info("DELTA COMPLETED IN " + (endTime - startTime) + "ms");
//        log.info("TYPES: " + typeNames);
//
//        notifyEndUpdate();
//
//    }

    private HollowBlobHeader readHeader(RandomAccessFile f, boolean isDelta) throws IOException {
        HollowBlobHeader header = headerReader.readHeader(f);

        if(isDelta && header.getOriginRandomizedTag() != fileEngine.getCurrentRandomizedTag())
            throw new IOException("Attempting to apply a delta to a state from which it was not originated!");

        fileEngine.setCurrentRandomizedTag(header.getDestinationRandomizedTag());
        fileEngine.setHeaderTags(header.getHeaderTags());
        return header;
    }

    private void notifyBeginUpdate() {
        for(HollowTypeReadFile typeFile: fileEngine.getTypeFiles()) {
            for(HollowTypeStateListener listener : typeFile.getListeners()) {
                listener.beginUpdate();
            }
        }
    }

    private void notifyEndUpdate() {
        for(HollowTypeReadFile typeFile : fileEngine.getTypeFiles()) {
            for(HollowTypeStateListener listener : typeFile.getListeners()) {
                listener.endUpdate();
            }
        }
    }

    private String readTypeFileSnapshot(RandomAccessFile raf, HollowBlobHeader header, HollowFilterConfig filter) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(raf);

        int numShards = readNumShards(raf);

        if(schema instanceof HollowObjectSchema) {
            if(!filter.doesIncludeType(schema.getName())) {
//                HollowObjectTypeReadState.discardSnapshot(raf, (HollowObjectSchema)schema, numShards);
            } else {
                HollowObjectSchema unfilteredSchema = (HollowObjectSchema)schema;
                HollowObjectSchema filteredSchema = unfilteredSchema.filterSchema(filter);
                populateTypeStateSnapshot(raf, new HollowObjectTypeReadState(fileEngine, filteredSchema, unfilteredSchema, numShards));
            }
        } else if (schema instanceof HollowListSchema) {
            if(!filter.doesIncludeType(schema.getName())) {
//                HollowListTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(raf, new HollowListTypeReadState(fileEngine, (HollowListSchema)schema, numShards));
            }
        } else if(schema instanceof HollowSetSchema) {
            if(!filter.doesIncludeType(schema.getName())) {
//                HollowSetTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(raf, new HollowSetTypeReadState(fileEngine, (HollowSetSchema)schema, numShards));
            }
        } else if(schema instanceof HollowMapSchema) {
            if(!filter.doesIncludeType(schema.getName())) {
//                HollowMapTypeReadState.discardSnapshot(is, numShards);
            } else {
                populateTypeStateSnapshot(raf, new HollowMapTypeReadState(fileEngine, (HollowMapSchema)schema, numShards));
            }
        }

        return schema.getName();
    }

    private void populateTypeStateSnapshot(RandomAccessFile raf, HollowTypeReadState typeState) throws IOException {
        fileEngine.addTypeState(typeState);
        typeState.readSnapshot(is, fileEngine.getMemoryRecycler());
    }

    private String readTypeStateDelta(DataInputStream is, HollowBlobHeader header) throws IOException {
        HollowSchema schema = HollowSchema.readFrom(is);

        int numShards = readNumShards(is);

        HollowTypeReadState typeState = fileEngine.getTypeState(schema.getName());
        if(typeState != null) {
            typeState.applyDelta(is, schema, fileEngine.getMemoryRecycler());
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

    private int readNumShards(RandomAccessFile raf) throws IOException {
        int backwardsCompatibilityBytes = VarInt.readVInt(raf);

        if(backwardsCompatibilityBytes == 0)
            return 1;  /// produced by a version of hollow prior to 2.1.0, always only 1 shard.

        skipForwardsCompatibilityBytes(raf);

        return VarInt.readVInt(raf);
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
    private void skipForwardsCompatibilityBytes(RandomAccessFile raf) throws IOException {
        int bytesToSkip = VarInt.readVInt(raf);
        while(bytesToSkip > 0) {
            int skippedBytes = (int)raf.skipBytes(bytesToSkip);
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
