package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class DeployablePackagesArrayOfCountryCodesHollow extends HollowList<DeployablePackagesCountryCodesHollow> {

    public DeployablePackagesArrayOfCountryCodesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DeployablePackagesCountryCodesHollow instantiateElement(int ordinal) {
        return (DeployablePackagesCountryCodesHollow) api().getDeployablePackagesCountryCodesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public DeployablePackagesArrayOfCountryCodesTypeAPI typeApi() {
        return (DeployablePackagesArrayOfCountryCodesTypeAPI) delegate.getTypeAPI();
    }

}