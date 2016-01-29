package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RatingsRatingCodeDelegate extends HollowObjectDelegate {

    public int getTranslatedTextsOrdinal(int ordinal);

    public RatingsRatingCodeTypeAPI getTypeAPI();

}