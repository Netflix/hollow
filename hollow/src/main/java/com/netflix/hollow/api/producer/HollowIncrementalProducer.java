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
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;

/**
 * Warning: This is a BETA API and is subject to breaking changes.
 */
public class HollowIncrementalProducer {

    private final HollowProducer hollowProducer;

    public HollowIncrementalProducer(HollowProducer hollowProducer) {
        this.hollowProducer = hollowProducer;
    }

    public HollowProducer getHollowProducer() {
        return hollowProducer;
    }

    public HollowProducer.ReadState restore(long version, HollowConsumer.BlobRetriever blobRetriever) {
        return hollowProducer.hardRestore(version, blobRetriever);
    }

    public long runCycle(final IncrementalPopulator incrementalPopulator) {
        return getHollowProducer().runCycle(new HollowProducer.Populator() {
            @Override
            public void populate(HollowProducer.WriteState newState) throws Exception {
                newState.getStateEngine().addAllObjectsFromPreviousCycle();
                IncrementalWriteState incrementalWriteState = new IncrementalWriteStateImpl(newState);
                incrementalPopulator.populate(incrementalWriteState);
            }
        });
    }

    public interface IncrementalPopulator {
        void populate(IncrementalWriteState newState) throws Exception;
    }

    public interface IncrementalWriteState {
        int addOrModify(Object o);

        int delete(Object o);

        int deleteByPrimaryKey(String typeName, Object... id);

        int deleteByPrimaryKey(RecordPrimaryKey recordPrimaryKey);

        HollowObjectMapper getObjectMapper();

        HollowWriteStateEngine getStateEngine();

        ReadState getPriorState();

        long getVersion();
    }

}