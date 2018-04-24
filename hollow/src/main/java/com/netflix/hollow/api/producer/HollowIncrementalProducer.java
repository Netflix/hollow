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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemAnnouncementWatcher;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Warning: This is a BETA API and is subject to breaking changes.
 * 
 */
public class HollowIncrementalProducer {
    
    private final HollowProducer producer;
    private final ConcurrentHashMap<RecordPrimaryKey, Object> mutations;
    private final HollowProducer.Populator populator;
    private final Class<?>[] dataModel;
    private final HollowConsumer.AnnouncementWatcher announcementWatcher;
    private final HollowConsumer.BlobRetriever blobRetriever;
    
    public HollowIncrementalProducer(HollowProducer producer) {
        this(producer, 1.0d);
    }

    //For backwards compatible. TODO: @Deprecated ??
    public HollowIncrementalProducer(HollowProducer producer, double threadsPerCpu) {
        this(producer, threadsPerCpu, null, null, null);
    }

    protected HollowIncrementalProducer(HollowProducer producer, double threadsPerCpu, HollowConsumer.AnnouncementWatcher announcementWatcher, HollowConsumer.BlobRetriever blobRetriever, Class<?>...classes) {
        this.producer = producer;
        this.mutations = new ConcurrentHashMap<RecordPrimaryKey, Object>();
        this.populator = new HollowIncrementalCyclePopulator(mutations, threadsPerCpu);
        this.dataModel = classes;
        this.announcementWatcher = announcementWatcher;
        this.blobRetriever = blobRetriever;
    }

    /**
     * Initializes the data model and restores from existing state.
     */
    public void restoreFromLastState() {
        producer.initializeDataModel(dataModel);
        long latestAnnouncedVersion = announcementWatcher.getLatestVersion();
        if(latestAnnouncedVersion == HollowFilesystemAnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE || latestAnnouncedVersion < 0) {
            return;
        }

        restore(latestAnnouncedVersion, blobRetriever);
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
        mutations.put(key, HollowIncrementalCyclePopulator.DELETE_RECORD);
    }

    public void discard(RecordPrimaryKey key) {
        mutations.remove(key);
    }

    public void clearChanges() {
        this.mutations.clear();
    }

    public boolean hasChanges() { return this.mutations.size() > 0; }

    /**
     * Runs a Hollow Cycle, if successful, cleans the mutations map.
     * @since 2.9.9
     * @return
     */
    public long runCycle() {
        long version = producer.runCycle(populator);
        clearChanges();
        return version;
    }

    private RecordPrimaryKey extractRecordPrimaryKey(Object obj) {
        return producer.getObjectMapper().extractPrimaryKey(obj);
    }

    public static HollowIncrementalProducer.Builder withProducer(HollowProducer hollowProducer) {
        Builder builder = new Builder();
        return builder.withProducer(hollowProducer);
    }

    public static class Builder<B extends HollowIncrementalProducer.Builder<B>> {
        protected HollowProducer producer;
        protected double threadsPerCpu = 1.0d;
        protected HollowConsumer.AnnouncementWatcher announcementWatcher;
        protected HollowConsumer.BlobRetriever blobRetriever;
        protected Class<?>[] dataModel;

        public B withProducer(HollowProducer producer) {
            this.producer = producer;
            return (B)this;
        }

        public B withThreadsPerCpu(double threadsPerCpu) {
            this.threadsPerCpu = threadsPerCpu;
            return (B)this;
        }

        public B withAnnouncementWatcher(HollowConsumer.AnnouncementWatcher announcementWatcher) {
            this.announcementWatcher = announcementWatcher;
            return (B)this;
        }

        public B withBlobRetriever(HollowConsumer.BlobRetriever blobRetriever) {
            this.blobRetriever = blobRetriever;
            return (B)this;
        }

        public B withDataModel(Class<?>...classes) {
            this.dataModel = classes;
            return (B)this;
        }

        protected void checkArguments() {
            if(producer == null)
                throw new IllegalArgumentException("HollowProducer must be specified.");

        }

        public HollowIncrementalProducer build() {
            checkArguments();
            return new HollowIncrementalProducer(producer, threadsPerCpu, announcementWatcher, blobRetriever, dataModel);
        }
    }

}
