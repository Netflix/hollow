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
package com.netflix.hollow.core.write;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class DefinedHashHeadersTest {

    @Test
    public void definedHashTypesAreSentInHollowHeader() throws IOException {
        DefaultHashCodeFinder hasher = new DefaultHashCodeFinder("DefinedHash1", "DefinedHash2", "DefinedHash3");
        
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine(hasher);
        
        stateEngine.prepareForWrite();
        
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);
        
        HollowReadStateEngine readEngine = new HollowReadStateEngine(true);
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        
        String headerTag = readEngine.getHeaderTag(HollowObjectHashCodeFinder.DEFINED_HASH_CODES_HEADER_NAME);
        
        String types[] = headerTag.split(",");
        
        Set<String> definedHashTypes = new HashSet<String>();
        
        for(String type : types) {
            definedHashTypes.add(type);
        }
        
        Assert.assertEquals(3, definedHashTypes.size());
        Assert.assertTrue(definedHashTypes.contains("DefinedHash1"));
        Assert.assertTrue(definedHashTypes.contains("DefinedHash2"));
        Assert.assertTrue(definedHashTypes.contains("DefinedHash3"));
    }
    
}
