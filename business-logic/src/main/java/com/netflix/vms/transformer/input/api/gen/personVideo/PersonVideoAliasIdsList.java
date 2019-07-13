package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PersonVideoAliasIdsList extends HollowList<PersonVideoAliasId> {

    public PersonVideoAliasIdsList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PersonVideoAliasId instantiateElement(int ordinal) {
        return (PersonVideoAliasId) api().getPersonVideoAliasId(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public PersonVideoAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoAliasIdsListTypeAPI typeApi() {
        return (PersonVideoAliasIdsListTypeAPI) delegate.getTypeAPI();
    }

}