package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoGeneralTitleTypeList extends HollowList<VideoGeneralTitleType> {

    public VideoGeneralTitleTypeList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoGeneralTitleType instantiateElement(int ordinal) {
        return (VideoGeneralTitleType) api().getVideoGeneralTitleType(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTitleTypeListTypeAPI typeApi() {
        return (VideoGeneralTitleTypeListTypeAPI) delegate.getTypeAPI();
    }

}