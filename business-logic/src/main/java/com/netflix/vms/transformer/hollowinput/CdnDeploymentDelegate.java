package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CdnDeploymentDelegate extends HollowObjectDelegate {

    public long getOriginServerId(int ordinal);

    public Long getOriginServerIdBoxed(int ordinal);

    public int getDirectoryOrdinal(int ordinal);

    public int getOriginServerOrdinal(int ordinal);

    public CdnDeploymentTypeAPI getTypeAPI();

}