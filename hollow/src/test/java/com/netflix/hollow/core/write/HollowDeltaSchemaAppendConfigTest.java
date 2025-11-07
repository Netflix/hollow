package com.netflix.hollow.core.write;

import org.junit.Assert;
import org.junit.Test;

public class HollowDeltaSchemaAppendConfigTest {

    @Test
    public void testDefaultDisabled() {
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(false);
        Assert.assertFalse(config.isEnabled());
    }

    @Test
    public void testEnabled() {
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(true);
        Assert.assertTrue(config.isEnabled());
    }

    @Test
    public void testToString() {
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(true);
        Assert.assertTrue(config.toString().contains("enabled=true"));
    }

    @Test
    public void testToStringDisabled() {
        HollowDeltaSchemaAppendConfig config = new HollowDeltaSchemaAppendConfig(false);
        Assert.assertTrue(config.toString().contains("enabled=false"));
    }
}
