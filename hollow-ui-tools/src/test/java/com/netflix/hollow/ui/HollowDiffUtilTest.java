package com.netflix.hollow.ui;

import static com.netflix.hollow.ui.HollowDiffUtil.formatBytes;

import org.junit.Assert;
import org.junit.Test;

public class HollowDiffUtilTest {

    @Test
    public void testFormatBytes() {
        sampleTesting(1,"B", -10, 2, 0, 2, 10);
        sampleTesting(Math.pow(2, 10),"KiB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 20),"MiB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 30),"GiB", -10, 30, 30, 100);
        sampleTesting(Math.pow(2, 40),"TiB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 50),"PiB", -100, 50, 30, 100);

        Assert.assertEquals( "-1,023 B", formatBytes(-1023));
        Assert.assertEquals( "-1 KiB", formatBytes(-1024));

        Assert.assertEquals( "1,000 TiB", formatBytes(1000 * (long)Math.pow(2, 40)));
        Assert.assertEquals( "1 PiB", formatBytes(1024 * (long)Math.pow(2, 40)));

        Assert.assertEquals( "8 EiB", formatBytes(Long.MAX_VALUE));

        // Validate Decimal
        Assert.assertEquals( "95.37 MiB", formatBytes(100000000));
        Assert.assertEquals( "-9.54 MiB", formatBytes(-10000000));
        Assert.assertEquals( "1.95 KiB", formatBytes(2001));
        Assert.assertEquals( "19.53 KiB", formatBytes(20000));
        Assert.assertEquals( "186.26 GiB", formatBytes(200000000000L));
    }

    private void sampleTesting(double multiple, String unit, long ... bytes) {
        for(long b : bytes) {
            Assert.assertEquals(b + " " + unit, formatBytes(b * (long)multiple));
        }
    }
}
