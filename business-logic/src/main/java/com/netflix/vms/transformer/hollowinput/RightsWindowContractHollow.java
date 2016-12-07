package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class RightsWindowContractHollow extends HollowObject {

    public RightsWindowContractHollow(RightsWindowContractDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getContractId() {
        return delegate().getContractId(ordinal);
    }

    public Long _getContractIdBoxed() {
        return delegate().getContractIdBoxed(ordinal);
    }

    public boolean _getDownload() {
        return delegate().getDownload(ordinal);
    }

    public Boolean _getDownloadBoxed() {
        return delegate().getDownloadBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public RightsWindowContractTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsWindowContractDelegate delegate() {
        return (RightsWindowContractDelegate)delegate;
    }

}