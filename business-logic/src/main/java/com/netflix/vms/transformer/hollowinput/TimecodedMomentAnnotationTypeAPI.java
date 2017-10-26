package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationTypeAPI extends HollowObjectTypeAPI {

    private final TimecodedMomentAnnotationDelegateLookupImpl delegateLookupImpl;

    public TimecodedMomentAnnotationTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "type",
            "startMillis",
            "endMillis"
        });
        this.delegateLookupImpl = new TimecodedMomentAnnotationDelegateLookupImpl(this);
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TimecodedMomentAnnotation", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getStartMillis(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("TimecodedMomentAnnotation", ordinal, "startMillis");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getStartMillisBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("TimecodedMomentAnnotation", ordinal, "startMillis");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getEndMillis(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("TimecodedMomentAnnotation", ordinal, "endMillis");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getEndMillisBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("TimecodedMomentAnnotation", ordinal, "endMillis");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public TimecodedMomentAnnotationDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}