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
package com.netflix.hollow.jsonadapter.chunker;

import com.netflix.hollow.core.util.SimultaneousExecutor;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class JsonArrayChunkerTest {


    @Test
    public void test() throws Exception {
        String jsonArray = "[ { \"f1\\\"\" : \"value1\", \"f2\" : { \"f1.1\" : \"hel}}{{{{lo \\\"w{orld\\\"\" } } , { \"obj2\" : \"f2.1\" } ]";

        JsonArrayChunker chunker = new JsonArrayChunker(new StringReader(jsonArray), new SimultaneousExecutor(getClass(), "test"), 4);
        chunker.initialize();

        String obj1 = IOUtils.toString(chunker.nextChunk());
        String obj2 = IOUtils.toString(chunker.nextChunk());

        Assert.assertEquals("{ \"f1\\\"\" : \"value1\", \"f2\" : { \"f1.1\" : \"hel}}{{{{lo \\\"w{orld\\\"\" } }", obj1);
        Assert.assertEquals("{ \"obj2\" : \"f2.1\" }", obj2);
    }

}
