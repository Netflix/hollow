package com.netflix.vms.transformer.util;


public class HollowBlobKeybaseBuilder {

    private final String vip;

    public HollowBlobKeybaseBuilder(String vip) {
        this.vip = vip;
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

    private String buildKeybase(String type) {
        final StringBuilder builder = new StringBuilder("netflix.vms.hollowblob.");
        builder.append(vip);
        builder.append(".all.");
        builder.append(type);

        return builder.toString();
    }
}
