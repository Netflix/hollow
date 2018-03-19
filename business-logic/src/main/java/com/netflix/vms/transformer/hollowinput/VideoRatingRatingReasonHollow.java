package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class VideoRatingRatingReasonHollow extends HollowObject {

    public VideoRatingRatingReasonHollow(VideoRatingRatingReasonDelegate delegate, int ordinal) {
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

    public VideoRatingRatingReasonArrayOfIdsHollow _getIds() {
        int refOrdinal = delegate().getIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoRatingRatingReasonArrayOfIdsHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingRatingReasonTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingRatingReasonDelegate delegate() {
        return (VideoRatingRatingReasonDelegate)delegate;
    }

}