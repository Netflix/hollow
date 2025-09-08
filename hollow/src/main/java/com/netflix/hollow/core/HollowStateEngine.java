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
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.StateEngineUtil;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

import java.util.ArrayList;
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
     * then the schema has changed from that of the prior version.
     */
    String HEADER_TAG_SCHEMA_CHANGE = "hollow.schema.changedFromPriorVersion";

    /**
     * A header tag indicating that num shards for a type has changed since the prior version. Its value encodes
     * the type(s) that were re-sharded along with the before and after num shards in the fwd delta direction.
     * For e.g. Movie:(2,4) Actor:(8,4)
     */
    String HEADER_TAG_TYPE_RESHARDING_INVOKED = "hollow.type.resharding.invoked";

    /**
     * A header tag containing the hash of serialized hollow schema.
     */
    String HEADER_TAG_SCHEMA_HASH = "hollow.schema.hash";

    /**
     * A header tag indicating the timestamp in milliseconds of when the producer cycle started
     * for this state engine.
     */
    String HEADER_TAG_METRIC_CYCLE_START = "hollow.metric.cycle.start";

    /**
     * A header tag indicating the timestamp in milliseconds to mark the start of announcement of a version.
     * */
    String HEADER_TAG_METRIC_ANNOUNCEMENT = "hollow.metric.announcement";

    /**
     * A header tag indicating which version is this blob produce to.
     */
    String HEADER_TAG_PRODUCER_TO_VERSION = "hollow.blob.to.version";

    /**
     * A header tag indicating monotonically increasing version in the same delta chain
     */
    String HEADER_TAG_DELTA_CHAIN_VERSION_COUNTER = "hollow.delta.chain.version.counter";

    @Override
    List<HollowSchema> getSchemas();

    @Override
    HollowSchema getSchema(String typeName);

    @Override
    HollowSchema getNonNullSchema(String typeName) throws SchemaNotFoundException;

    Map<String, String> getHeaderTags();

    String getHeaderTag(String name);

    default PrimaryKey getPrimaryKey(String typeName) {
        PrimaryKey pk=null;
        HollowSchema schema = getSchema(typeName);
        if (schema!=null && schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
            pk = ((HollowObjectSchema) schema).getPrimaryKey();
        }

        return pk;
    }

    default List<PrimaryKey> getPrimaryKeys() {
        List<HollowSchema> schemas = getSchemas();

        List<PrimaryKey> primaryKeys = new ArrayList<>();
        for (HollowSchema schema : schemas) {
            if (schema.getSchemaType() == HollowSchema.SchemaType.OBJECT) {
                PrimaryKey pk = ((HollowObjectSchema) schema).getPrimaryKey();
                if (pk != null)
                    primaryKeys.add(pk);
            }
        }

        return StateEngineUtil.sortPrimaryKeys(primaryKeys, this);
    }
}
