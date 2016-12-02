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
package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.memory.encoding.GapEncodedVariableLengthIntegerReader;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.api.sampling.HollowSampler;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

/**
 * A HollowTypeReadState contains and is the root handle to all of the records of a specific type in
 * a {@link HollowReadStateEngine}.
 */
public abstract class HollowTypeReadState implements HollowTypeDataAccess {

    protected static final HollowTypeStateListener[] EMPTY_LISTENERS = new HollowTypeStateListener[0];

    protected final HollowReadStateEngine stateEngine;
    protected final HollowSchema schema;
    protected HollowTypeStateListener[] stateListeners;

    public HollowTypeReadState(HollowReadStateEngine stateEngine, HollowSchema schema) {
        this.stateEngine = stateEngine;
        this.schema = schema;
        this.stateListeners = EMPTY_LISTENERS;
    }

    /**
     * Add a {@link HollowTypeStateListener} to this type.
     */
    public void addListener(HollowTypeStateListener listener) {
        HollowTypeStateListener newListeners[] = new HollowTypeStateListener[stateListeners.length + 1];
        System.arraycopy(stateListeners, 0, newListeners, 0, stateListeners.length);
        newListeners[stateListeners.length] = listener;
        stateListeners = newListeners;
    }

    /**
     * Remove a specific {@link HollowTypeStateListener} from this type.
     */
    public void removeListener(HollowTypeStateListener listener) {
        if(stateListeners.length == 0)
            return;

        HollowTypeStateListener oldListeners[] = stateListeners;
        HollowTypeStateListener newListeners[] = new HollowTypeStateListener[stateListeners.length - 1];
        int newListenerIdx = 0;
        for(int i=0;i<oldListeners.length;i++) {
            if(newListenerIdx == newListeners.length)
                return;

            if(oldListeners[i] != listener)
                newListeners[newListenerIdx++] = oldListeners[i];
        }

        if(newListenerIdx < newListeners.length)
            stateListeners = Arrays.copyOf(newListeners, newListenerIdx);
        else
            stateListeners = newListeners;
    }

    /**
     * @return all {@link HollowTypeStateListener}s currently associated with this type.
     */
    public HollowTypeStateListener[] getListeners() {
        return stateListeners;
    }

    /**
     * @return a {@link HollowTypeStateListener} of the specified class currently associated with this type, or
     * null if none is currently attached.
     */
    @SuppressWarnings("unchecked")
    public <T extends HollowTypeStateListener> T getListener(Class<T> listenerClazz) {
        HollowTypeStateListener[] stateListeners = this.stateListeners;
        for(int i=0;i<stateListeners.length;i++) {
            HollowTypeStateListener listener = stateListeners[i];
            if(listenerClazz.isAssignableFrom(listener.getClass())) {
                return (T) listener;
            }
        }
        return null;
    }
    
    /**
     * Returns the BitSet containing the currently populated ordinals in this type state.
     * <p>
     * WARNING: Do not modify the returned BitSet.  
     */
    public BitSet getPopulatedOrdinals() {
        return getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }
    
    /**
     * Returns the BitSet containing the populated ordinals in this type state prior to the previous delta transition.
     * <p>
     * WARNING: Do not modify the returned BitSet.
     */
    public BitSet getPreviousOrdinals() {
        return getListener(PopulatedOrdinalListener.class).getPreviousOrdinals();
    }

    /**
     * @return The maximum ordinal currently populated in this type state.
     */
    public abstract int maxOrdinal();

    public abstract void readSnapshot(DataInputStream dis, ArraySegmentRecycler recycler) throws IOException;
    public abstract void applyDelta(DataInputStream dis, HollowSchema schema, ArraySegmentRecycler memoryRecycler) throws IOException;

    public HollowSchema getSchema() {
        return schema;
    }

    @Override
    public HollowDataAccess getDataAccess() {
        return stateEngine;
    }

    /**
     * @return the {@link HollowReadStateEngine} which this type state belongs to.
     */
    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    protected void notifyListenerAboutDeltaChanges(GapEncodedVariableLengthIntegerReader removals, GapEncodedVariableLengthIntegerReader additions) {
        for(HollowTypeStateListener stateListener : stateListeners) {
            removals.reset();
            int removedOrdinal = removals.nextElement();
            while(removedOrdinal < Integer.MAX_VALUE) {
                stateListener.removedOrdinal(removedOrdinal);
                removals.advance();
                removedOrdinal = removals.nextElement();
            }

            additions.reset();
            int addedOrdinal = additions.nextElement();
            while(addedOrdinal < Integer.MAX_VALUE) {
                stateListener.addedOrdinal(addedOrdinal);
                additions.advance();
                addedOrdinal = additions.nextElement();
            }
        }
    }

    public abstract HollowSampler getSampler();

    protected abstract void invalidate();

    public HollowChecksum getChecksum(HollowSchema withSchema) {
        HollowChecksum cksum = new HollowChecksum();
        applyToChecksum(cksum, withSchema);
        return cksum;
    }

    protected abstract void applyToChecksum(HollowChecksum checksum, HollowSchema withSchema);

    @Override
    public HollowTypeReadState getTypeState() {
        return this;
    }
    
    /**
     * @return an approximate accounting of the current heap footprint occupied by this type state.
     */
    public abstract long getApproximateHeapFootprintInBytes();
    
    /**
     * @return an approximate accounting of the current cost of the "ordinal holes" in this type state.
     */
    public abstract long getApproximateHoleCostInBytes();

}
