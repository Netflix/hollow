package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class VideoTypeDescriptorSet extends HollowSet<VideoTypeDescriptor> {

    public VideoTypeDescriptorSet(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public VideoTypeDescriptor instantiateElement(int ordinal) {
        return (VideoTypeDescriptor) api().getVideoTypeDescriptor(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VideoTypeAPI api() {
        return typeApi().getAPI();
    }

    public VideoTypeDescriptorSetTypeAPI typeApi() {
        return (VideoTypeDescriptorSetTypeAPI) delegate.getTypeAPI();
    }

}