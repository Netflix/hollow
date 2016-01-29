package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoArtWorkLocalesDelegate extends HollowObjectDelegate {

    public int getTerritoryCodesOrdinal(int ordinal);

    public int getBcp47CodeOrdinal(int ordinal);

    public long getEffectiveDate(int ordinal);

    public Long getEffectiveDateBoxed(int ordinal);

    public VideoArtWorkLocalesTypeAPI getTypeAPI();

}