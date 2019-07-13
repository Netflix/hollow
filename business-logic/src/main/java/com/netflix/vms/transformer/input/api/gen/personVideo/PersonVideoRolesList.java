package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PersonVideoRolesList extends HollowList<PersonVideoRole> {

    public PersonVideoRolesList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PersonVideoRole instantiateElement(int ordinal) {
        return (PersonVideoRole) api().getPersonVideoRole(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public PersonVideoAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoRolesListTypeAPI typeApi() {
        return (PersonVideoRolesListTypeAPI) delegate.getTypeAPI();
    }

}