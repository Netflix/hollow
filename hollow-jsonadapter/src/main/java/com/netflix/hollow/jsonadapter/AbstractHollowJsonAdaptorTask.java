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
package com.netflix.hollow.jsonadapter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.jsonadapter.chunker.JsonArrayChunker;
import com.netflix.hollow.jsonadapter.field.FieldProcessor;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



public abstract class AbstractHollowJsonAdaptorTask {
    public static boolean isDebug = false;

    protected final int maxWorkQueue = 2048;
    protected final SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "json-adaptor");

    protected final String typeName;
    protected final String actionName;
    protected final Map<String, Map<String, FieldProcessor>> fieldProcessors;

    public AbstractHollowJsonAdaptorTask(String typeName) {
        this(typeName, null);
    }

    public AbstractHollowJsonAdaptorTask(String typeName, String actionName) {
        this.typeName = typeName;
        this.actionName = actionName;
        this.fieldProcessors = new HashMap<String, Map<String,FieldProcessor>>();
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
        processFile(new FileReader(f), maxSample);
    }

    ///TODO: Many parse failures can cause out of memory errors.
    protected void processFile(Reader r, int maxSample) throws Exception {
        JsonArrayChunker chunker = new JsonArrayChunker(r, executor);
        chunker.initialize();

        int counter = 0;

        Reader jsonObj = chunker.nextChunk();
        while(jsonObj != null && counter < maxSample) {

            final Reader currentObject = jsonObj;

            executor.execute(new Runnable() {
                public void run() {
                    try {
                        JsonFactory factory = new JsonFactory();
                        JsonParser parser = factory.createParser(currentObject);
                        processRecord(parser);
                    } catch(Exception e){
                        throw new RuntimeException(e);
                    }
                }
            });

            while(executor.getQueue().size() > maxWorkQueue) {
                Thread.sleep(5);
            }

            counter++;

            jsonObj.close();
            jsonObj = chunker.nextChunk();
        }

        executor.awaitSuccessfulCompletion();
    }

    protected abstract int processRecord(JsonParser parser) throws IOException;

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