package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoGeneralAliasList extends HollowList<VideoGeneralAlias> {

    public VideoGeneralAliasList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoGeneralAlias instantiateElement(int ordinal) {
        return (VideoGeneralAlias) api().getVideoGeneralAlias(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralAliasListTypeAPI typeApi() {
        return (VideoGeneralAliasListTypeAPI) delegate.getTypeAPI();
    }

}