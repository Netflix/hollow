package com.netflix.hollow.api.common;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import org.junit.Test;

public class VersionMinterWithCounterTest {

    @Test
    public void testTimestampFromVersion() throws ParseException {
        assertEquals(1602538656000l, VersionMinterWithCounter.timestampFromVersion(20201012213736000l));
        assertEquals(1602538656000l, VersionMinterWithCounter.timestampFromVersion(20201012213736001l));
    }
}
