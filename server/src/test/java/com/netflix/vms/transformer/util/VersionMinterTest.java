package com.netflix.vms.transformer.util;
import com.netflix.vms.transformer.util.VersionMinter;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class VersionMinterTest {
    private VersionMinter subject;

    @Before
    public void setup() {
        this.subject = new VersionMinter();
    }

    @Test
    public void has17Digits() {
        assertThat(String.valueOf(subject.mintANewVersion())).hasSize(17);
    }

    @Test
    public void lastThreeDigitsIncrementUntilRollingOver() {
        for(int i=0;i<4000;i++) {
            long newVersion = subject.mintANewVersion();
            long versionCounter = newVersion % 1000;

            assertThat(versionCounter).isEqualTo((i+1)%1000);
        }
    }
}
