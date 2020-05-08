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
package com.netflix.hollow.api.client;

import com.netflix.hollow.core.HollowConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A HollowBlob, which is either a snapshot or a delta, defines three things:
 * 
 * <dl>
 *      <dt>The "from" version</dt>
 *      <dd>The unique identifier of the state to which a delta transition should be applied.  If
 *          this is a snapshot, then this value is HollowConstants.VERSION_NONE.</dd>
 *          
 *      <dt>The "to" version</dt>
 *      <dd>The unique identifier of the state at which a dataset will arrive after this blob is applied.</dd>
 *      
 *      <dt>The actual blob data</dt>
 *      <dd>Implementations will define how to retrieve the actual blob data for this specific blob from a data store as an InputStream.</dd>
 * </dl>
 * 
 * @deprecated Extend the {@link com.netflix.hollow.api.consumer.HollowConsumer.Blob} for use with the
 *             {@link com.netflix.hollow.api.consumer.HollowConsumer.BlobRetriever} instead.
 */
@Deprecated
public abstract class HollowBlob {

    private final long fromVersion;
    private final long toVersion;

    /**
     * Instantiate a snapshot to a specified data state version.
     *
     * @param toVersion the version to end from
     */
    public HollowBlob(long toVersion) {
        this(HollowConstants.VERSION_NONE, toVersion);
    }

    /**
     * Instantiate a delta from one data state version to another.
     *
     * @param fromVersion the version to start from
     * @param toVersion the version to end from
     */
    public HollowBlob(long fromVersion, long toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    /**
     * Implementations will define how to retrieve the actual blob data for this specific transition from a data store.
     * 
     * It is expected that the returned InputStream will not be interrupted.  For this reason, it is a good idea to
     * retrieve the entire blob (e.g. to disk) from a remote datastore prior to returning this stream.
     *     
     * @return the input stream to the blob
     * @throws IOException if the input stream to the blob cannot be obtained
     */
    public abstract InputStream getInputStream() throws IOException;

    public File getFile() throws IOException {
        throw new NotImplementedException();
    }

    public boolean isSnapshot() {
        return fromVersion == HollowConstants.VERSION_NONE;
    }

    public boolean isReverseDelta() {
        return toVersion < fromVersion;
    }

    public long getFromVersion() {
        return fromVersion;
    }

    public long getToVersion() {
        return toVersion;
    }
}
