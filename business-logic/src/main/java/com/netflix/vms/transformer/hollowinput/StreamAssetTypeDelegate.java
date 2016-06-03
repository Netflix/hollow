package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface StreamAssetTypeDelegate extends HollowObjectDelegate {

    public long getAssetTypeId(int ordinal);

    public Long getAssetTypeIdBoxed(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public StreamAssetTypeTypeAPI getTypeAPI();

}