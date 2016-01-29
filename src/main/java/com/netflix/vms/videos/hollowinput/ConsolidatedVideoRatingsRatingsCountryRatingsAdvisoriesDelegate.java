package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesDelegate extends HollowObjectDelegate {

    public boolean getOrdered(int ordinal);

    public Boolean getOrderedBoxed(int ordinal);

    public boolean getImageOnly(int ordinal);

    public Boolean getImageOnlyBoxed(int ordinal);

    public int getIdsOrdinal(int ordinal);

    public ConsolidatedVideoRatingsRatingsCountryRatingsAdvisoriesTypeAPI getTypeAPI();

}