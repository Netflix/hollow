/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.core;

import com.netflix.hollow.api.error.SchemaNotFoundException;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.List;
import java.util.Map;

/**
 * 
 * A dataset changes over time.  A core concept in Hollow is that the timeline for a changing dataset can be broken
 * down into discrete "states", each of which is a complete snapshot of the data at a particular point in time.
 * 
 * A state engine holds a complete copy of a hollow dataset, and is generally the root handle to the data.  
 * The state engine can be transitioned between states.
 * 
 * Depending on whether a dataset is being produced or consumed, a HollowStateEngine will be either a {@link HollowWriteStateEngine} 
 * or a {@link HollowReadStateEngine}, respectively. 
 * 
 * @author dkoszewnik
 *
 */
public interface HollowStateEngine extends HollowDataset {

    /**
     * A header tag indicating that the schema has changed from that of the prior version.
     * <p>
     * If the header tag is present in the state engine and the value is "true" (ignoring case)
     * then the schema has changed from that of the the prior version.
     */
    String HEADER_TAG_SCHEMA_CHANGE = "hollow.schema.changedFromPriorVersion";

    @Override
    List<HollowSchema> getSchemas();

    @Override
    HollowSchema getSchema(String typeName);

    @Override
    HollowSchema getNonNullSchema(String typeName) throws SchemaNotFoundException;

    Map<String, String> getHeaderTags();

    String getHeaderTag(String name);
}
