package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonArtworkLocaleListHollow extends HollowList<PersonArtworkLocaleHollow> {

    public PersonArtworkLocaleListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonArtworkLocaleHollow instantiateElement(int ordinal) {
        return (PersonArtworkLocaleHollow) api().getPersonArtworkLocaleHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkLocaleListTypeAPI typeApi() {
        return (PersonArtworkLocaleListTypeAPI) delegate.getTypeAPI();
    }

}