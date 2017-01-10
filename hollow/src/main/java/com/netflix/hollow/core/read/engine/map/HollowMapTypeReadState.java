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
package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.index.key.HollowPrimaryKeyValueDeriver;
import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowMapSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.iterator.EmptyMapOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIteratorImpl;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for MAP type records. 
 */
public class HollowMapTypeReadState extends HollowTypeReadState implements HollowMapTypeDataAccess {

    private HollowMapTypeDataElements currentData;
    private volatile HollowMapTypeDataElements currentDataVolatile;

    private final HollowMapSampler sampler;
    
    private HollowPrimaryKeyValueDeriver keyDeriver;

    public HollowMapTypeReadState(HollowReadStateEngine stateEngine, HollowMapSchema schema) {
        super(stateEngine, schema);
        this.sampler = new HollowMapSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
    }

    @Override
    public void readSnapshot(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowMapTypeDataElements currentData = new HollowMapTypeDataElements(memoryRecycler);
        currentData.readSnapshot(dis);
        setCurrentData(currentData);
        SnapshotPopulatedOrdinalsReader.readOrdinals(dis, stateListeners);
    }

    @Override
    public void applyDelta(DataInputStream dis, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowMapTypeDataElements deltaData = new HollowMapTypeDataElements(memoryRecycler);
        HollowMapTypeDataElements nextData = new HollowMapTypeDataElements(memoryRecycler);
        deltaData.readDelta(dis);
        nextData.applyDelta(currentData, deltaData);
        HollowMapTypeDataElements oldData = currentData;
        setCurrentData(nextData);
        notifyListenerAboutDeltaChanges(deltaData.encodedRemovals, deltaData.encodedAdditions, 0, 1);
        deltaData.destroy();
        oldData.destroy();
    }

    public static void discardSnapshot(DataInputStream dis) throws IOException {
        discardType(dis, false);
    }

    public static void discardDelta(DataInputStream dis) throws IOException {
        discardType(dis, true);
    }

    public static void discardType(DataInputStream dis, boolean delta) throws IOException {
        HollowMapTypeDataElements.discardFromStream(dis, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(dis);
    }

    @Override
    public int maxOrdinal() {
        return currentData.maxOrdinal;
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();

        HollowMapTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentData;
            size = (int)currentData.mapPointerAndSizeArray.getElementValue((long)(ordinal * currentData.bitsPerFixedLengthMapPortion) + currentData.bitsPerMapPointer, currentData.bitsPerMapSizeValue);
        } while(readWasUnsafe(currentData));

        return size;
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        return get(ordinal, keyOrdinal, keyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        sampler.recordGet();

        HollowMapTypeDataElements currentData;
        int valueOrdinal;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentData;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            hashCode = HashCodes.hashInt(hashCode);
            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(bucketKeyOrdinal == keyOrdinal) {
                    valueOrdinal = getBucketValueByAbsoluteIndex(currentData, bucket);
                    continue threadsafe;
                }
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

            valueOrdinal = -1;
        } while(readWasUnsafe(currentData));

        return valueOrdinal;
    }
    
    @Override
    public int findKey(int ordinal, Object... hashKey) {
        sampler.recordGet();
        
        if(keyDeriver == null)
            return -1;
        
        FieldType fieldTypes[] = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return -1;

        int hashCode = SetMapKeyHasher.hash(hashKey, fieldTypes);

        HollowMapTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentData;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;
                
                if(keyDeriver.keyMatches(bucketKeyOrdinal, hashKey)) {
                    return bucketKeyOrdinal;
                }
                    
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return -1;
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        return (int)findEntry(ordinal, hashKey);
    }
    
    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        sampler.recordGet();
        
        if(keyDeriver == null)
            return -1L;
        
        FieldType fieldTypes[] = keyDeriver.getFieldTypes();
        
        if(hashKey.length != fieldTypes.length)
            return -1L;

        int hashCode = SetMapKeyHasher.hash(hashKey, fieldTypes);

        HollowMapTypeDataElements currentData;

        threadsafe:
        do {
            long startBucket;
            long endBucket;
            do {
                currentData = this.currentData;

                startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
                endBucket = currentData.mapPointerAndSizeArray.getElementValue((long)ordinal * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
            } while(readWasUnsafe(currentData));

            long bucket = startBucket + (hashCode & (endBucket - startBucket - 1));
            int bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);

            while(bucketKeyOrdinal != currentData.emptyBucketKeyValue) {
                if(readWasUnsafe(currentData))
                    continue threadsafe;
                
                if(keyDeriver.keyMatches(bucketKeyOrdinal, hashKey)) {
                    long valueOrdinal = getBucketValueByAbsoluteIndex(currentData, bucket);
                    if(readWasUnsafe(currentData))
                        continue threadsafe;
                    
                    return (long)bucketKeyOrdinal << 32 | valueOrdinal;
                }
                    
                bucket++;
                if(bucket == endBucket)
                    bucket = startBucket;
                bucketKeyOrdinal = getBucketKeyByAbsoluteIndex(currentData, bucket);
            }

        } while(readWasUnsafe(currentData));

