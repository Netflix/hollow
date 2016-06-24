package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeListHollow extends HollowList<VideoGeneralEpisodeTypeHollow> {

    public VideoGeneralEpisodeTypeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoGeneralEpisodeTypeHollow instantiateElement(int ordinal) {
        return (VideoGeneralEpisodeTypeHollow) api().getVideoGeneralEpisodeTypeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralEpisodeTypeListTypeAPI typeApi() {
        return (VideoGeneralEpisodeTypeListTypeAPI) delegate.getTypeAPI();
    }

}