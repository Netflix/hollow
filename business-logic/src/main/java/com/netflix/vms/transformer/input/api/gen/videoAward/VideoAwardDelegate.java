package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoAwardDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getAwardOrdinal(int ordinal);

    public VideoAwardTypeAPI getTypeAPI();

}