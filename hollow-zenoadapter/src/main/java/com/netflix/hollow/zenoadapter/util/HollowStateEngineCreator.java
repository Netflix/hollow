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
package com.netflix.hollow.zenoadapter.util;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.zenoadapter.HollowSerializationFramework;
import com.netflix.zeno.fastblob.FastBlobStateEngine;
import com.netflix.zeno.fastblob.state.FastBlobTypeDeserializationState;
import com.netflix.zeno.serializer.NFTypeSerializer;
import com.netflix.zeno.serializer.SerializerFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HollowStateEngineCreator {

    private final FastBlobStateEngine stateEngine;
    private final HollowSerializationFramework hollowFramework;

    private final Map<String, int[]> ordinalMappings = new ConcurrentHashMap<String, int[]>();


    public HollowStateEngineCreator(FastBlobStateEngine stateEngine, SerializerFactory serializerFactory, HollowObjectHashCodeFinder hashCodeFinder) {
        this.stateEngine = stateEngine;
        this.hollowFramework = new HollowSerializationFramework(serializerFactory, hashCodeFinder);
    }

    public void copyAllObjectsToHollowStateEngine() {
        hollowFramework.prepareForNextCycle();
        ordinalMappings.clear();

        for(NFTypeSerializer<?> serializer : stateEngine.getOrderedSerializers()) {
            FastBlobTypeDeserializationState<?> typeState = stateEngine.getTypeDeserializationState(serializer.getName());
            typeState.createIdentityOrdinalMap();
        }

        SimultaneousExecutor executor = new SimultaneousExecutor(8, getClass(), "copy-all");

        for(final NFTypeSerializer<?> serializer : stateEngine.getOrderedSerializers()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ADDING OBJECTS FOR TYPE " + serializer.getName());
                    FastBlobTypeDeserializationState<Object> state = stateEngine.getTypeDeserializationState(serializer.getName());

                    int maxOrdinal = state.maxOrdinal();
                    int mapping[] = new int[maxOrdinal + 1];
                    Arrays.fill(mapping, -1);

                    for(int i = 0; i <= maxOrdinal; i++) {
                        Object obj = state.get(i);
                        if(obj != null) {
                            int ordinal = hollowFramework.add(serializer.getName(), obj);
                            while(ordinal >= mapping.length) {
                                int oldLength = mapping.length;
                                mapping = Arrays.copyOf(mapping, mapping.length * 2);
                                Arrays.fill(mapping, oldLength, mapping.length, -1);
                            }
                            mapping[ordinal] = i;
                        }
                    }

                    ordinalMappings.put(serializer.getName(), mapping);
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeHollowBlobSnapshot(OutputStream os) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(hollowFramework.getStateEngine());

        writer.writeSnapshot(os);

        os.flush();
    }

    public void writeHollowBlobDelta(OutputStream os) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(hollowFramework.getStateEngine());

        writer.writeDelta(os);

        os.flush();
    }

    public void writeHollowBlobReverseDelta(OutputStream os) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(hollowFramework.getStateEngine());

        writer.writeReverseDelta(os);

        os.flush();
    }

    public HollowWriteStateEngine getWriteStateEngine() {
        return hollowFramework.getStateEngine();
    }

    public HollowSerializationFramework getHollowSerializationFramework() {
        return hollowFramework;
    }

    public void writeHollowToFastBlobIndex(OutputStream os) throws IOException {
        System.out.println("WRITING HOLLOWBLOB -> FASTBLOB ORDINAL INDEX");

        DataOutputStream dos = new DataOutputStream(os);
        dos.writeShort(ordinalMappings.size());
        for(Map.Entry<String, int[]> entry : ordinalMappings.entrySet()) {
            dos.writeUTF(entry.getKey());
            dos.writeInt(entry.getValue().length);
            for(int i = 0; i < entry.getValue().length; i++) {
                dos.writeInt(entry.getValue()[i]);
            }
        }

        dos.flush();
        dos.close();
    }

}
