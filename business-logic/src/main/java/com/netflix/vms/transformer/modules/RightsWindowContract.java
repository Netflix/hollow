package com.netflix.vms.transformer.modules;

import com.netflix.vms.transformer.hollowinput.RightsContractHollow;

public class RightsWindowContract {
    public final long contractId;
    public final RightsContractHollow contract;
    public final boolean isDownloadable;

    public RightsWindowContract(long contractId, RightsContractHollow contract, boolean isDownloadable) {
        this.contractId = contractId;
        this.contract = contract;
        this.isDownloadable = isDownloadable;
    }
}
