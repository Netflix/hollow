package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class CertificationSystemDelegateLookupImpl extends HollowObjectAbstractDelegate implements CertificationSystemDelegate {

    private final CertificationSystemTypeAPI typeAPI;

    public CertificationSystemDelegateLookupImpl(CertificationSystemTypeAPI typeAPI) {
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

    public int getOfficialURLOrdinal(int ordinal) {
        return typeAPI.getOfficialURLOrdinal(ordinal);
    }

    public CertificationSystemTypeAPI getTypeAPI() {
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