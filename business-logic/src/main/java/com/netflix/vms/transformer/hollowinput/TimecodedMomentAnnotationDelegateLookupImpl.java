package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationDelegateLookupImpl extends HollowObjectAbstractDelegate implements TimecodedMomentAnnotationDelegate {

    private final TimecodedMomentAnnotationTypeAPI typeAPI;

    public TimecodedMomentAnnotationDelegateLookupImpl(TimecodedMomentAnnotationTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public long getStartMillis(int ordinal) {
        return typeAPI.getStartMillis(ordinal);
    }

    public Long getStartMillisBoxed(int ordinal) {
        return typeAPI.getStartMillisBoxed(ordinal);
    }

    public long getEndMillis(int ordinal) {
        return typeAPI.getEndMillis(ordinal);
    }

    public Long getEndMillisBoxed(int ordinal) {
        return typeAPI.getEndMillisBoxed(ordinal);
    }

    public TimecodedMomentAnnotationTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}