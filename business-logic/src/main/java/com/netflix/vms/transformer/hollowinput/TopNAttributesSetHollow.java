package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class TopNAttributesSetHollow extends HollowSet<TopNAttributeHollow> {

    public TopNAttributesSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public TopNAttributeHollow instantiateElement(int ordinal) {
        return (TopNAttributeHollow) api().getTopNAttributeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TopNAttributesSetTypeAPI typeApi() {
        return (TopNAttributesSetTypeAPI) delegate.getTypeAPI();
    }

}