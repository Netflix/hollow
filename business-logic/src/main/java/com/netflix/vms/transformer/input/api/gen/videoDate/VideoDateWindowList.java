package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoDateWindowList extends HollowList<VideoDateWindow> {

    public VideoDateWindowList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoDateWindow instantiateElement(int ordinal) {
        return (VideoDateWindow) api().getVideoDateWindow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoDateAPI api() {
        return typeApi().getAPI();
    }

    public VideoDateWindowListTypeAPI typeApi() {
        return (VideoDateWindowListTypeAPI) delegate.getTypeAPI();
    }

}