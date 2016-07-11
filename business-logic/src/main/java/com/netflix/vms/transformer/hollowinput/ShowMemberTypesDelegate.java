package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ShowMemberTypesDelegate extends HollowObjectDelegate {

    public long getShowMemberTypeId(int ordinal);

    public Long getShowMemberTypeIdBoxed(int ordinal);

    public int getDisplayNameOrdinal(int ordinal);

    public ShowMemberTypesTypeAPI getTypeAPI();

}