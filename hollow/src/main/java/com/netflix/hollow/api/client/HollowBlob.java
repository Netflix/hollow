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

import java.io.IOException;
import java.io.InputStream;

/**
 * A HollowBlob, which is either a snapshot or a delta, defines three things:
 * 
 * <dl>
 *      <dt>The "from" version</dt>
 *      <dd>The unique identifier of the state to which a delta transition should be applied.  If
 *          this is a snapshot, then this value is Long.MIN_VALUE</dd>
 *          
 *      <dt>The "to" version</dt>
 *      <dd>The unique identifier of the state at which a dataset will arrive after this blob is applied.</dd>
 *      
 *      <dt>The actual blob data</dt>
 *      <dd>Implementations will define how to retrieve the actual blob data for this specific blob from a data store as an InputStream.</dd>
 * </dl>
 * 
 * @deprecated Extend the {@link HollowConsumer.Blob} for use with the {@link HollowConsumer.BlobRetriever} instead.
 * 
 */
@Deprecated
public abstract class HollowBlob extends HollowConsumer.Blob {

    /**
     * Instantiate a snapshot to a specified data state version.
     */
    public HollowBlob(long toVersion) {
        super(toVersion);
    }

    /**
     * Instantiate a delta from one data state version to another. 
     */
    public HollowBlob(long fromVersion, long toVersion) {
        super(fromVersion, toVersion);
    }

    /**
     * Implementations will define how to retrieve the actual blob data for this specific transition from a data store.
     * 
     * It is expected that the returned InputStream will not be interrupted.  For this reason, it is a good idea to
     * retrieve the entire blob (e.g. to disk) from a remote datastore prior to returning this stream.
     *     
     * @return
     * @throws IOException
     */
    public abstract InputStream getInputStream() throws IOException;

    public boolean isSnapshot() {
        return super.isSnapshot();
    }

    public boolean isReverseDelta() {
        return super.isReverseDelta();
    }

    public long getFromVersion() {
        return super.getFromVersion();
    }

    public long getToVersion() {
        return super.getToVersion();
    }
}
