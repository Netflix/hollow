package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface FestivalsDelegate extends HollowObjectDelegate {

    public long getFestivalId(int ordinal);

    public Long getFestivalIdBoxed(int ordinal);

    public int getCopyrightOrdinal(int ordinal);

    public int getFestivalNameOrdinal(int ordinal);

    public int getDescriptionOrdinal(int ordinal);

    public int getShortNameOrdinal(int ordinal);

    public int getSingularNameOrdinal(int ordinal);

    public FestivalsTypeAPI getTypeAPI();

}