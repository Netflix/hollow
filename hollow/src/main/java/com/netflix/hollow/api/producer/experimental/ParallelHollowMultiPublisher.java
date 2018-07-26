/*
 *
 *  Copyright 2018 Netflix, Inc.
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
package com.netflix.hollow.api.producer.experimental;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.util.SimultaneousExecutor;

import java.util.List;
import java.util.logging.Logger;

/**
 * Warning: This is a BETA API and is subject to breaking changes.
 *
 * Allows a producer to publish to multiple cloud/regions at the same time
 */
public class ParallelHollowMultiPublisher extends AbstractHollowMultiPublisher {

    private static final Logger LOG = Logger.getLogger(ParallelHollowMultiPublisher.class.getName());

    private final double threadsPerCpu;

    public ParallelHollowMultiPublisher(List<HollowProducer.Publisher> publishers, double threadsPerCpu) {
        super(publishers);
        this.threadsPerCpu = threadsPerCpu;
    }

    @Override
    public void publish(final HollowProducer.Blob blob) {
        SimultaneousExecutor executor = new SimultaneousExecutor(threadsPerCpu);
        for(final HollowProducer.Publisher publisher : publishers) {
            executor.execute(new Runnable() {
                public void run() {
                    publisher.publish(blob);
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Throwable t) {
            LOG.warning("Could not publish blob" + t);
            throw new RuntimeException(t);
        }
    }
}
