package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface CdnsDelegate extends HollowObjectDelegate {

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public int getNameOrdinal(int ordinal);

    public CdnsTypeAPI getTypeAPI();

}