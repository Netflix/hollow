package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SeasonListHollow extends HollowList<SeasonHollow> {

    public SeasonListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
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