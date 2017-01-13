package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfRightsWindowHollow extends HollowList<RightsWindowHollow> {

    public ListOfRightsWindowHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public RightsWindowHollow instantiateElement(int ordinal) {
        return (RightsWindowHollow) api().getRightsWindowHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfRightsWindowTypeAPI typeApi() {
        return (ListOfRightsWindowTypeAPI) delegate.getTypeAPI();
    }

}