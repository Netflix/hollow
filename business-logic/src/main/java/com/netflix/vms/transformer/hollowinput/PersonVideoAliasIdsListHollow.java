package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PersonVideoAliasIdsListHollow extends HollowList<PersonVideoAliasIdHollow> {

    public PersonVideoAliasIdsListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PersonVideoAliasIdHollow instantiateElement(int ordinal) {
        return (PersonVideoAliasIdHollow) api().getPersonVideoAliasIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoAliasIdsListTypeAPI typeApi() {
        return (PersonVideoAliasIdsListTypeAPI) delegate.getTypeAPI();
    }

}