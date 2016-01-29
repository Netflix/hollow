package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonArtworkLocalesArrayOfTerritoryCodesHollow extends HollowList<PersonArtworkLocalesTerritoryCodesHollow> {

    public PersonArtworkLocalesArrayOfTerritoryCodesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonArtworkLocalesTerritoryCodesHollow instantiateElement(int ordinal) {
        return (PersonArtworkLocalesTerritoryCodesHollow) api().getPersonArtworkLocalesTerritoryCodesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI typeApi() {
        return (PersonArtworkLocalesArrayOfTerritoryCodesTypeAPI) delegate.getTypeAPI();
    }

}