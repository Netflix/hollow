package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ShowMemberTypeList extends HollowList<ShowMemberType> {

    public ShowMemberTypeList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ShowMemberType instantiateElement(int ordinal) {
        return (ShowMemberType) api().getShowMemberType(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypeListTypeAPI typeApi() {
        return (ShowMemberTypeListTypeAPI) delegate.getTypeAPI();
    }

}