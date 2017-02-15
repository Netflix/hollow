/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.api.consumer;


/**
 * Alpha API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
public class HollowConsumer {

    // TODO: timt: is this needed, or do we just use a HollowReadStateEngine in place of this? Created for now
    //   to have symmetry with HollowProducer.WriteState
    public static interface ReadState {
        long getVersion();
        HollowReadStateEngine getStateEngine();
    }

    public static interface StateRetriever {
        ReadState retrieveLatestAnnounced();
        long latestAnnouncedVersion();
    }

    // TODO: timt: don't use HollowBlobRetriever or HollowClient; this is temporary bridge code
    public static class BlobStoreStateRetriever implements StateRetriever {
        private final HollowAnnouncementWatcher announcementWatcher;
        private final HollowBlobRetriever blobRetriever;

        long get();
    }

}
