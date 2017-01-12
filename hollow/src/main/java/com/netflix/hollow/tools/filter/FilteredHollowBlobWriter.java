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
package com.netflix.hollow.tools.filter;

import static com.netflix.hollow.core.util.IOUtils.copySegmentedLongArray;
import static com.netflix.hollow.core.util.IOUtils.copyVInt;
import static com.netflix.hollow.core.util.IOUtils.copyVLong;
import static com.netflix.hollow.tools.filter.FilteredHollowBlobWriterStreamAndFilter.streamsOnly;

import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;

import com.netflix.hollow.core.util.IOUtils;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.HollowFilterConfig.ObjectFilterConfig;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The FilteredHollowBlobWriter can be used to pre-filter data from serialized blobs before disseminating to
 * specific clients.
 * <p>
 * Note that filter functionality is more commonly applied at the consumer at load-time.  Pre-filtering at the producer
 * prior to disseminating to clients is unlikely to be important for performance reasons, but may be desirable e.g. for
 * security reasons.
 *
 */
///TODO: This assumes all configured types and fields EXIST.
public class FilteredHollowBlobWriter {

    private final HollowFilterConfig configs[];
    private final HollowBlobHeaderReader headerReader;
    private final ArraySegmentRecycler memoryRecycler;

    /**
     * A FilteredHollowBlobWriter should be configured with one or more configs.  
     * 
     * Specifying multiple configs will allow for the writing of multiple filtered blobs in parallel.
     * 
     * @param configs
     */
    public FilteredHollowBlobWriter(HollowFilterConfig... configs) {
        this.configs = configs;
        this.headerReader = new HollowBlobHeaderReader();
        this.memoryRecycler = new RecyclingRecycler();
    }

