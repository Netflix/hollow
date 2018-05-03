package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfStreamBoxInfoHollow extends HollowSet<StreamBoxInfoHollow> {

    public SetOfStreamBoxInfoHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public StreamBoxInfoHollow instantiateElement(int ordinal) {
        return (StreamBoxInfoHollow) api().getStreamBoxInfoHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public SetOfStreamBoxInfoTypeAPI typeApi() {
        return (SetOfStreamBoxInfoTypeAPI) delegate.getTypeAPI();
    }

}