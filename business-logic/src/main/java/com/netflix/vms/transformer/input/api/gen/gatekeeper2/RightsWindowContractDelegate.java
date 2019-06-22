package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsWindowContractDelegate extends HollowObjectDelegate {

    public int getAssetsOrdinal(int ordinal);

    public long getDealId(int ordinal);

    public Long getDealIdBoxed(int ordinal);

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public int getPackagesOrdinal(int ordinal);

    public boolean getDownload(int ordinal);

    public Boolean getDownloadBoxed(int ordinal);

    public RightsWindowContractTypeAPI getTypeAPI();

}