package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationHollow extends HollowObject {

    public TimecodedMomentAnnotationHollow(TimecodedMomentAnnotationDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getStartMillis() {
        return delegate().getStartMillis(ordinal);
    }

    public Long _getStartMillisBoxed() {
        return delegate().getStartMillisBoxed(ordinal);
    }

    public long _getEndMillis() {
        return delegate().getEndMillis(ordinal);
    }

    public Long _getEndMillisBoxed() {
        return delegate().getEndMillisBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TimecodedMomentAnnotationTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TimecodedMomentAnnotationDelegate delegate() {
        return (TimecodedMomentAnnotationDelegate)delegate;
    }

}