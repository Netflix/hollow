package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface StreamBoxInfoDelegate extends HollowObjectDelegate {

    public int getBoxOffset(int ordinal);

    public Integer getBoxOffsetBoxed(int ordinal);

    public int getBoxSize(int ordinal);

    public Integer getBoxSizeBoxed(int ordinal);

    public int getKeyOrdinal(int ordinal);

    public StreamBoxInfoTypeAPI getTypeAPI();

}