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

/**
 * Warning: This is a BETA API and is subject to breaking changes.
 *
 * Allows a producer to publish to multiple cloud/regions in the same cycle
 */
public abstract class AbstractHollowMultiPublisher implements HollowProducer.Publisher {
    protected final List<HollowProducer.Publisher> publishers;

    protected AbstractHollowMultiPublisher(List<HollowProducer.Publisher> publishers) {
        this.publishers = publishers;
    }

    public abstract void publish(HollowProducer.Blob blob);
}
