package com.netflix.hollow.ui;

import static com.netflix.hollow.ui.HollowDiffUtil.formatBytes;

import org.junit.Assert;
import org.junit.Test;

public class HollowDiffUtilTest {

    @Test
    public void testFormatBytes() {
        sampleTesting(1,"B", -10, 2, 0, 2, 10);
        sampleTesting(Math.pow(2, 10),"KB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 20),"MB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 30),"GB", -10, 30, 30, 100);
        sampleTesting(Math.pow(2, 40),"TB", -100, 50, 30, 100);
        sampleTesting(Math.pow(2, 50),"PB", -100, 50, 30, 100);

        Assert.assertEquals( "-1,023 B", formatBytes(-1023));
        Assert.assertEquals( "-1 KB", formatBytes(-1024));

        Assert.assertEquals( "1,000 TB", formatBytes(1000 * (long)Math.pow(2, 40)));
        Assert.assertEquals( "1 PB", formatBytes(1024 * (long)Math.pow(2, 40)));

        Assert.assertEquals( "8 EB", formatBytes(Long.MAX_VALUE));
    }

    private void sampleTesting(double multiple, String unit, long ... bytes) {
        for(long b : bytes) {
            Assert.assertEquals(b + " " + unit, formatBytes(b * (long)multiple));
        }
    }
}
