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
package com.netflix.hollow.protoadapter;

import com.google.protobuf.Message;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.protoadapter.field.FieldProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractHollowProtoAdapterTask {
    public static boolean isDebug = false;

    protected final int maxWorkQueue = 2048;
    protected final SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "proto-adapter");

    protected final String typeName;
    protected final String actionName;
    protected final Map<String, Map<String, FieldProcessor>> fieldProcessors;

    public AbstractHollowProtoAdapterTask(String typeName) {
        this(typeName, null);
    }

    public AbstractHollowProtoAdapterTask(String typeName, String actionName) {
        this.typeName = typeName;
        this.actionName = actionName;
        this.fieldProcessors = new HashMap<String, Map<String, FieldProcessor>>();
    }

    public String getTypeName() {
        return typeName;
    }

    public void addFieldProcessor(FieldProcessor... processors) {
        for (FieldProcessor p : processors) {
            Map<String, FieldProcessor> entityFieldProcessors = fieldProcessors.get(p.getEntityName());
            if(entityFieldProcessors == null) {
                entityFieldProcessors = new HashMap<String, FieldProcessor>();
                fieldProcessors.put(p.getEntityName(), entityFieldProcessors);
            }

            entityFieldProcessors.put(p.getFieldName(), p);
        }
    }

    public FieldProcessor getFieldProcessor(String entityName, String fieldName) {
        Map<String, FieldProcessor> entityFieldProcessors = fieldProcessors.get(entityName);
        if(entityFieldProcessors == null)
            return null;
        return entityFieldProcessors.get(fieldName);
    }

    protected void processFile(File f, int maxSample) throws Exception {
        processFile(new FileInputStream(f), maxSample);
    }

    protected void processFile(InputStream inputStream, int maxSample) throws Exception {
        // Protocol Buffer file processing requires knowledge of the specific message format and delimiters.
        // Unlike JSON (text-based with clear delimiters), Protocol Buffers are binary and need:
        //   1. A known message type/descriptor to parse with
        //   2. Length-delimited or size-prefixed message format in the file
        //
        // Recommended approach: Use CodedInputStream with writeDelimitedTo/parseDelimitedFrom pattern
        // or implement custom framing based on your specific use case.
        //
        // For now, use processMessage() directly with individual Protocol Buffer Message objects.
        throw new UnsupportedOperationException(
            "File-based Protocol Buffer processing not implemented. " +
            "Use processMessage(Message) directly with individual parsed messages."
        );
    }

    protected abstract int processMessage(Message message) throws IOException;

    protected boolean wait(List<Future<?>> futureList) throws Exception {
        boolean isSuccess = false;
        for (final Future<?> f : futureList) {
            try {
                f.get();
                isSuccess = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final ExecutionException e) {
                e.printStackTrace();
                throw e;
            }
        }

        return isSuccess;
    }
}
