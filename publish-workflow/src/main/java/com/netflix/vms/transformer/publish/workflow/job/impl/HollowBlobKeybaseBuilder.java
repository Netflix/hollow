package com.netflix.vms.transformer.publish.workflow.job.impl;

public class HollowBlobKeybaseBuilder {

    private final String vip;
    private final int shardNumber;

    public HollowBlobKeybaseBuilder(String vip) {
        this(vip, -1);
    }

    public HollowBlobKeybaseBuilder(String vip, int shardNumber) {
        this.vip = vip;
        this.shardNumber = shardNumber;
    }

    public String getReverseDeltaKeybase() {
        return buildKeybase("reversedelta");
    }

    public String getDeltaKeybase() {
        return buildKeybase("delta");
    }

    public String getSnapshotKeybase() {
        return buildKeybase("snapshot");
    }

    private String buildKeybase(final String type) {
        final StringBuilder builder = new StringBuilder("netflix.vms.hollowblob.");
        return buildKeybase(type, builder);
    }

    protected String buildKeybase(final String type, final StringBuilder builder) {
        builder.append(vip);
        if (!"stateengine".equals(type)) {
            builder.append(".all");
        }
        builder.append(".");
        builder.append(type);
        if (shardNumber >= 0) {
            builder.append(".");
            builder.append(shardNumber);
            builder.append(".shard");
        }
        return builder.toString();
    }
}
