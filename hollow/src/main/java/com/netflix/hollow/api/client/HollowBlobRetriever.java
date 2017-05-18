/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;

/**
 * An interface which defines the necessary interactions of a {@link HollowClient} with a blob data store. 
 * 
 * Implementations will define how to retrieve blob data from a data store.
 *
 * @deprecated Implement the {@link HollowConsumer.BlobRetriever} for use with the {@link HollowConsumer} instead.
 */
@Deprecated
public interface HollowBlobRetriever {

    /**
     * Returns the snapshot for the state with an identifier equal to or less than the desired version
     */
    public HollowBlob retrieveSnapshotBlob(long desiredVersion);

    /**
     * Returns a delta transition which can be applied to the currentVersion
     */
    public HollowBlob retrieveDeltaBlob(long currentVersion);

    /**
     * Returns a reverse delta transition which can be applied to the currentVersion
     */
    public HollowBlob retrieveReverseDeltaBlob(long currentVersion);

}
