package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfString extends HollowList<HString> {

    public ListOfString(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public HString instantiateElement(int ordinal) {
        return (HString) api().getHString(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public ListOfStringTypeAPI typeApi() {
        return (ListOfStringTypeAPI) delegate.getTypeAPI();
    }

}