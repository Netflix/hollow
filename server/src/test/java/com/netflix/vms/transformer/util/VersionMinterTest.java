package com.netflix.vms.transformer.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.vms.transformer.common.VersionMinter;
import org.junit.Before;
import org.junit.Test;

public class VersionMinterTest {
    private VersionMinter subject;

    @Before
    public void setup() {
        this.subject = new SequenceVersionMinter();
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
