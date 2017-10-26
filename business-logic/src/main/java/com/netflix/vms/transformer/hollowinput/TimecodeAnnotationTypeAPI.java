package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TimecodeAnnotationTypeAPI extends HollowObjectTypeAPI {

    private final TimecodeAnnotationDelegateLookupImpl delegateLookupImpl;

    public TimecodeAnnotationTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "packageId",
            "timecodeAnnotations"
        });
        this.delegateLookupImpl = new TimecodeAnnotationDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("TimecodeAnnotation", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("TimecodeAnnotation", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPackageId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("TimecodeAnnotation", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("TimecodeAnnotation", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTimecodeAnnotationsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("TimecodeAnnotation", ordinal, "timecodeAnnotations");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TimecodeAnnotationsListTypeAPI getTimecodeAnnotationsTypeAPI() {
        return getAPI().getTimecodeAnnotationsListTypeAPI();
    }

    public TimecodeAnnotationDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}