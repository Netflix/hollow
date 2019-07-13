package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ListOfDerivativeTag extends HollowList<DerivativeTag> {

    public ListOfDerivativeTag(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DerivativeTag instantiateElement(int ordinal) {
        return (DerivativeTag) api().getDerivativeTag(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public ListOfDerivativeTagTypeAPI typeApi() {
        return (ListOfDerivativeTagTypeAPI) delegate.getTypeAPI();
    }

}