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
package com.netflix.hollow.api.producer.listener;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.schema.HollowSchema;
import java.time.Duration;

/**
 * A listener of data model initialization events.
 * <p>
 * A producer will emit a data model initialization after the data model is initialized when
 * {@link HollowProducer#initializeDataModel(Class[]) registering} top-level data model classes
 * or when {@link HollowProducer#initializeDataModel(HollowSchema[]) registering} schemas.
 * <p>
 * A data model initialization instance may be registered when building a {@link HollowProducer producer}
 * (see {@link HollowProducer.Builder#withListener(HollowProducerEventListener)}} or by
 * registering on the producer itself
 * (see {@link HollowProducer#addListener(HollowProducerEventListener)}.
 */
public interface DataModelInitializationListener extends HollowProducerEventListener {
    /**
     * Called after the {@code HollowProducer} has initialized its data model.
     * @param elapsed the elapsed duration
     */
    void onProducerInit(Duration elapsed);
}
