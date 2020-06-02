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
package com.netflix.hollow.tools.filter;

import static com.netflix.hollow.core.util.IOUtils.copySegmentedLongArray;
import static com.netflix.hollow.core.util.IOUtils.copyVInt;
import static com.netflix.hollow.core.util.IOUtils.copyVLong;
import static com.netflix.hollow.tools.filter.FilteredHollowBlobWriterStreamAndFilter.streamsOnly;

import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.memory.encoding.FixedLengthElementArray;
import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.filter.HollowFilterConfig.ObjectFilterConfig;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.IOUtils;
import com.netflix.hollow.core.write.HollowBlobHeaderWriter;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The FilteredHollowBlobWriter can be used to pre-filter data from serialized blobs before disseminating to
 * specific clients.
 * <p>
 * Note that filter functionality is more commonly applied at the consumer at load-time.  Pre-filtering at the producer
 * prior to disseminating to clients is unlikely to be important for performance reasons, but may be desirable e.g. for
 * security reasons.
 *
 */
public class FilteredHollowBlobWriter {

    private final HollowFilterConfig configs[];
    private final HollowBlobHeaderReader headerReader;
    private final HollowBlobHeaderWriter headerWriter;
    private final ArraySegmentRecycler memoryRecycler;
    private final Set<String> expectedTypes;

    /**
     * A FilteredHollowBlobWriter should be configured with one or more configs.  
     * 
     * Specifying multiple configs will allow for the writing of multiple filtered blobs in parallel.
     * 
     * @param configs the filter configurations
     */
    public FilteredHollowBlobWriter(HollowFilterConfig... configs) {
        this.configs = configs;
        this.headerReader = new HollowBlobHeaderReader();
        this.headerWriter = new HollowBlobHeaderWriter();
        this.memoryRecycler = WastefulRecycler.DEFAULT_INSTANCE;
        this.expectedTypes = new HashSet<String>();
        for(HollowFilterConfig config : configs)
            expectedTypes.addAll(config.getSpecifiedTypes());
    }

    /**
     * Filter a snapshot, provided via the InputStream, to each of the OutputStreams.
     * 
     * The number of provided OutputStreams should be equal to the number of provided HollowFilterConfigs at instantiation.
     * 
     * @param in the input stream to the snapshot
     * @param out the output streams to write the filtered snapshot
     * @throws IOException if the snapshot cannot be filtered
     */
    public void filterSnapshot(InputStream in, OutputStream... out) throws IOException {
        filter(false, in, out);
    }

    /**
     * Filter a delta (or reversedelta), provided via the InputStream, to each of the OutputStreams.
     * 
     * The number of provided OutputStreams should be equal to the number of provided HollowFilterConfigs at instantiation.
     *
     * @param in the input stream to the delta
     * @param out the output streams to write the filtered delta
     * @throws IOException if the delta cannot be filtered
     */
    public void filterDelta(InputStream in, OutputStream... out) throws IOException {
        filter(true, in, out);
    }

