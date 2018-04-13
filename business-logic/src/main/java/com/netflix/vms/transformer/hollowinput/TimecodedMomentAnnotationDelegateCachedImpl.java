package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TimecodedMomentAnnotationDelegate {

    private final int typeOrdinal;
    private final Long startMillis;
    private final Long endMillis;
    private TimecodedMomentAnnotationTypeAPI typeAPI;

    public TimecodedMomentAnnotationDelegateCachedImpl(TimecodedMomentAnnotationTypeAPI typeAPI, int ordinal) {
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.startMillis = typeAPI.getStartMillisBoxed(ordinal);
        this.endMillis = typeAPI.getEndMillisBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    public long getStartMillis(int ordinal) {
        if(startMillis == null)
            return Long.MIN_VALUE;
        return startMillis.longValue();
    }

    public Long getStartMillisBoxed(int ordinal) {
        return startMillis;
    }

    public long getEndMillis(int ordinal) {
        if(endMillis == null)
            return Long.MIN_VALUE;
        return endMillis.longValue();
    }

    public Long getEndMillisBoxed(int ordinal) {
        return endMillis;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TimecodedMomentAnnotationTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TimecodedMomentAnnotationTypeAPI) typeAPI;
    }

}