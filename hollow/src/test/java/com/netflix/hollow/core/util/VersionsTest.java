package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.VERSION_LATEST;
import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;

import org.junit.Assert;
import org.junit.Test;

public class VersionsTest {

    @Test
    public void testPrettyPrint() {
        Assert.assertEquals(Versions.PRETTY_VERSION_NONE, Versions.prettyVersion(VERSION_NONE));
        Assert.assertEquals(Versions.PRETTY_VERSION_LATEST, Versions.prettyVersion(VERSION_LATEST));
        Assert.assertEquals("123", Versions.prettyVersion(123l));
    }

}
