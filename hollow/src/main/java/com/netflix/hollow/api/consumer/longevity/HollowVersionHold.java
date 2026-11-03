/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.consumer.longevity;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;

/**
 * A version of a held-mode consumer's data that stays readable, exactly, while the consumer's state engine moves on.
 * <p>
 * A hold is not tracked by the stale-reference detector and does not depend on garbage collection: it keeps its version
 * until {@link #close()}. Obtain one with {@link HollowConsumer#holdVersion(long)}.
 */
public interface HollowVersionHold extends AutoCloseable {

    /**
     * @return the held version
     */
    long getVersion();

    /**
     * Returns an API of the given class that reads exactly this version. It is created once per class and never uses
     * cached types, whose caches follow the state engine.
     *
     * @param apiClass {@code HollowAPI} or a generated API class
     * @param <T> the type of the API
     * @return the API
     */
    <T extends HollowAPI> T getAPI(Class<T> apiClass);

    /**
     * @return a data access that reads exactly this version, by ordinal
     */
    HollowDataAccess getDataAccess();

    /**
     * Releases the version. The release takes effect at the next delta, without tracking readers: everything read
     * through the hold, including objects created after the close, stays correct until the second delta after the
     * close. Later reads go to the state engine and are logged as errors. Closing more than once has no further effect.
     */
    @Override
    void close();
}
