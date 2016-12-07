package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoRatingAdvisoriesHollow extends HollowObject {

    public VideoRatingAdvisoriesHollow(VideoRatingAdvisoriesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getOrdered() {
        return delegate().getOrdered(ordinal);
    }

    public Boolean _getOrderedBoxed() {
        return delegate().getOrderedBoxed(ordinal);
    }

    public boolean _getImageOnly() {
        return delegate().getImageOnly(ordinal);
    }

    public Boolean _getImageOnlyBoxed() {
        return delegate().getImageOnlyBoxed(ordinal);
    }

    public VideoRatingAdvisoryIdListHollow _getIds() {
        int refOrdinal = delegate().getIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingAdvisoryIdListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingAdvisoriesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingAdvisoriesDelegate delegate() {
        return (VideoRatingAdvisoriesDelegate)delegate;
    }

}