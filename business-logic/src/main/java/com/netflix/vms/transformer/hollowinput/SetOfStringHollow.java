package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class SetOfStringHollow extends HollowSet<StringHollow> {

    public SetOfStringHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StringHollow instantiateElement(int ordinal) {
        return (StringHollow) api().getStringHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SetOfStringTypeAPI typeApi() {
        return (SetOfStringTypeAPI) delegate.getTypeAPI();
    }

}