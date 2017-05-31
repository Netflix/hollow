package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfRightsAssetHollow extends HollowSet<RightsAssetHollow> {

    public SetOfRightsAssetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsAssetHollow instantiateElement(int ordinal) {
        return (RightsAssetHollow) api().getRightsAssetHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SetOfRightsAssetTypeAPI typeApi() {
        return (SetOfRightsAssetTypeAPI) delegate.getTypeAPI();
    }

}