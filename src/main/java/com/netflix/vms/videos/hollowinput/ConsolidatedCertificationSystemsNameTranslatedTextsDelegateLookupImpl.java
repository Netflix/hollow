package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsNameTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedCertificationSystemsNameTranslatedTextsDelegate {

    private final ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsNameTranslatedTextsDelegateLookupImpl(ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public ConsolidatedCertificationSystemsNameTranslatedTextsTypeAPI getTypeAPI() {
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