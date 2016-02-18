package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonArtworkDerivativeListHollow extends HollowList<PersonArtworkDerivativeHollow> {

    public PersonArtworkDerivativeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonArtworkDerivativeHollow instantiateElement(int ordinal) {
        return (PersonArtworkDerivativeHollow) api().getPersonArtworkDerivativeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkDerivativeListTypeAPI typeApi() {
        return (PersonArtworkDerivativeListTypeAPI) delegate.getTypeAPI();
    }

}