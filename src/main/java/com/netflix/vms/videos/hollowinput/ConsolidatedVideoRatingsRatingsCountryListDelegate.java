package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedVideoRatingsRatingsCountryListDelegate extends HollowObjectDelegate {

    public int getValueOrdinal(int ordinal);

    public ConsolidatedVideoRatingsRatingsCountryListTypeAPI getTypeAPI();

}