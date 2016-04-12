package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class StreamDeploymentLabelSetHollow extends HollowSet<StreamDeploymentLabelHollow> {

    public StreamDeploymentLabelSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamDeploymentLabelHollow instantiateElement(int ordinal) {
        return (StreamDeploymentLabelHollow) api().getStreamDeploymentLabelHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public StreamDeploymentLabelSetTypeAPI typeApi() {
        return (StreamDeploymentLabelSetTypeAPI) delegate.getTypeAPI();
    }

}