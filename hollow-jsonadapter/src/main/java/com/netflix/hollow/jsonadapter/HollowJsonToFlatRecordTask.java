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

import com.fasterxml.jackson.core.JsonParser;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecordWriter;
import com.netflix.hollow.core.write.objectmapper.flatrecords.HollowSchemaIdentifierMapper;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

public class HollowJsonToFlatRecordTask extends AbstractHollowJsonAdaptorTask {

    private final HollowJsonAdapter adapter;
    private final HollowSchemaIdentifierMapper schemaIdMapper;
    private final Consumer<FlatRecord> action;

    private final ThreadLocal<FlatRecordWriter> flatRecordWriter;

    public HollowJsonToFlatRecordTask(HollowJsonAdapter adapter,
            HollowSchemaIdentifierMapper schemaIdMapper,
            Consumer<FlatRecord> action) {
        super(adapter.getTypeName());
        this.adapter = adapter;
        this.schemaIdMapper = schemaIdMapper;
        this.flatRecordWriter = new ThreadLocal<>();
        this.action = action;
    }

    public void process(Reader jsonReader) throws Exception {
        processFile(jsonReader, Integer.MAX_VALUE);
    }

    @Override
    protected int processRecord(JsonParser parser) throws IOException {
        FlatRecordWriter recWriter = getFlatRecordWriter();
        int ordinal = adapter.processRecord(parser, recWriter);
        FlatRecord rec = recWriter.generateFlatRecord();
        action.accept(rec);
        return ordinal;
    }

    private FlatRecordWriter getFlatRecordWriter() {
        FlatRecordWriter writer = flatRecordWriter.get();
        if(writer == null) {
            writer = new FlatRecordWriter(adapter.stateEngine, schemaIdMapper);
            flatRecordWriter.set(writer);
        }
        writer.reset();
        return writer;
    }

}
