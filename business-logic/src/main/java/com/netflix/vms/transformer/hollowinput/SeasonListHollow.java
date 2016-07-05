package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class SeasonListHollow extends HollowList<SeasonHollow> {

    public SeasonListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SeasonHollow instantiateElement(int ordinal) {
        return (SeasonHollow) api().getSeasonHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SeasonListTypeAPI typeApi() {
        return (SeasonListTypeAPI) delegate.getTypeAPI();
    }

}