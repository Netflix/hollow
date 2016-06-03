package com.netflix.vms.transformer.publish.workflow.job.impl;


public class HermesTopicProvider {
    private static final String HOLLOWBLOB_TOPIC_PREFIX = "vms.hollow.blob.";
    private static final String DATACANARY_TOPIC_PREFIX = "vms.canary.hollow.blob.";
    private static final String OVERRIDE_TOPIC_PREFIX = "vms.override.hollow.blob.";
    private static final String OVERRIDE_DATACANARY_TOPIC_PREFIX = "vms.override.canary.hollow.blob.";

    private static String getTopic(String topicPrefix, String vip) {
        return topicPrefix + vip;
    }

    public static String getHollowBlobTopic(String vip) {
        return getTopic(HOLLOWBLOB_TOPIC_PREFIX, vip);
    }

    public static String getDataCanaryTopic(String vip) {
        return getTopic(DATACANARY_TOPIC_PREFIX, vip);
    }

}
