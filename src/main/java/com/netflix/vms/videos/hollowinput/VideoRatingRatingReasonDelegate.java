package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRatingRatingReasonDelegate extends HollowObjectDelegate {

    public boolean getOrdered(int ordinal);

    public Boolean getOrderedBoxed(int ordinal);

    public boolean getImageOnly(int ordinal);

    public Boolean getImageOnlyBoxed(int ordinal);

    public int getIdsOrdinal(int ordinal);

    public VideoRatingRatingReasonTypeAPI getTypeAPI();

}