package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ShowCountryLabelDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getShowMemberTypesOrdinal(int ordinal);

    public ShowCountryLabelTypeAPI getTypeAPI();

}