    public void filter(boolean delta, InputStream is, OutputStream... out) throws IOException {
        HollowBlobInput in = HollowBlobInput.serial(is);

        FilteredHollowBlobWriterStreamAndFilter allStreamAndFilters[] = FilteredHollowBlobWriterStreamAndFilter.combine(out, configs);

        HollowBlobHeader header = headerReader.readHeader(in);
        
        List<HollowSchema> unfilteredSchemaList = header.getSchemas(); 

        for(FilteredHollowBlobWriterStreamAndFilter streamAndFilter : allStreamAndFilters) {
            List<HollowSchema> filteredSchemaList = getFilteredSchemaList(unfilteredSchemaList, streamAndFilter.getConfig());
            header.setSchemas(filteredSchemaList);
            headerWriter.writeHeader(header, streamAndFilter.getStream());
            VarInt.writeVInt(streamAndFilter.getStream(), filteredSchemaList.size());
        }
        
        int numStates = VarInt.readVInt(in);
        
        Set<String> encounteredTypes = new HashSet<String>();

        for(int i=0;i<numStates;i++) {
            HollowSchema schema = HollowSchema.readFrom(in);
            
            encounteredTypes.add(schema.getName());

            int numShards = readNumShards(in);

            FilteredHollowBlobWriterStreamAndFilter[] streamsWithType = FilteredHollowBlobWriterStreamAndFilter.withType(schema.getName(), allStreamAndFilters);

            if(schema instanceof HollowObjectSchema) {
                if(streamsWithType.length == 0)
                    HollowObjectTypeReadState.discardType(in, (HollowObjectSchema)schema, numShards, delta);
                else
                    copyFilteredObjectState(delta, in, streamsWithType, (HollowObjectSchema)schema, numShards);
            } else {
                for(int j=0;j<streamsWithType.length;j++) {
                    schema.writeTo(streamsWithType[j].getStream());
                    VarInt.writeVInt(streamsWithType[j].getStream(), 1 + VarInt.sizeOfVInt(numShards));
                    VarInt.writeVInt(streamsWithType[j].getStream(), 0); /// forwards compatibility
                    VarInt.writeVInt(streamsWithType[j].getStream(), numShards);
                }

                if (schema instanceof HollowListSchema) {
                    if(streamsWithType.length == 0)
                        HollowListTypeReadState.discardType(in, numShards, delta);
                    else
                        copyListState(delta, in, streamsOnly(streamsWithType), numShards);
                } else if(schema instanceof HollowSetSchema) {
                    if(streamsWithType.length == 0)
                        HollowSetTypeReadState.discardType(in, numShards, delta);
                    else
                        copySetState(delta, in, streamsOnly(streamsWithType), numShards);
                } else if(schema instanceof HollowMapSchema) {
                    if(streamsWithType.length == 0)
                        HollowMapTypeReadState.discardType(in, numShards, delta);
                    else
                        copyMapState(delta, in, streamsOnly(streamsWithType), numShards);
                }
            }
        }
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
            int skippedBytes = (int)in.skipBytes(bytesToSkip);
            if(skippedBytes < 0)
                throw new EOFException();
            bytesToSkip -= skippedBytes;
        }
    }

    @SuppressWarnings("unchecked")
    private void copyFilteredObjectState(boolean delta, HollowBlobInput in, FilteredHollowBlobWriterStreamAndFilter[] streamAndFilters, HollowObjectSchema schema, int numShards) throws IOException {
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
            copyVInt(in, os);
        
        for(int shard=0;shard<numShards;shard++) {
            int maxShardOrdinal = copyVInt(in, os);
            int numRecordsToCopy = maxShardOrdinal + 1;
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
                GapEncodedVariableLengthIntegerReader addedOrdinals = GapEncodedVariableLengthIntegerReader.readEncodedDeltaOrdinals(in, memoryRecycler);
                numRecordsToCopy = addedOrdinals.remainingElements();
                for(DataOutputStream stream : os)
                    addedOrdinals.writeTo(stream);
            }
    
            /// SETUP ///
            int bitsPerField[] = new int[schema.numFields()];
            for(int i=0;i<schema.numFields();i++)
                bitsPerField[i] = VarInt.readVInt(in);
    
            FixedLengthElementArray fixedLengthArraysPerStream[] = new FixedLengthElementArray[os.length];
            long bitsRequiredPerStream[] = new long[os.length];
            List<FixedLengthArrayWriter> fixedLengthArraysPerField[] = (List<FixedLengthArrayWriter>[])new List[schema.numFields()];
            for(int i=0;i<fixedLengthArraysPerField.length;i++)
                fixedLengthArraysPerField[i] = new ArrayList<FixedLengthArrayWriter>();
    
            for(int i=0;i<streamAndFilters.length;i++) {
                long bitsPerRecord = writeBitsPerField(schema, bitsPerField, filteredObjectSchemas[i], streamAndFilters[i].getStream());
    
                bitsRequiredPerStream[i] = bitsPerRecord * numRecordsToCopy;
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
            FixedLengthElementArray unfilteredFixedLengthFields = FixedLengthElementArray.newFrom(in, memoryRecycler);
    
            /// populate the filtered arrays (each field just gets written to all FixedLengthArrayWriters assigned to its field index)
            long bitsPerRecord = 0;
            for(int fieldBits : bitsPerField)
                bitsPerRecord += fieldBits;
    
            long stopBit = bitsPerRecord * numRecordsToCopy;
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
    
                long numBytesInVarLengthData = IOUtils.copyVLong(in, streamsWithField);
                IOUtils.copyBytes(in, streamsWithField, numBytesInVarLengthData);
            }
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(in, os);
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
    
    private List<HollowSchema> getFilteredSchemaList(List<HollowSchema> schemaList, HollowFilterConfig filterConfig) {
        List<HollowSchema> filteredList = new ArrayList<HollowSchema>();
        
        for(HollowSchema schema : schemaList) {
            HollowSchema filteredSchema = getFilteredSchema(schema, filterConfig);
            if(filteredSchema != null)
                filteredList.add(filteredSchema);
        }
        
        return filteredList;
    }
    
    private HollowSchema getFilteredSchema(HollowSchema schema, HollowFilterConfig filterConfig) {
        if(filterConfig.doesIncludeType(schema.getName())) {
            if(schema.getSchemaType() == SchemaType.OBJECT)
                return getFilteredObjectSchema((HollowObjectSchema) schema, filterConfig);
            return schema;
        }
        
        return null;
    }

    private HollowObjectSchema getFilteredObjectSchema(HollowObjectSchema schema, HollowFilterConfig filterConfig) {
        ObjectFilterConfig typeConfig = filterConfig.getObjectTypeConfig(schema.getName());

        int numIncludedFields = 0;

        for(int i=0;i<schema.numFields();i++) {
            if(typeConfig.includesField(schema.getFieldName(i)))
                numIncludedFields++;
        }

        if(numIncludedFields == schema.numFields())
            return schema;
        
        HollowObjectSchema filteredSchema = new HollowObjectSchema(schema.getName(), numIncludedFields, schema.getPrimaryKey());

        for(int i=0;i<schema.numFields();i++) {
            if(typeConfig.includesField(schema.getFieldName(i)))
                filteredSchema.addField(schema.getFieldName(i), schema.getFieldType(i), schema.getReferencedType(i));
        }

        return filteredSchema;
    }

    private void copyListState(boolean delta, HollowBlobInput in, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(in, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(in, os);  /// maxOrdinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
            }
    
            copyVInt(in, os);  /// bitsPerListPointer
            copyVInt(in, os);  /// bitsPerElement
            copyVLong(in, os); /// totalNumberOfElements
    
            copySegmentedLongArray(in, os);
            copySegmentedLongArray(in, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(in, os);
    }

    private void copySetState(boolean delta, HollowBlobInput in, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(in, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(in, os);  /// max ordinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
            }
    
            copyVInt(in, os);  /// bitsPerSetPointer
            copyVInt(in, os);  /// bitsPerSetSizeValue
            copyVInt(in, os);  /// bitsPerElement
            copyVLong(in, os); /// totalNumberOfBuckets
    
            copySegmentedLongArray(in, os);
            copySegmentedLongArray(in, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(in, os);
    }

    private void copyMapState(boolean delta, HollowBlobInput in, DataOutputStream[] os, int numShards) throws IOException {
        if(numShards > 1)
            copyVInt(in, os);
        
        for(int shard=0;shard<numShards;shard++) {
            copyVInt(in, os);  /// max ordinal
    
            if(delta) {
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
                GapEncodedVariableLengthIntegerReader.copyEncodedDeltaOrdinals(in, os);
            }
    
            copyVInt(in, os);  /// bitsPerMapPointer
            copyVInt(in, os);  /// bitsPerMapSizeValue
            copyVInt(in, os);  /// bitsPerKeyElement
            copyVInt(in, os);  /// bitsPerValueElement
            copyVLong(in, os); /// totalNumberOfBuckets
    
            copySegmentedLongArray(in, os);
            copySegmentedLongArray(in, os);
        }

        if(!delta)
            copySnapshotPopulatedOrdinals(in, os);
    }

    private void copySnapshotPopulatedOrdinals(HollowBlobInput in, DataOutputStream[] os) throws IOException {
        int numLongs = in.readInt();
        for(int i=0;i<os.length;i++)
            os[i].writeInt(numLongs);

        IOUtils.copyBytes(in, os, numLongs * 8);
    }
    
}
