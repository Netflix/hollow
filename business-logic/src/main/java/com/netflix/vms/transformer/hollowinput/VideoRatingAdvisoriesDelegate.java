package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoRatingAdvisoriesDelegate extends HollowObjectDelegate {

    public boolean getOrdered(int ordinal);

    public Boolean getOrderedBoxed(int ordinal);

    public boolean getImageOnly(int ordinal);

    public Boolean getImageOnlyBoxed(int ordinal);

    public int getIdsOrdinal(int ordinal);

    public VideoRatingAdvisoriesTypeAPI getTypeAPI();

}