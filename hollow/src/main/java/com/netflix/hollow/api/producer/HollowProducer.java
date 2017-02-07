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
package com.netflix.hollow.api.producer;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.netflix.hollow.api.StateTransition;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

public class HollowProducer {

    private final VersionMinter versionMinter;
    private final HollowPublisher publisher;
    private final HollowAnnouncer announcer;
    private final HollowWriteStateEngine writeEngine;

    private StateTransition announced;

    public HollowProducer(VersionMinter versionMinter, HollowPublisher publisher, HollowAnnouncer announcer) {
        this.versionMinter = versionMinter;
        this.publisher = publisher;
        this.announcer = announcer;

        writeEngine = new HollowWriteStateEngine();
        announced = new StateTransition();
    }

    public void restoreFrom(HollowReadStateEngine priorAnnouncedState, long priorAnnouncedVersion) {
        writeEngine.restoreFrom(priorAnnouncedState);
        announced = new StateTransition(priorAnnouncedVersion);
    }

    /**
     * Each cycle produces a single state.
     */
    public void produce(Task task) {
        try {
            WriteState writeState = beginCycle(announced.advance(versionMinter.mint()));
            task.populate(writeState);
            publish(writeState);
            announced = announce(writeState);
        } catch(Throwable th) {
            th.printStackTrace();
            rollback();
        }
    }

    private WriteState beginCycle(StateTransition transition) {
        writeEngine.prepareForNextCycle();
        WriteState writeState = new WriteState(writeEngine, transition);
        System.out.println("Beginning cycle " + transition);
        return writeState;
    }

    private void publish(WriteState writeState) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        StateTransition transition = writeState.getTransition();

        HollowBlob snapshot = publisher.openSnapshot(transition);
        try {
            writer.writeSnapshot(snapshot.getOutputStream());
            snapshot.finish();

            if(transition.isDelta()) {
                HollowBlob delta = publisher.openDelta(transition);
                HollowBlob reverseDelta = publisher.openReverseDelta(transition);
                try {
                    writer.writeDelta(delta.getOutputStream());
                    delta.finish();

                    writer.writeReverseDelta(reverseDelta.getOutputStream());
                    reverseDelta.finish();

                    publisher.publish(delta);
                    publisher.publish(reverseDelta);
                } finally {
                    delta.close();
                    reverseDelta.close();
                }
            }

            /// it's ok to fail to publish a snapshot, as long as you don't miss too many in a row.
            /// you can add a timeout or even do this in a separate thread.
            try {
                publisher.publish(snapshot);
            } catch(Throwable ignored) {
                ignored.printStackTrace(); // TODO: timt: log and notify listerners
            }
        } finally {
            snapshot.close();
        }
    }

    private void rollback() {
        writeEngine.resetToLastPrepareForNextCycle();
    }

    private StateTransition announce(WriteState writeState) {
        announcer.announce(writeState.getVersion());
        StateTransition transition = writeState.getTransition();
        return transition;
    }

    public static interface Task {
        void populate(WriteState newState);
    }

}
