package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoTypeMediaList extends HollowList<VideoTypeMedia> {

    public VideoTypeMediaList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoTypeMedia instantiateElement(int ordinal) {
        return (VideoTypeMedia) api().getVideoTypeMedia(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoTypeAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeMediaListTypeAPI typeApi() {
        return (VideoTypeMediaListTypeAPI) delegate.getTypeAPI();
    }

}