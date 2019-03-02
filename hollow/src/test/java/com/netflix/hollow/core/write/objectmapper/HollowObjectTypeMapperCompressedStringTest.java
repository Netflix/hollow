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
package com.netflix.hollow.core.write.objectmapper;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import java.io.IOException;
import org.junit.Test;

/**
 * Checks String encoding handling in presence of JDK9 compressed String feature
 */
public class HollowObjectTypeMapperCompressedStringTest extends AbstractStateEngineTest {

    @Test
    public void referencedStringUtfHandling() throws IOException {
        final String stringValue = "龍爭虎鬥";// "Enter The Dragon"

        String readValue = roundTripReferenceStringValue(stringValue);
        assertEquals(stringValue, readValue);
    }

    @Test
    public void referenceStringLatin1Handling() throws IOException {
        final String stringValue = "abc";
        String readValue = roundTripReferenceStringValue(stringValue);
        assertEquals(stringValue, readValue);
    }

    @Test
    public void inlinedStringUtfHandling() throws IOException {
        final String stringValue = "龍爭虎鬥";// "Enter The Dragon"

        String readValue = roundTripInlinedStringValue(stringValue);
        assertEquals(stringValue, readValue);
    }

    @Test
    public void inlinedStringLatin1Handling() throws IOException {
        final String stringValue = "abc";
        String readValue = roundTripInlinedStringValue(stringValue);
        assertEquals(stringValue, readValue);
    }

    private String roundTripReferenceStringValue(String value) throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeA(value, 1, null, null));

        roundTripSnapshot();

        HollowObjectTypeDataAccess typeDataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("TypeA");
        int stringFieldIndex = typeDataAccess.getSchema().getPosition("a1");
        int stringValueOrdinal = typeDataAccess.readOrdinal(0, stringFieldIndex);

        HollowObjectTypeDataAccess stringDataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("String");
        int stringValueField = stringDataAccess.getSchema().getPosition("value");
        return stringDataAccess.readString(stringValueOrdinal, stringValueField);
    }

    private String roundTripInlinedStringValue(String value) throws IOException {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        mapper.add(new TypeD(value));

        roundTripSnapshot();

        HollowObjectTypeDataAccess typeDataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("TypeD");
        int stringFieldIndex = typeDataAccess.getSchema().getPosition("inlinedString");
        return typeDataAccess.readString(0, stringFieldIndex);
    }

    @Override
    protected void initializeTypeStates() {
    }
}
