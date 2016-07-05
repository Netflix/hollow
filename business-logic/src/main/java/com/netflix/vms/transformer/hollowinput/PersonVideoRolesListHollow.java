package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PersonVideoRolesListHollow extends HollowList<PersonVideoRoleHollow> {

    public PersonVideoRolesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonVideoRoleHollow instantiateElement(int ordinal) {
        return (PersonVideoRoleHollow) api().getPersonVideoRoleHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoRolesListTypeAPI typeApi() {
        return (PersonVideoRolesListTypeAPI) delegate.getTypeAPI();
    }

}