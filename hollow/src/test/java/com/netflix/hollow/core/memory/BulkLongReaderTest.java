/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.core.memory;

import static org.junit.Assert.assertArrayEquals;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.nio.ByteBuffer;
import org.junit.Test;

public class BulkLongReaderTest {
    @Test
    public void readFully_givenOffsetAndMultipleDestinations_preservesValues() throws Exception {
        byte[] bytes = ByteBuffer.allocate(40).putLong(11).putLong(22).putLong(33)
                .putLong(44).putLong(55).array();
        try (HollowBlobInput input = HollowBlobInput.serial(bytes)) {
            BulkLongReader reader = new BulkLongReader(input, 2);
            long[] first = {7, 7, 7, 7, 7};
            reader.readFully(first, 1, 3);
            long[] second = {8, 8, 8};
            reader.readFully(second, 0, 0);
            reader.readFully(second, 1, 2);
            assertArrayEquals(new long[] {7, 11, 22, 33, 7}, first);
            assertArrayEquals(new long[] {8, 44, 55}, second);
        }
    }
}
