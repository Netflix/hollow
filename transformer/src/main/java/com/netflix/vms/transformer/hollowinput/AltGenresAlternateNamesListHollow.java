package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class AltGenresAlternateNamesListHollow extends HollowList<AltGenresAlternateNamesHollow> {

    public AltGenresAlternateNamesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AltGenresAlternateNamesHollow instantiateElement(int ordinal) {
        return (AltGenresAlternateNamesHollow) api().getAltGenresAlternateNamesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AltGenresAlternateNamesListTypeAPI typeApi() {
        return (AltGenresAlternateNamesListTypeAPI) delegate.getTypeAPI();
    }

}