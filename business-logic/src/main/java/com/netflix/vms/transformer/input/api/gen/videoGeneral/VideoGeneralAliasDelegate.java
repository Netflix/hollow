package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoGeneralAliasDelegate extends HollowObjectDelegate {

    public String getValue(int ordinal);

    public boolean isValueEqual(int ordinal, String testValue);

    public int getValueOrdinal(int ordinal);

    public VideoGeneralAliasTypeAPI getTypeAPI();

}