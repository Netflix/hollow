package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfPackageTags extends HollowList<HString> {

    public ListOfPackageTags(HollowListDelegate delegate, int ordinal) {
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

    public PackageDealCountryAPI api() {
        return typeApi().getAPI();
    }

    public ListOfPackageTagsTypeAPI typeApi() {
        return (ListOfPackageTagsTypeAPI) delegate.getTypeAPI();
    }

}