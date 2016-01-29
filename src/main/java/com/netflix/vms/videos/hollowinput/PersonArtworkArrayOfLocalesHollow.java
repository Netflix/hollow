package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonArtworkArrayOfLocalesHollow extends HollowList<PersonArtworkLocalesHollow> {

    public PersonArtworkArrayOfLocalesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonArtworkLocalesHollow instantiateElement(int ordinal) {
        return (PersonArtworkLocalesHollow) api().getPersonArtworkLocalesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkArrayOfLocalesTypeAPI typeApi() {
        return (PersonArtworkArrayOfLocalesTypeAPI) delegate.getTypeAPI();
    }

}