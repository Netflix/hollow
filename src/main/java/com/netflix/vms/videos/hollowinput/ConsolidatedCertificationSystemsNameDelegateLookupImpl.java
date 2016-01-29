package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsNameDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedCertificationSystemsNameDelegate {

    private final ConsolidatedCertificationSystemsNameTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsNameDelegateLookupImpl(ConsolidatedCertificationSystemsNameTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        return typeAPI.getTranslatedTextsOrdinal(ordinal);
    }

    public ConsolidatedCertificationSystemsNameTypeAPI getTypeAPI() {
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