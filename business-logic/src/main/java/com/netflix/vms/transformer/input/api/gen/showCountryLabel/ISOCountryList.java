package com.netflix.vms.transformer.input.api.gen.showCountryLabel;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ISOCountryList extends HollowList<ISOCountry> {

    public ISOCountryList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ISOCountry instantiateElement(int ordinal) {
        return (ISOCountry) api().getISOCountry(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public ShowCountryLabelAPI api() {
        return typeApi().getAPI();
    }

    public ISOCountryListTypeAPI typeApi() {
        return (ISOCountryListTypeAPI) delegate.getTypeAPI();
    }

}