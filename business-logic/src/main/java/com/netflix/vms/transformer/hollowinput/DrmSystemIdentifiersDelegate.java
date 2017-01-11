package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface DrmSystemIdentifiersDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getGuidOrdinal(int ordinal);

    public int getNameOrdinal(int ordinal);

    public boolean getHeaderDataAvailable(int ordinal);

    public Boolean getHeaderDataAvailableBoxed(int ordinal);

    public DrmSystemIdentifiersTypeAPI getTypeAPI();

}