package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfReleaseDatesHollow extends HollowList<ReleaseDateHollow> {

    public ListOfReleaseDatesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ReleaseDateHollow instantiateElement(int ordinal) {
        return (ReleaseDateHollow) api().getReleaseDateHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ListOfReleaseDatesTypeAPI typeApi() {
        return (ListOfReleaseDatesTypeAPI) delegate.getTypeAPI();
    }

}