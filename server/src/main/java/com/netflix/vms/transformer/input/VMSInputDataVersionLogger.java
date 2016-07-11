package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataVersionIds;

import java.util.Map;

import com.netflix.vms.logging.TaggingLogger;

public class VMSInputDataVersionLogger {
    public static void logInputVersions(Map<String, String> inputBlobHeaders, TaggingLogger logger) {
        for(Map.Entry<String, String> entry : inputBlobHeaders.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputBlobHeaders.get(mutationGroup + "_events");
                String coldstartKeybase = inputBlobHeaders.get(mutationGroup + "_coldstartKeybase");

                logger.info(InputDataVersionIds,
                        "mutationGroup=" + mutationGroup +
                        " latestEventId=" + latestEventId +
                        " coldstartVersionId=" + latestColdstartVersion +
                        " coldstartKeybase=" + coldstartKeybase +
                        " coldstartS3Filename=null" +
                        " isColdstartPinned=false");
            }
        }
    }
}
