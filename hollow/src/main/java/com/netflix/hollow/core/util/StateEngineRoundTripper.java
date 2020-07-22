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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility to create and keep up-to-date a {@link HollowReadStateEngine} from a {@link HollowWriteStateEngine} 
 */
public class StateEngineRoundTripper {

    /**
     * @param writeEngine the write state engine
     * @return a brand-new {@link HollowReadStateEngine} with the dataset populated in the provided {@link HollowWriteStateEngine}
     * @throws IOException if the round trip from write to read state failed
     */
    public static HollowReadStateEngine roundTripSnapshot(HollowWriteStateEngine writeEngine) throws IOException {
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        roundTripSnapshot(writeEngine, readEngine);
        return readEngine;
    }

    /**
     * Populate the provided {@link HollowReadStateEngine} with the dataset currently in the provided {@link HollowWriteStateEngine}
     *
     * @param writeEngine the write state engine
     * @param readEngine the read state engine
     * @throws IOException if the round trip from write to read state failed
     */
    public static void roundTripSnapshot(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws IOException {
        roundTripSnapshot(writeEngine, readEngine, null);
    }

    /**
     * Populate the provided {@link HollowReadStateEngine} with the dataset currently in the provided {@link HollowWriteStateEngine}.
     * <p>
     * Apply the provided {@link HollowFilterConfig}.
     *
     * @param writeEngine the write state engine
     * @param readEngine the read state engine
     * @param filter the filter configuration
     * @throws IOException if the round trip from write to read state failed
     */
    public static void roundTripSnapshot(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine, HollowFilterConfig filter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);
        writeEngine.prepareForNextCycle();

        HollowBlobReader reader = new HollowBlobReader(readEngine);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        try (HollowBlobInput in = HollowBlobInput.serial(is)) {
            if (filter == null)
                reader.readSnapshot(in);
            else
                reader.readSnapshot(in, filter);
        }
    }

    /**
     * Update the provided {@link HollowReadStateEngine} with the new state currently available in the {@link HollowWriteStateEngine}.
     * <p>
     * It is assumed that the readEngine is currently populated with the prior state from the writeEngine.
     *
     * @param writeEngine the write state engine
     * @param readEngine the read state engine
     * @throws IOException if the round trip from write to read state failed
     */
    public static void roundTripDelta(HollowWriteStateEngine writeEngine, HollowReadStateEngine readEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        writer.writeDelta(baos);
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        try (HollowBlobInput hbi = HollowBlobInput.serial(baos.toByteArray())) {
            reader.applyDelta(hbi);
        }
        writeEngine.prepareForNextCycle();
    }

}
