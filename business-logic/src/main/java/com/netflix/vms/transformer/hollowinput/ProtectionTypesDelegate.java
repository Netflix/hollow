package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ProtectionTypesDelegate extends HollowObjectDelegate {

    public int getNameOrdinal(int ordinal);

    public long getId(int ordinal);

    public Long getIdBoxed(int ordinal);

    public ProtectionTypesTypeAPI getTypeAPI();

}