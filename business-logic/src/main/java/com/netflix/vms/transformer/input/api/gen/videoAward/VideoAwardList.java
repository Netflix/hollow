package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoAwardList extends HollowList<VideoAwardMapping> {

    public VideoAwardList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoAwardMapping instantiateElement(int ordinal) {
        return (VideoAwardMapping) api().getVideoAwardMapping(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoAwardAPI api() {
        return typeApi().getAPI();
    }

    public VideoAwardListTypeAPI typeApi() {
        return (VideoAwardListTypeAPI) delegate.getTypeAPI();
    }

}