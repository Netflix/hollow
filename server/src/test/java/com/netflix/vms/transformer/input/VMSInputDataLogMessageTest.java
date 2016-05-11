package com.netflix.vms.transformer.input;

import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class VMSInputDataLogMessageTest {

    @Test
    public void test() {
        Map<String, String> inputBlobHeaders = new HashMap<>();

        inputBlobHeaders.put("MPL_events", "12345");
        inputBlobHeaders.put("MPL_coldstart", "123456");
        inputBlobHeaders.put("CATALOG_events", "23456");
        inputBlobHeaders.put("CATALOG_coldstart", "234567");

        VMSInputDataLogMessage msg = new VMSInputDataLogMessage(inputBlobHeaders);

        Assert.assertEquals(
                "{\"inputData\":{\"CATALOG\":{\"latestColdstartVersion\":\"234567\",\"latestEventId\":\"23456\"},\"MPL\":{\"latestColdstartVersion\":\"123456\",\"latestEventId\":\"12345\"}}}",
                msg.toString()
        );
    }

}
