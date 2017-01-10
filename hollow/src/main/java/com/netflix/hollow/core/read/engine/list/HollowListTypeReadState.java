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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.tools.checksum.HollowChecksum;

import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.api.sampling.HollowSamplingDirector;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.SnapshotPopulatedOrdinalsReader;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * A {@link HollowTypeReadState} for LIST type records.
 */
public class HollowListTypeReadState extends HollowCollectionTypeReadState implements HollowListTypeDataAccess {

    private HollowListTypeDataElements currentData;
    private volatile HollowListTypeDataElements currentDataVolatile;

    private final HollowListSampler sampler;

    public HollowListTypeReadState(HollowReadStateEngine stateEngine, HollowListSchema schema) {
        super(stateEngine, schema);
        this.sampler = new HollowListSampler(schema.getName(), DisabledSamplingDirector.INSTANCE);
    }

    @Override
    public void readSnapshot(DataInputStream dis, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowListTypeDataElements currentData = new HollowListTypeDataElements(memoryRecycler);
        currentData.readSnapshot(dis);
        setCurrentData(currentData);
        SnapshotPopulatedOrdinalsReader.readOrdinals(dis, stateListeners);
    }

    @Override
    public void applyDelta(DataInputStream dis, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException {
        HollowListTypeDataElements deltaData = new HollowListTypeDataElements(memoryRecycler);
        HollowListTypeDataElements nextData = new HollowListTypeDataElements(memoryRecycler);
        deltaData.readDelta(dis);
        nextData.applyDelta(currentData, deltaData);
        HollowListTypeDataElements oldData = currentData;
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
        HollowListTypeDataElements.discardFromStream(dis, delta);
        if(!delta)
            SnapshotPopulatedOrdinalsReader.discardOrdinals(dis);
    }

    @Override
    public HollowListSchema getSchema() {
        return (HollowListSchema) schema;
    }

    @Override
    public int maxOrdinal() {
        return currentData.maxOrdinal;
    }

    @Override
    public int getElementOrdinal(int ordinal, int listIndex) {
        sampler.recordGet();

        HollowListTypeDataElements currentData;
        int elementOrdinal;

        do {
            long startAndEndElement;

            do {
                currentData = this.currentData;

                long fixedLengthOffset = currentData.bitsPerListPointer * ordinal;

                startAndEndElement = ordinal == 0 ?
                        currentData.listPointerArray.getElementValue(fixedLengthOffset, currentData.bitsPerListPointer) << currentData.bitsPerListPointer :
                            currentData.listPointerArray.getElementValue(fixedLengthOffset - currentData.bitsPerListPointer, currentData.bitsPerListPointer * 2);

            } while(readWasUnsafe(currentData));

            long endElement = startAndEndElement >> currentData.bitsPerListPointer;
            long startElement = startAndEndElement &  ((1 << currentData.bitsPerListPointer) - 1);

            long elementIndex = startElement + listIndex;

            if(elementIndex >= endElement)
                throw new ArrayIndexOutOfBoundsException("Array index out of bounds: " + listIndex + ", list size: " + (endElement - startElement));

            elementOrdinal = (int)currentData.elementArray.getElementValue(elementIndex * currentData.bitsPerElement, currentData.bitsPerElement);
        } while(readWasUnsafe(currentData));

        return elementOrdinal;
    }

    @Override
    public int size(int ordinal) {
        sampler.recordSize();

        HollowListTypeDataElements currentData;
        int size;

        do {
            currentData = this.currentData;

            long fixedLengthOffset = currentData.bitsPerListPointer * ordinal;

            long startAndEndElement = ordinal == 0 ?
                    currentData.listPointerArray.getElementValue(fixedLengthOffset, currentData.bitsPerListPointer) << currentData.bitsPerListPointer :
                        currentData.listPointerArray.getElementValue(fixedLengthOffset - currentData.bitsPerListPointer, currentData.bitsPerListPointer * 2);

            long endElement = startAndEndElement >> currentData.bitsPerListPointer;
            long startElement = startAndEndElement &  ((1 << currentData.bitsPerListPointer) - 1);

            size = (int)(endElement - startElement);
        } while(readWasUnsafe(currentData));

        return size;
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        sampler.recordIterator();

        return new HollowListOrdinalIterator(ordinal, this);
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

    HollowListTypeDataElements currentDataElements() {
        return currentData;
    }

    private boolean readWasUnsafe(HollowListTypeDataElements data) {
        return data != currentDataVolatile;
    }

    void setCurrentData(HollowListTypeDataElements data) {
        this.currentData = data;
        this.currentDataVolatile = data;
    }

    @Override
    protected void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema) {
        if(!getSchema().equals(withSchema))
            throw new IllegalArgumentException("HollowListTypeReadState cannot calculate checksum with unequal schemas: " + getSchema().getName());
        
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            int size = size(ordinal);

            checksum.applyInt(ordinal);
            for(int i=0;i<size;i++)
                checksum.applyInt(getElementOrdinal(ordinal, i));

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }
    }

	@Override
	public long getApproximateHeapFootprintInBytes() {
		long requiredListPointerBits = ((long)currentData.bitsPerListPointer * (currentData.maxOrdinal + 1));
		long requiredElementBits = (currentData.totalNumberOfElements * currentData.bitsPerElement);
		long requiredBits = requiredListPointerBits + requiredElementBits;
		return requiredBits / 8;
	}
	
	@Override
    public long getApproximateHoleCostInBytes() {
        BitSet populatedOrdinals = getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        
        return ((long)(populatedOrdinals.length() - populatedOrdinals.cardinality()) * (long)currentData.bitsPerListPointer) / 8; 
    }

}
