package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TrailerThemeListHollow extends HollowList<TrailerThemeHollow> {

    public TrailerThemeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TrailerThemeHollow instantiateElement(int ordinal) {
        return (TrailerThemeHollow) api().getTrailerThemeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerThemeListTypeAPI typeApi() {
        return (TrailerThemeListTypeAPI) delegate.getTypeAPI();
    }

}