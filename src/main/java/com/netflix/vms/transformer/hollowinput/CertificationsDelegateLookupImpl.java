package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationsDelegateLookupImpl extends HollowObjectAbstractDelegate implements CertificationsDelegate {

    private final CertificationsTypeAPI typeAPI;

    public CertificationsDelegateLookupImpl(CertificationsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getCertificationTypeId(int ordinal) {
        return typeAPI.getCertificationTypeId(ordinal);
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        return typeAPI.getCertificationTypeIdBoxed(ordinal);
    }

    public int getNameOrdinal(int ordinal) {
        return typeAPI.getNameOrdinal(ordinal);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public CertificationsTypeAPI getTypeAPI() {
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