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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.read.filter.HollowFilterConfig;

import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * A utility to create and keep up-to-date a {@link HollowReadStateEngine} from a {@link HollowWriteStateEngine} 
 */
public class StateEngineRoundTripper {

    /**
     * Create a brand-new {@link HollowReadStateEngine} with the dataset populated in the provided {@link HollowWriteStateEngine} 
     */
    public static HollowReadStateEngine roundTripSnapshot(HollowWriteStateEngine writeEngine) throws IOException {
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTripSnapshot(writeEngine, readEngine);
        return readEngine;
    }

    /**
     * Populate the provided {@link HollowReadStateEngine} with the dataset currently in the provided {@link HollowWriteStateEngine} 
     */
    public static void roundTripSnapshot(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws IOException {
        roundTripSnapshot(writeEngine, readEngine, null);
    }

    /**
     * Populate the provided {@link HollowReadStateEngine} with the dataset currently in the provided {@link HollowWriteStateEngine}.
     * <p>
     * Apply the provided {@link HollowFilterConfig}.
     */
    public static void roundTripSnapshot(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, HollowFilterConfig filter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);
        writeEngine.prepareForNextCycle();


        HollowBlobReader reader = new HollowBlobReader(readEngine);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        if(filter == null)
            reader.readSnapshot(is);
        else
            reader.readSnapshot(is, filter);
    }

    /**
     * Update the provided {@link HollowReadStateEngine} with the new state currently available in the {@link HollowWriteStateEngine}.
     * <p>
     * It is assumed that the readEngine is currently populated with the prior state from the writeEngine.
     */
    public static void roundTripDelta(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeDelta(baos);
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.applyDelta(new ByteArrayInputStream(baos.toByteArray()));
        writeEngine.prepareForNextCycle();
    }

}