    /**
     * Filter a snapshot, provided via the InputStream, to each of the OutputStreams.
     * 
     * The number of provided OutputStreams should be equal to the number of provided HollowFilterConfigs at instantiation.
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public void filterSnapshot(InputStream in, OutputStream... out) throws IOException {
        filter(false, in, out);
    }

    /**
     * Filter a delta (or reversedelta), provided via the InputStream, to each of the OutputStreams.
     * 
     * The number of provided OutputStreams should be equal to the number of provided HollowFilterConfigs at instantiation.
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public void filterDelta(InputStream in, OutputStream... out) throws IOException {
        filter(true, in, out);
    }

    public void filter(boolean delta, InputStream in, OutputStream... out) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        FilteredHollowBlobWriterStreamAndFilter allStreamAndFilters[] = FilteredHollowBlobWriterStreamAndFilter.combine(out, configs);

        headerReader.copyHeader(dis, streamsOnly(allStreamAndFilters));

        int numStates = VarInt.readVInt(in);
        for(int i=0;i<allStreamAndFilters.length;i++) {
            HollowFilterConfig streamFilterConfig = allStreamAndFilters[i].getConfig();
            int numTypesAfterFilter = streamFilterConfig.isExcludeFilter() ? numStates - streamFilterConfig.numSpecifiedTypes() : streamFilterConfig.numSpecifiedTypes();
            VarInt.writeVInt(allStreamAndFilters[i].getStream(), numTypesAfterFilter);
        }

        for(int i=0;i<numStates;i++) {
            HollowSchema schema = HollowSchema.readFrom(in);

            int numShards = readNumShards(in);

            FilteredHollowBlobWriterStreamAndFilter[] streamsWithType = FilteredHollowBlobWriterStreamAndFilter.withType(schema.getName(), allStreamAndFilters);

            if(schema instanceof HollowObjectSchema) {
                if(streamsWithType.length == 0)
                    HollowObjectTypeReadState.discardType(dis, (HollowObjectSchema)schema, numShards, delta);
                else
                    copyFilteredObjectState(delta, dis, streamsWithType, (HollowObjectSchema)schema, numShards);
            } else {
                for(int j=0;j<streamsWithType.length;j++) {
                    schema.writeTo(streamsWithType[j].getStream());
                    VarInt.writeVInt(streamsWithType[j].getStream(), 1 + VarInt.sizeOfVInt(numShards));
                    VarInt.writeVInt(streamsWithType[j].getStream(), 0); /// forwards compatibility
                    VarInt.writeVInt(streamsWithType[j].getStream(), numShards);
                }

                if (schema instanceof HollowListSchema) {
                    if(streamsWithType.length == 0)
                        HollowListTypeReadState.discardType(dis, numShards, delta);
                    else
                        copyListState(delta, dis, streamsOnly(streamsWithType), numShards);
                } else if(schema instanceof HollowSetSchema) {
                    if(streamsWithType.length == 0)
                        HollowSetTypeReadState.discardType(dis, numShards, delta);
                    else
                        copySetState(delta, dis, streamsOnly(streamsWithType), numShards);
                } else if(schema instanceof HollowMapSchema) {
                    if(streamsWithType.length == 0)
                        HollowMapTypeReadState.discardType(dis, numShards, delta);
                    else
                        copyMapState(delta, dis, streamsOnly(streamsWithType), numShards);
                }
            }
        }
    }

    private int readNumShards(InputStream is) throws IOException {
        int backwardsCompatibilityBytes = VarInt.readVInt(is);
        
        if(backwardsCompatibilityBytes == 0)
            return 1;  /// produced by a version of hollow prior to 2.1.0, always only 1 shard.
        
        skipForwardsCompatibilityBytes(is);
        
        return VarInt.readVInt(is);
    }
    
    private void skipForwardsCompatibilityBytes(InputStream is) throws IOException {
        int bytesToSkip = VarInt.readVInt(is);
        while(bytesToSkip > 0) {
            int skippedBytes = (int)is.skip(bytesToSkip);
            if(skippedBytes < 0)
                throw new EOFException();
            bytesToSkip -= skippedBytes;
        }
    }

    @SuppressWarnings("unchecked")
    private void copyFilteredObjectState(boolean delta, DataInputStream is, FilteredHollowBlobWriterStreamAndFilter[] streamAndFilters, HollowObjectSchema schema, int numShards) throws IOException {
        DataOutputStream[] os = streamsOnly(streamAndFilters);
        HollowObjectSchema[] filteredObjectSchemas = new HollowObjectSchema[os.length];

        for(int i=0;i<streamAndFilters.length;i++) {
            HollowObjectSchema filteredObjectSchema = getFilteredObjectSchema(schema, streamAndFilters[i].getConfig());
            filteredObjectSchemas[i] = filteredObjectSchema;
            filteredObjectSchema.writeTo(streamAndFilters[i].getStream());
            
            VarInt.writeVInt(streamAndFilters[i].getStream(), 1 + VarInt.sizeOfVInt(numShards));
            VarInt.writeVInt(streamAndFilters[i].getStream(), 0); /// forwards compatibility
            VarInt.writeVInt(streamAndFilters[i].getStream(), numShards);
        }

        if(numShards > 1)
            copyVInt(is, os);
        
        for(int shard=0;shard<numShards;shard++) {
            int maxShardOrdinal = copyVInt(is, os);
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
            }
    
            /// SETUP ///
            int bitsPerField[] = new int[schema.numFields()];
            for(int i=0;i<schema.numFields();i++)
                bitsPerField[i] = VarInt.readVInt(is);
    
            FixedLengthElementArray fixedLengthArraysPerStream[] = new FixedLengthElementArray[os.length];
            long bitsRequiredPerStream[] = new long[os.length];
            List<FixedLengthArrayWriter> fixedLengthArraysPerField[] = (List<FixedLengthArrayWriter>[])new List[schema.numFields()];
            for(int i=0;i<fixedLengthArraysPerField.length;i++)
                fixedLengthArraysPerField[i] = new ArrayList<FixedLengthArrayWriter>();
    
            for(int i=0;i<streamAndFilters.length;i++) {
                long bitsPerRecord = writeBitsPerField(schema, bitsPerField, filteredObjectSchemas[i], streamAndFilters[i].getStream());
    
                bitsRequiredPerStream[i] = bitsPerRecord * (maxShardOrdinal + 1);
                fixedLengthArraysPerStream[i] = new FixedLengthElementArray(memoryRecycler,  bitsRequiredPerStream[i]);
                FixedLengthArrayWriter filteredArrayWriter = new FixedLengthArrayWriter(fixedLengthArraysPerStream[i]);
    
                for(int j=0;j<schema.numFields();j++) {
                    if(filteredObjectSchemas[i].getPosition(schema.getFieldName(j)) != -1) {
                        fixedLengthArraysPerField[j].add(filteredArrayWriter);
                    }
                }
            }
            /// END SETUP ///
    
            /// read the unfiltered long array into memory
            FixedLengthElementArray unfilteredFixedLengthFields = FixedLengthElementArray.deserializeFrom(is, memoryRecycler);
    
            /// populate the filtered arrays (each field just gets written to all FixedLengthArrayWriters assigned to its field index)
            long bitsPerRecord = 0;
            for(int fieldBits : bitsPerField)
                bitsPerRecord += fieldBits;
    
            long stopBit = bitsPerRecord * (maxShardOrdinal + 1);
            long bitCursor = 0;
            int fieldCursor = 0;
    
            while(bitCursor < stopBit) {
                if(!fixedLengthArraysPerField[fieldCursor].isEmpty()) {
                    long fieldValue = bitsPerField[fieldCursor] > 56 ?
                            unfilteredFixedLengthFields.getLargeElementValue(bitCursor, bitsPerField[fieldCursor])
                            : unfilteredFixedLengthFields.getElementValue(bitCursor, bitsPerField[fieldCursor]);
    
                            for(int i=0;i<fixedLengthArraysPerField[fieldCursor].size();i++)
                                fixedLengthArraysPerField[fieldCursor].get(i).writeField(fieldValue, bitsPerField[fieldCursor]);
                }
    
                bitCursor += bitsPerField[fieldCursor];
                if(++fieldCursor == schema.numFields())
                    fieldCursor = 0;
    
            }
    
            /// write the filtered arrays
            for(int i=0;i<os.length;i++) {
                long numLongsRequired = bitsRequiredPerStream[i] == 0 ? 0 : ((bitsRequiredPerStream[i] - 1) / 64) + 1;
                fixedLengthArraysPerStream[i].writeTo(os[i], numLongsRequired);
            }
    
            /// copy the var length arrays for populated fields
            for(int i=0;i<schema.numFields();i++) {
                List<DataOutputStream> streamsWithFieldList = new ArrayList<DataOutputStream>();
                for(int j=0;j<streamAndFilters.length;j++) {
                    ObjectFilterConfig objectTypeConfig = streamAndFilters[j].getConfig().getObjectTypeConfig(schema.getName());
                    if(objectTypeConfig.includesField(schema.getFieldName(i)))
                        streamsWithFieldList.add(streamAndFilters[j].getStream());
                }
    
                DataOutputStream streamsWithField[] = new DataOutputStream[streamsWithFieldList.size()];
                streamsWithField = streamsWithFieldList.toArray(streamsWithField);
    
                long numBytesInVarLengthData = IOUtils.copyVLong(is, streamsWithField);
                IOUtils.copyBytes(is, streamsWithField, numBytesInVarLengthData);
            }
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(is, os);
    }

    private long writeBitsPerField(HollowObjectSchema unfilteredSchema, int bitsPerField[], HollowObjectSchema filteredSchema, DataOutputStream os) throws IOException {
        long bitsPerRecord = 0;

        for(int i=0;i<unfilteredSchema.numFields();i++) {
            if(filteredSchema.getPosition(unfilteredSchema.getFieldName(i)) != -1) {
                VarInt.writeVInt(os, bitsPerField[i]);
                bitsPerRecord += bitsPerField[i];
            }
        }

        return bitsPerRecord;
    }

    private HollowObjectSchema getFilteredObjectSchema(HollowObjectSchema schema, HollowFilterConfig filterConfig) {
        ObjectFilterConfig typeConfig = filterConfig.getObjectTypeConfig(schema.getName());

        int numIncludedFields = 0;

        for(int i=0;i<schema.numFields();i++) {
            if(typeConfig.includesField(schema.getFieldName(i))) {
                numIncludedFields++;
            }
        }

        HollowObjectSchema filteredSchema = new HollowObjectSchema(schema.getName(), numIncludedFields, schema.getPrimaryKey());

        for(int i=0;i<schema.numFields();i++) {
            if(typeConfig.includesField(schema.getFieldName(i))) {
                filteredSchema.addField(schema.getFieldName(i), schema.getFieldType(i), schema.getReferencedType(i));
            }
        }

        return filteredSchema;
    }

    private void copyListState(boolean delta, DataInputStream is, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(is, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(is, os);  /// maxOrdinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
            }
    
            copyVInt(is, os);  /// bitsPerListPointer
            copyVInt(is, os);  /// bitsPerElement
            copyVLong(is, os); /// totalNumberOfElements
    
            copySegmentedLongArray(is, os);
            copySegmentedLongArray(is, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(is, os);
    }

    private void copySetState(boolean delta, DataInputStream is, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(is, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(is, os);  /// max ordinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
            }
    
            copyVInt(is, os);  /// bitsPerSetPointer
            copyVInt(is, os);  /// bitsPerSetSizeValue
            copyVInt(is, os);  /// bitsPerElement
            copyVLong(is, os); /// totalNumberOfBuckets
    
            copySegmentedLongArray(is, os);
            copySegmentedLongArray(is, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(is, os);
    }

    private void copyMapState(boolean delta, DataInputStream is, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(is, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(is, os);  /// max ordinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(is, os);
            }
    
            copyVInt(is, os);  /// bitsPerMapPointer
            copyVInt(is, os);  /// bitsPerMapSizeValue
            copyVInt(is, os);  /// bitsPerKeyElement
            copyVInt(is, os);  /// bitsPerValueElement
            copyVLong(is, os); /// totalNumberOfBuckets
    
            copySegmentedLongArray(is, os);
            copySegmentedLongArray(is, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(is, os);
    }

    private void copySnapshotPopulatedOrdinals(DataInputStream is, DataOutputStream[] os) throws IOException {
        int numLongs = is.readInt();
        for(int i=0;i<os.length;i++)
            os[i].writeInt(numLongs);

        IOUtils.copyBytes(is, os, numLongs * 8);
    }

}
