package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class VideoRatingRatingReasonIdsHollow extends HollowObject {

    public VideoRatingRatingReasonIdsHollow(VideoRatingRatingReasonIdsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getValue() {
        return delegate().getValue(ordinal);
    }

    public Long _getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoRatingRatingReasonIdsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoRatingRatingReasonIdsDelegate delegate() {
        return (VideoRatingRatingReasonIdsDelegate)delegate;
    }

}