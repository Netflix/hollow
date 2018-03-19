package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class CdnDeploymentHollow extends HollowObject {

    public CdnDeploymentHollow(CdnDeploymentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getOriginServerId() {
        return delegate().getOriginServerId(ordinal);
    }

    public Long _getOriginServerIdBoxed() {
        return delegate().getOriginServerIdBoxed(ordinal);
    }

    public StringHollow _getDirectory() {
        int refOrdinal = delegate().getDirectoryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getOriginServer() {
        int refOrdinal = delegate().getOriginServerOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CdnDeploymentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CdnDeploymentDelegate delegate() {
        return (CdnDeploymentDelegate)delegate;
    }

}