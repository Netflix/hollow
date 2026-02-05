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
package com.netflix.hollow.core.memory;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.lang.reflect.Field;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ByteArrayOrdinalTest {

    private static final int TEST_SOFT_ORDINAL_LIMIT = 1 << 3; // 8

    /**
     * Sets SOFT_ORDINAL_LIMIT to a small value for testing and returns the original value.
     */
    private int setSoftOrdinalLimitForTesting() throws Exception {
        Field softLimitField = ByteArrayOrdinalMap.class.getDeclaredField("SOFT_ORDINAL_LIMIT");
        softLimitField.setAccessible(true);
        int originalValue = softLimitField.getInt(null);
        softLimitField.set(null, TEST_SOFT_ORDINAL_LIMIT);

        return originalValue;
    }

    /**
     * Restores the original SOFT_ORDINAL_LIMIT value to avoid impacting other tests.
     */
    private void restoreSoftOrdinalLimit(int originalValue) throws Exception {
        Field softLimitField = ByteArrayOrdinalMap.class.getDeclaredField("SOFT_ORDINAL_LIMIT");
        softLimitField.setAccessible(true);
        softLimitField.set(null, originalValue);
    }

    @Test
    public void testResize() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();

        int[] ordinals = new int[179];
        for (int i = 0; i < ordinals.length; i++) {
            ordinals[i] = m.getOrAssignOrdinal(createBuffer("TEST" + i));
        }

        m.resize(4096);

        int[] newOrdinals = new int[ordinals.length];
        for (int i = 0; i < ordinals.length; i++) {
            newOrdinals[i] = m.get(createBuffer("TEST" + i));
        }

        Assert.assertArrayEquals(ordinals, newOrdinals);
    }

    @Test
    public void testResizeWhenEmpty() {
        ByteArrayOrdinalMap m = new ByteArrayOrdinalMap();
        m.resize(4096);

        int[] ordinals = new int[179];
        for (int i = 0; i < ordinals.length; i++) {
            ordinals[i] = m.getOrAssignOrdinal(createBuffer("TEST" + i));
        }

        m.resize(16384);

        int[] newOrdinals = new int[ordinals.length];
        for (int i = 0; i < ordinals.length; i++) {
            newOrdinals[i] = m.get(createBuffer("TEST" + i));
        }

        Assert.assertArrayEquals(ordinals, newOrdinals);
    }

    @Test
    public void testIgnoreSoftLimits() throws Exception {
        int originalLimit = setSoftOrdinalLimitForTesting();
        try {
            ByteArrayOrdinalMap m = new ByteArrayOrdinalMap(256, false);

            for (int i = 0; i < TEST_SOFT_ORDINAL_LIMIT; i++) {
                m.getOrAssignOrdinal(createBuffer("TEST" + i));
            }

            // add 1 extra record to breach the SOFT_ORDINAL_LIMIT to cause exception
            try {
                m.getOrAssignOrdinal(createBuffer("TEST_BREACH_LIMIT"));
                fail("Expected IllegalStateException to be thrown when soft ordinal limit is breached with ignoreSoftLimits=false");
            } catch (IllegalStateException e) {
                Assert.assertTrue(e.getMessage().contains("exceeds the soft ordinal limit"));
            }

            // when ignoreSoftLimits set to true, limit breach should be ignored.
            m.setIgnoreSoftLimits(true);
            m.getOrAssignOrdinal(createBuffer("TEST_OVER_LIMIT"));
        } finally {
            restoreSoftOrdinalLimit(originalLimit);
        }
    }

    @Test
    public void testLogSoftLimitsBreach() throws Exception {
        int originalLimit = setSoftOrdinalLimitForTesting();
        Logger logger = Logger.getLogger(ByteArrayOrdinalMap.class.getName());
        Handler mockHandler = mock(Handler.class);
        logger.addHandler(mockHandler);
        logger.setLevel(Level.ALL);
        boolean originalUseParentHandlers = logger.getUseParentHandlers();
        logger.setUseParentHandlers(false);
        try {
            ByteArrayOrdinalMap m = new ByteArrayOrdinalMap(256, true);

            // breach SOFT_ORDINAL_LIMIT 5 times should only result in 1 related log being published.
            for (int i = 0; i < TEST_SOFT_ORDINAL_LIMIT + 5; i++) {
                m.getOrAssignOrdinal(createBuffer("TEST" + i));
            }

            ArgumentCaptor<LogRecord> logCaptor = ArgumentCaptor.forClass(LogRecord.class);
            verify(mockHandler, times(1)).publish(logCaptor.capture());

            // after reset, add new records to breach SOFT_ORDINAL_LIMIT for another 5 times, should result in 1 more
            // related log being published.
            m.resetLogSoftLimitsBreach();
            for (int i = TEST_SOFT_ORDINAL_LIMIT + 5; i < TEST_SOFT_ORDINAL_LIMIT + 10; i++) {
                m.getOrAssignOrdinal(createBuffer("TEST" + i));
            }
            verify(mockHandler, times(2)).publish(logCaptor.capture());
        } finally {
            restoreSoftOrdinalLimit(originalLimit);
            logger.removeHandler(mockHandler);
            logger.setUseParentHandlers(originalUseParentHandlers);
        }
    }

    static ByteDataArray createBuffer(String s) {
        return write(new ByteDataArray(), s);
    }

    static ByteDataArray write(ByteDataArray bdb, String s) {
        for (byte b : s.getBytes()) {
            bdb.write(b);
        }
        return bdb;
    }
}
