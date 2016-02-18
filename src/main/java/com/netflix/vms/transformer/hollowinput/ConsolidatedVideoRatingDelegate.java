package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedVideoRatingDelegate extends HollowObjectDelegate {

    public int getCountryRatingsOrdinal(int ordinal);

    public int getCountryListOrdinal(int ordinal);

    public ConsolidatedVideoRatingTypeAPI getTypeAPI();

}