package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class TimecodeAnnotationDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TimecodeAnnotationDelegate {

    private final Long movieId;
    private final Long packageId;
    private final int timecodeAnnotationsOrdinal;
   private TimecodeAnnotationTypeAPI typeAPI;

    public TimecodeAnnotationDelegateCachedImpl(TimecodeAnnotationTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.timecodeAnnotationsOrdinal = typeAPI.getTimecodeAnnotationsOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getPackageId(int ordinal) {
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public int getTimecodeAnnotationsOrdinal(int ordinal) {
        return timecodeAnnotationsOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TimecodeAnnotationTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TimecodeAnnotationTypeAPI) typeAPI;
    }

}