package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoDateDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getWindowOrdinal(int ordinal);

    public VideoDateTypeAPI getTypeAPI();

}