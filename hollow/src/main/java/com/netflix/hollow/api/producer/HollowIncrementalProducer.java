/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Warning: This is a BETA API and is subject to breaking changes.
 * 
 */
public class HollowIncrementalProducer {
    
    private static final Object DELETE_RECORD = new Object();
    private static final long FAILED_VERSION = Long.MIN_VALUE;

    private final HollowProducer producer;
    private final ConcurrentHashMap<RecordPrimaryKey, Object> mutations;
    private final HollowProducer.Populator populator;
    private final ListenerSupport listeners;
    private final HashMap<String, Object> cycleMetadata;

    public HollowIncrementalProducer(HollowProducer producer) {
        this(producer, 1.0d, new ArrayList<IncrementalCycleListener>());
    }

    public HollowIncrementalProducer(HollowProducer producer,
                                     double threadsPerCpu,
                                     List<IncrementalCycleListener> listeners) {
        this.producer = producer;
        this.mutations = new ConcurrentHashMap<RecordPrimaryKey, Object>();
        this.populator = new HollowIncrementalCyclePopulator(mutations, threadsPerCpu);
        this.listeners = new ListenerSupport();
        this.cycleMetadata = new HashMap<String, Object>();

        for(IncrementalCycleListener listener : listeners)
            this.listeners.add(listener);
    }

    public void addCycleMetadata(HashMap<String, Object> metadata) {
        this.cycleMetadata.putAll(metadata);
    }

    public void restore(long versionDesired, BlobRetriever blobRetriever) {
        producer.hardRestore(versionDesired, blobRetriever);
    }
    
    public void addOrModify(Object obj) {
        RecordPrimaryKey pk = extractRecordPrimaryKey(obj);
        mutations.put(pk, obj);
    }
    
    public void delete(Object obj) {
        RecordPrimaryKey pk = extractRecordPrimaryKey(obj);
        delete(pk);
    }

    public void discard(Object obj) {
        RecordPrimaryKey pk = extractRecordPrimaryKey(obj);
        discard(pk);
    }
    
    public void delete(RecordPrimaryKey key) {
        mutations.put(key, DELETE_RECORD);
    }

    public void discard(RecordPrimaryKey key) {
        mutations.remove(key);
    }

    public void clearChanges() {
        this.mutations.clear();
    }

    public boolean hasChanges() { return this.mutations.size() > 0; }

    public void clearCycleMetadata() { this.cycleMetadata.clear(); }

    public boolean hasMetadata() { return !this.cycleMetadata.isEmpty(); }

    public void addListener(IncrementalCycleListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IncrementalCycleListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Runs a Hollow Cycle, if successful, cleans the mutations map.
     * @since 2.9.9
     * @return
     */
    public long runCycle() {
        long recordsRemoved = countRecordsToRemove();
        long recordsAddedOrModified = this.mutations.values().size() - recordsRemoved;
        try {
            long version = producer.runCycle(populator);
            listeners.fireIncrementalCycleComplete(version, recordsAddedOrModified, recordsRemoved, new HashMap<String, Object>(cycleMetadata));
            clearChanges();
            return version;
        } catch (Exception e) {
            listeners.fireIncrementalCycleFail(e, recordsAddedOrModified, recordsRemoved, new HashMap<String, Object>(cycleMetadata));
            return FAILED_VERSION;
        } finally {
            clearCycleMetadata();
        }
    }

    private long countRecordsToRemove() {
        long recordsToRemove = 0L;
        Collection<Object> records = mutations.values();
        for(Object record : records) {
            if(record == DELETE_RECORD) recordsToRemove++;
        }
        return recordsToRemove;
    }

    private RecordPrimaryKey extractRecordPrimaryKey(Object obj) {
        return producer.getObjectMapper().extractPrimaryKey(obj);
    }

    public static HollowIncrementalProducer.Builder withHollowProducer(HollowProducer hollowProducer) {
        Builder builder = new Builder();
        return builder.withHollowProducer(hollowProducer);
    }

    public static class Builder {
        protected HollowProducer hollowProducer;
        protected double threadsPerCpu = 1.0d;
        protected List<IncrementalCycleListener> listeners = new ArrayList<IncrementalCycleListener>();

        public Builder withHollowProducer(HollowProducer hollowProducer) {
            this.hollowProducer = hollowProducer;
            return this;
        }

        public Builder withThreadsPerCpu(double threadsPerCpu) {
            this.threadsPerCpu = threadsPerCpu;
            return this;
        }

        public Builder withListener(IncrementalCycleListener listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder withListeners(IncrementalCycleListener... listeners) {
            for(IncrementalCycleListener listener : listeners)
                this.listeners.add(listener);
            return this;
        }

        protected void checkArguments() {
            if(hollowProducer == null)
                throw new RuntimeException("HollowProducer should be specified.");
        }

        public HollowIncrementalProducer build() {
            checkArguments();
            return new HollowIncrementalProducer(hollowProducer, threadsPerCpu, listeners);
        }
    }
}
