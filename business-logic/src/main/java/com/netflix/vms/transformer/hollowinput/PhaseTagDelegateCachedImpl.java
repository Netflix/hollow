package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PhaseTagDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PhaseTagDelegate {

    private final int phaseTagOrdinal;
    private final int scheduleIdOrdinal;
   private PhaseTagTypeAPI typeAPI;

    public PhaseTagDelegateCachedImpl(PhaseTagTypeAPI typeAPI, int ordinal) {
        this.phaseTagOrdinal = typeAPI.getPhaseTagOrdinal(ordinal);
        this.scheduleIdOrdinal = typeAPI.getScheduleIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getPhaseTagOrdinal(int ordinal) {
        return phaseTagOrdinal;
    }

    public int getScheduleIdOrdinal(int ordinal) {
        return scheduleIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PhaseTagTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PhaseTagTypeAPI) typeAPI;
    }

}