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

import java.util.List;
import java.util.logging.Logger;

/**
 * Warning: This is a BETA API and is subject to breaking changes.
 * <p>
 * Allows a producer to publish to multiple cloud/regions at the same time
 */
public class SingleHollowMultiPublisher extends AbstractHollowMultiPublisher {

    private static final Logger LOG = Logger.getLogger(SingleHollowMultiPublisher.class.getName());

    public SingleHollowMultiPublisher(List<HollowProducer.Publisher> publishers) {
        super(publishers);
    }

    @Override
    public void publish(final HollowProducer.Blob blob) {
        Exception cause = null;
        for (final HollowProducer.Publisher publisher : publishers) {
            try {
                publisher.publish(blob);
            } catch (Exception ex) {
                if (cause == null) {
                    cause = ex;
                }
            }
        }
        if(cause != null) {
            LOG.warning("Could not publish blob" + cause);
            throw new RuntimeException(cause);
        }
    }
}
