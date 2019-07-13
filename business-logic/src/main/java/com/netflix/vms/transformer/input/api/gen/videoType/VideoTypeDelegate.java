package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoTypeDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getCountryInfosOrdinal(int ordinal);

    public VideoTypeTypeAPI getTypeAPI();

}