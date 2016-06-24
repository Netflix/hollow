package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class CdnDeploymentSetHollow extends HollowSet<CdnDeploymentHollow> {

    public CdnDeploymentSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public CdnDeploymentHollow instantiateElement(int ordinal) {
        return (CdnDeploymentHollow) api().getCdnDeploymentHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CdnDeploymentSetTypeAPI typeApi() {
        return (CdnDeploymentSetTypeAPI) delegate.getTypeAPI();
    }

}