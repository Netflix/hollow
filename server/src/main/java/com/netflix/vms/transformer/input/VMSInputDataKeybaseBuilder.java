package com.netflix.vms.transformer.input;

public class VMSInputDataKeybaseBuilder {

    private final String converterVip;

    public VMSInputDataKeybaseBuilder(String converterVip) {
        this.converterVip = converterVip;
    }

    public String getSnapshotKeybase() {
        return "vms.hollowinput.blob." + converterVip + ".snapshot";
    }

    public String getDeltaKeybase() {
        return "vms.hollowinput.blob." + converterVip + ".delta";
    }

}
