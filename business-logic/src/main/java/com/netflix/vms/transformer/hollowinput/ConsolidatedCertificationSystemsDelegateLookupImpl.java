package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedCertificationSystemsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedCertificationSystemsDelegate {

    private final ConsolidatedCertificationSystemsTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsDelegateLookupImpl(ConsolidatedCertificationSystemsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCertificationSystemId(int ordinal) {
        return typeAPI.getCertificationSystemId(ordinal);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return typeAPI.getCertificationSystemIdBoxed(ordinal);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getRatingOrdinal(int ordinal) {
        return typeAPI.getRatingOrdinal(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public int getOfficialURLOrdinal(int ordinal) {
        return typeAPI.getOfficialURLOrdinal(ordinal);
    }

    public ConsolidatedCertificationSystemsTypeAPI getTypeAPI() {
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