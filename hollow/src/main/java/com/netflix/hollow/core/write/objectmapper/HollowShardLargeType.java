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
package com.netflix.hollow.core.write.objectmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates how many shards are used to encode hollow records, in effect
 * partitioning records into {@code P} parts corresponding to the number of
 * shards.
 * <p>
 * This annotation may be declared on a POJO or the field of a POJO that
 * is of type {@code List}, {@code Set}, or {@code Map}.  It should be used
 * when determining the number of shards automatically, given a target position
 * size, is insufficient for a particular hollow record type.
 * <p>
 * If {@code N} records are encoded for a particular hollow record type,
 * where each record has an unique ordinal {@code O},
 * and where {@code 0 <= O < N}, and the records are encoded in {@code P}
 * parts, then a record's assigned shard is the result of the expression
 * {@code O & (P - 1)}.
 * <p>
 * If this annotation is absent then the number of shards is dynamically
 * calculated given a target shard size in bytes
 * (see
 * {@link com.netflix.hollow.api.producer.HollowProducer.Builder#withTargetMaxTypeShardSize(long)
 *  HollowProducer.Builder.withTargetMaxTypeShardSize}
 * and
 * {@link com.netflix.hollow.core.write.HollowWriteStateEngine#setTargetMaxTypeShardSize(long)
 *  HollowWriteStateEngine.setTargetMaxTypeShardSize}
 * )
 * and the projected size in bytes of the hollow records for a particular type.
 * For example, if the target shard size is set to a value in bytes of {@code 25MB} and the
 * projected size of the hollow records for a type is {@code 50MB} then the number of shards
 * will be {@code 2}.
 * @see <a href="https://hollow.how/advanced-topics/#type-sharding">Type-sharding documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface HollowShardLargeType {

    /**
     * Returns the number of shards to partition a hollow record.
     * <p>
     * The number of shards must be a power of 2.
     *
     * @return the number of shards to partition a hollow record
     */
    int numShards();
}
