package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.InputDataVersionIds;

import com.netflix.vms.transformer.common.TransformerLogger;
import java.util.Map;

public class VMSInputDataVersionLogger {

    public static void logInputVersions(Map<String, String> inputBlobHeaders, TransformerLogger logger) {
        for(Map.Entry<String, String> entry : inputBlobHeaders.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputBlobHeaders.get(mutationGroup + "_events");

                logger.info(InputDataVersionIds,
                        "mutationGroup=" + mutationGroup +
                        " latestEventId=" + latestEventId +
                        " coldstartVersionId=" + latestColdstartVersion +
                        " coldstartKeybase=dummyValue" +
                        " coldstartS3Filename=anotherDummyValue" +
                        " isColdstartPinned=false");
            }
        }

    }
}
