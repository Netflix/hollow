package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SupplementalsList extends HollowList<IndividualSupplemental> {

    public SupplementalsList(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public IndividualSupplemental instantiateElement(int ordinal) {
        return (IndividualSupplemental) api().getIndividualSupplemental(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public SupplementalAPI api() {
        return typeApi().getAPI();
    }

    public SupplementalsListTypeAPI typeApi() {
        return (SupplementalsListTypeAPI) delegate.getTypeAPI();
    }

}