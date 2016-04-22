package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class DisallowedAssetBundlesListHollow extends HollowList<DisallowedAssetBundleHollow> {

    public DisallowedAssetBundlesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DisallowedAssetBundleHollow instantiateElement(int ordinal) {
        return (DisallowedAssetBundleHollow) api().getDisallowedAssetBundleHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DisallowedAssetBundlesListTypeAPI typeApi() {
        return (DisallowedAssetBundlesListTypeAPI) delegate.getTypeAPI();
    }

}