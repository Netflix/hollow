package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeList extends HollowList<VideoGeneralEpisodeType> {

    public VideoGeneralEpisodeTypeList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoGeneralEpisodeType instantiateElement(int ordinal) {
        return (VideoGeneralEpisodeType) api().getVideoGeneralEpisodeType(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralEpisodeTypeListTypeAPI typeApi() {
        return (VideoGeneralEpisodeTypeListTypeAPI) delegate.getTypeAPI();
    }

}