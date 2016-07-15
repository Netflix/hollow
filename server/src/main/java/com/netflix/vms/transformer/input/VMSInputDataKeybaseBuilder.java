package com.netflix.vms.transformer.input;

import com.netflix.vms.transformer.common.KeybaseBuilder;

public class VMSInputDataKeybaseBuilder implements KeybaseBuilder {

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
