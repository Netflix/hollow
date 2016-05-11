package com.netflix.vms.transformer.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class VMSInputDataLogMessage {

    private final Map<String, MutationGroupInputData> inputData;

    public VMSInputDataLogMessage(Map<String, String> inputBlobHeaders) {
        this.inputData = new HashMap<String, MutationGroupInputData>();

        for(Map.Entry<String, String> entry : inputBlobHeaders.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputBlobHeaders.get(mutationGroup + "_events");

                inputData.put(mutationGroup, new MutationGroupInputData(latestColdstartVersion, latestEventId));
            }
        }
    }


    public Map<String, MutationGroupInputData> getInputData() {
        return inputData;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }


    static class MutationGroupInputData {
        private final String latestColdstartVersion;
        private final String latestEventId;

        public MutationGroupInputData(String latestColdstartVersion, String latestEventId) {
            this.latestColdstartVersion = latestColdstartVersion;
            this.latestEventId = latestEventId;
        }

        public String getLatestColdstartVersion() {
            return latestColdstartVersion;
        }

        public String getLatestEventId() {
            return latestEventId;
        }
    }

}
