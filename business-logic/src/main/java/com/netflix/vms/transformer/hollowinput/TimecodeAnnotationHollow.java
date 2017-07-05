package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodeAnnotationHollow extends HollowObject {

    public TimecodeAnnotationHollow(TimecodeAnnotationDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public Long _getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long _getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long _getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public TimecodeAnnotationsListHollow _getTimecodeAnnotations() {
        int refOrdinal = delegate().getTimecodeAnnotationsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTimecodeAnnotationsListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TimecodeAnnotationTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TimecodeAnnotationDelegate delegate() {
        return (TimecodeAnnotationDelegate)delegate;
    }

}