package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonArtworkArrayOfDerivativesHollow extends HollowList<PersonArtworkDerivativesHollow> {

    public PersonArtworkArrayOfDerivativesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonArtworkDerivativesHollow instantiateElement(int ordinal) {
        return (PersonArtworkDerivativesHollow) api().getPersonArtworkDerivativesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkArrayOfDerivativesTypeAPI typeApi() {
        return (PersonArtworkArrayOfDerivativesTypeAPI) delegate.getTypeAPI();
    }

}