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
package com.netflix.hollow.core.read;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowBlobRandomizedTagTest {

    byte snapshot[];
    byte delta1[];
    byte delta2[];
    byte reversedelta1[];
    byte reversedelta2[];
    byte snapshot2[];


    @Before
    public void setUp() throws IOException {

        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);

        stateEngine.prepareForWrite();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        snapshot = baos.toByteArray();

        stateEngine.prepareForNextCycle();
        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        delta1 = baos.toByteArray();

        baos = new ByteArrayOutputStream();
        writer.writeReverseDelta(baos);
        reversedelta1 = baos.toByteArray();

        stateEngine.prepareForNextCycle();
        stateEngine.prepareForNextCycle();
        stateEngine.prepareForNextCycle();
        stateEngine.prepareForWrite();
        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        delta2 = baos.toByteArray();

        baos = new ByteArrayOutputStream();
        writer.writeReverseDelta(baos);
        reversedelta2 = baos.toByteArray();

        baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        snapshot2 = baos.toByteArray();
    }


    @Test
    public void applyingIncorrectDeltaFails() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(HollowBlobInput.serial(snapshot));
        reader.applyDelta(HollowBlobInput.serial(delta1));

        try {
            reader.applyDelta(HollowBlobInput.serial(delta1));
            Assert.fail("Should have refused to apply delta to incorrect state");
        } catch(IOException expected) { }

        reader.applyDelta(HollowBlobInput.serial(delta2));
    }

    @Test
    public void applyingReverseDeltaToIncorrectStateFails() throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(HollowBlobInput.serial(snapshot2));

        try {
            reader.applyDelta(HollowBlobInput.serial(reversedelta1));
            Assert.fail("Should have refused to apply reverse delta to incorrect state");
        } catch(IOException expected) { }

        reader.applyDelta(HollowBlobInput.serial(reversedelta2));
        reader.applyDelta(HollowBlobInput.serial(reversedelta1));

        try {
            reader.applyDelta(HollowBlobInput.serial(delta2));
            Assert.fail("Should have refused to apply delta to incorrect state");
        } catch(IOException expected) { }

        reader.applyDelta(HollowBlobInput.serial(delta1));
        reader.applyDelta(HollowBlobInput.serial(delta2));
    }

}
