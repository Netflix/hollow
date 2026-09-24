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
package com.netflix.hollow.api.client;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChecksumValidatorTest {

    @Test
    public void testValidateChecksumMatch() {
        // Arrange
        HollowReadStateEngine stateEngine = mock(HollowReadStateEngine.class);
        HollowChecksum checksum = mock(HollowChecksum.class);
        when(checksum.intValue()).thenReturn(12345);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum", "12345");

        ChecksumValidator validator = new ChecksumValidator();

        // Act
        boolean isValid = validator.validate(stateEngine, metadata, checksum);

        // Assert
        assertTrue("Checksum should match", isValid);
    }

    @Test
    public void testValidateChecksumMismatch() {
        HollowReadStateEngine stateEngine = mock(HollowReadStateEngine.class);
        HollowChecksum checksum = mock(HollowChecksum.class);
        when(checksum.intValue()).thenReturn(12345);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("hollow.checksum", "99999");

        ChecksumValidator validator = new ChecksumValidator();

        boolean isValid = validator.validate(stateEngine, metadata, checksum);

        assertFalse("Checksum should not match", isValid);
    }

    @Test
    public void testValidateNoChecksumInMetadata() {
        HollowReadStateEngine stateEngine = mock(HollowReadStateEngine.class);
        HollowChecksum checksum = mock(HollowChecksum.class);

        Map<String, String> metadata = new HashMap<>();

        ChecksumValidator validator = new ChecksumValidator();

        boolean isValid = validator.validate(stateEngine, metadata, checksum);

        assertTrue("Should return true when no checksum in metadata", isValid);
    }
}
