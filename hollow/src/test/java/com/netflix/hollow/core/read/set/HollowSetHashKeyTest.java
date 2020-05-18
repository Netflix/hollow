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
package com.netflix.hollow.core.read.set;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowHashKey;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetHashKeyTest {
    
    @Test
    public void testSetHashKeys() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.useDefaultHashKeys();
        
        mapper.add(new TestTopLevelObject(1, new Obj(1, "US", 100), new Obj(2, "CA", 200), new Obj(3, "IT", 300), new Obj(4, "GB", 400), new Obj(5, "IT", 500)));
        
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
        
        GenericHollowObject obj = new GenericHollowObject(readEngine, "TestTopLevelObject", 0); 
        
        GenericHollowObject element = (GenericHollowObject) obj.getSet("setById").findElement(1);
        Assert.assertEquals("US", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(2);
        Assert.assertEquals("CA", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(3);
        Assert.assertEquals("IT", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(4);
        Assert.assertEquals("GB", element.getObject("country").getString("value"));
        element = (GenericHollowObject) obj.getSet("setById").findElement(5);
        Assert.assertEquals("IT", element.getObject("country").getString("value"));

        element = (GenericHollowObject)obj.getSet("setByIdCountry").findElement(1, "US");
        Assert.assertEquals(1, element.getInt("id"));
        element = (GenericHollowObject)obj.getSet("setByIdCountry").findElement(2, "CA");
        Assert.assertEquals(2, element.getInt("id"));
        element = (GenericHollowObject)obj.getSet("setByIdCountry").findElement(3, "IT");
        Assert.assertEquals(3, element.getInt("id"));
        element = (GenericHollowObject)obj.getSet("setByIdCountry").findElement(4, "GB");
        Assert.assertEquals(4, element.getInt("id"));
        element = (GenericHollowObject)obj.getSet("setByIdCountry").findElement(5, "IT");
        Assert.assertEquals(5, element.getInt("id"));
        
        element = (GenericHollowObject)obj.getSet("intSet").findElement(100);
        Assert.assertEquals(100, element.getInt("value"));
        element = (GenericHollowObject)obj.getSet("intSet").findElement(200);
        Assert.assertEquals(200, element.getInt("value"));
        element = (GenericHollowObject)obj.getSet("intSet").findElement(300);
        Assert.assertEquals(300, element.getInt("value"));
        element = (GenericHollowObject)obj.getSet("intSet").findElement(400);
        Assert.assertEquals(400, element.getInt("value"));
        element = (GenericHollowObject)obj.getSet("intSet").findElement(500);
        Assert.assertEquals(500, element.getInt("value"));
    }
    
    
    @SuppressWarnings("unused")
    private static class TestTopLevelObject {
        int id;
        
        @HollowTypeName(name="SetById")
        @HollowHashKey(fields="id")
        Set<Obj> setById;
        
        @HollowTypeName(name="SetByIdCountry")
        @HollowHashKey(fields={"id", "country.value"})
        Set<Obj> setByIdCountry;
        
        Set<Integer> intSet;
        
        public TestTopLevelObject(int id, Obj... elements) {
            this.id = id;
            this.setById = new HashSet<Obj>();
            this.setByIdCountry = new HashSet<Obj>();
            this.intSet = new HashSet<Integer>();
            
            for(int i=0;i<elements.length;i++) {
                setById.add(elements[i]);
                setByIdCountry.add(elements[i]);
                intSet.add((int)elements[i].extraValue);
            }
                
        }
    }
    
    @SuppressWarnings("unused")
    private static class Obj {
        int id;
        String country;
        long extraValue;
        
        public Obj(int id, String country, long extraValue) {
            this.id = id;
            this.country = country;
            this.extraValue = extraValue;
        }
    }

}
