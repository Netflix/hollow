package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedVideoRatingsRatingsDelegate extends HollowObjectDelegate {

    public int getCountryRatingsOrdinal(int ordinal);

    public int getCountryListOrdinal(int ordinal);

    public ConsolidatedVideoRatingsRatingsTypeAPI getTypeAPI();

}