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
package com.netflix.hollow.tools.split;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.List;

/**
 * This tool can be used to shard a Hollow dataset into two or more smaller datasets. 
 */
public class HollowSplitter {

    private final HollowReadStateEngine inputStateEngine;
    private final HollowWriteStateEngine outputStateEngines[];
    private final HollowSplitterCopyDirector director;

    public HollowSplitter(HollowSplitterCopyDirector director, HollowReadStateEngine inputStateEngine) {
        this.inputStateEngine = inputStateEngine;
        this.outputStateEngines = new HollowWriteStateEngine[director.getNumShards()];
        this.director = director;

        List<HollowSchema> schemas = inputStateEngine.getSchemas();

        for(int i = 0; i < director.getNumShards(); i++)
            outputStateEngines[i] = HollowWriteStateCreator.createWithSchemas(schemas);
    }

    public void split() {
        prepareForNextCycle();

        SimultaneousExecutor executor = new SimultaneousExecutor(getNumberOfShards(), getClass(), "split");

        for(int i = 0; i < getNumberOfShards(); i++) {
            final int shardNumber = i;

            executor.execute(new Runnable() {
                public void run() {
                    HollowSplitterShardCopier copier = new HollowSplitterShardCopier(inputStateEngine, outputStateEngines[shardNumber], director, shardNumber);
                    copier.copy();
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    public HollowReadStateEngine getInputStateEngine() {
        return inputStateEngine;
    }

    public HollowWriteStateEngine getOutputShardStateEngine(int shardNumber) {
        return outputStateEngines[shardNumber];
    }

    public int getNumberOfShards() {
        return outputStateEngines.length;
    }

    private void prepareForNextCycle() {
        for(int i = 0; i < outputStateEngines.length; i++)
            outputStateEngines[i].prepareForNextCycle();
    }


}
