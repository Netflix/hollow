package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class StreamDeploymentLabelSetHollow extends HollowSet<StreamDeploymentLabelHollow> {

    public StreamDeploymentLabelSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public StreamDeploymentLabelHollow instantiateElement(int ordinal) {
        return (StreamDeploymentLabelHollow) api().getStreamDeploymentLabelHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDeploymentLabelSetTypeAPI typeApi() {
        return (StreamDeploymentLabelSetTypeAPI) delegate.getTypeAPI();
    }

}