        return -1L;
    }

    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        sampler.recordGet();

        if(size(ordinal) == 0)
            return EmptyMapOrdinalIterator.INSTANCE;
        return new PotentialMatchHollowMapEntryOrdinalIteratorImpl(ordinal, this, hashCode);
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();

        if(size(ordinal) == 0)
            return EmptyMapOrdinalIterator.INSTANCE;
        return new HollowMapEntryOrdinalIteratorImpl(ordinal, this);
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        HollowMapTypeDataElements currentData;
        do {
            long absoluteBucketIndex;
            do {
                currentData = this.currentData;
                absoluteBucketIndex = getAbsoluteBucketStart(currentData, ordinal) + bucketIndex;
            } while(readWasUnsafe(currentData));
            long key = getBucketKeyByAbsoluteIndex(currentData, absoluteBucketIndex);
            if(key == currentData.emptyBucketKeyValue)
                return -1;

            return key << 32 | getBucketValueByAbsoluteIndex(currentData, absoluteBucketIndex);
        } while(readWasUnsafe(currentData));
    }

    private long getAbsoluteBucketStart(HollowMapTypeDataElements currentData, int ordinal) {
        long startBucket = ordinal == 0 ? 0 : currentData.mapPointerAndSizeArray.getElementValue((long)(ordinal - 1) * currentData.bitsPerFixedLengthMapPortion, currentData.bitsPerMapPointer);
        return startBucket;
    }

    private int getBucketKeyByAbsoluteIndex(HollowMapTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.entryArray.getElementValue(absoluteBucketIndex * currentData.bitsPerMapEntry, currentData.bitsPerKeyElement);
    }

    private int getBucketValueByAbsoluteIndex(HollowMapTypeDataElements currentData, long absoluteBucketIndex) {
        return (int)currentData.entryArray.getElementValue((absoluteBucketIndex * currentData.bitsPerMapEntry) + currentData.bitsPerKeyElement, currentData.bitsPerValueElement);
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema)schema;
    }

    @Override
    public HollowSampler getSampler() {
        return sampler;
    }

    @Override
    public void setSamplingDirector(HollowSamplingDirector director) {
        sampler.setSamplingDirector(director);
    }
    
    @Override
    public void setFieldSpecificSamplingDirector(HollowFilterConfig fieldSpec, HollowSamplingDirector director) {
        sampler.setFieldSpecificSamplingDirector(fieldSpec, director);
    }

    @Override
    public void ignoreUpdateThreadForSampling(Thread t) {
        sampler.setUpdateThread(t);
    }

    @Override
    protected void invalidate() {
        stateListeners = EMPTY_LISTENERS;
        setCurrentData(null);
    }

    HollowMapTypeDataElements currentDataElements() {
        return currentData;
    }

    private boolean readWasUnsafe(HollowMapTypeDataElements data) {
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowMapTypeDataElements data) {
        this.currentData = data;
        this.currentDataVolatile = data;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowMapTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            int numBuckets = HashCodes.hashTableSize(size(ordinal));
            long offset = getAbsoluteBucketStart(currentData, ordinal);

            checksum.applyInt(ordinal);
            for(int i=0;i<numBuckets;i++) {
                int bucketKey = getBucketKeyByAbsoluteIndex(currentData, offset + i);
                if(bucketKey != currentData.emptyBucketKeyValue) {
                    checksum.applyInt(i);
                    checksum.applyInt(bucketKey);
                    checksum.applyInt(getBucketValueByAbsoluteIndex(currentData, offset + i));
                }
            }

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
		long requiredBitsForMapPointers = (long)currentData.bitsPerFixedLengthMapPortion * (currentData.maxOrdinal + 1);
		long requiredBitsForMapBuckets = (long)currentData.bitsPerMapEntry * currentData.totalNumberOfBuckets;
		long requiredBits = requiredBitsForMapPointers + requiredBitsForMapBuckets;
		return requiredBits / 8;
	}
	
    @Override
    public long getApproximateHoleCostInBytes() {
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        
        return ((long)(populatedOrdinals.length() - populatedOrdinals.cardinality()) * (long)currentData.bitsPerFixedLengthMapPortion) / 8; 
    }
    
    public HollowPrimaryKeyValueDeriver getKeyDeriver() {
        return keyDeriver;
    }
    
    public void buildKeyDeriver() {
        if(getSchema().getHashKey() != null)
            this.keyDeriver = new HollowPrimaryKeyValueDeriver(getSchema().getHashKey(), getStateEngine());
    }

}
