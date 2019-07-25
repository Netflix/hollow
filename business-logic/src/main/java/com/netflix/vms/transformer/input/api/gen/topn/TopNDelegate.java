package com.netflix.vms.transformer.input.api.gen.topn;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TopNDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getAttributesOrdinal(int ordinal);

    public TopNTypeAPI getTypeAPI();

}