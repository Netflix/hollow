package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class TimecodeAnnotationsListHollow extends HollowList<TimecodedMomentAnnotationHollow> {

    public TimecodeAnnotationsListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public TimecodedMomentAnnotationHollow instantiateElement(int ordinal) {
        return (TimecodedMomentAnnotationHollow) api().getTimecodedMomentAnnotationHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TimecodeAnnotationsListTypeAPI typeApi() {
        return (TimecodeAnnotationsListTypeAPI) delegate.getTypeAPI();
    }

}