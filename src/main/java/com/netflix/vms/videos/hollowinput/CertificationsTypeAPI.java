package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CertificationsTypeAPI extends HollowObjectTypeAPI {

    private final CertificationsDelegateLookupImpl delegateLookupImpl;

    CertificationsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "name",
            "description",
            "certificationTypeId"
        });
        this.delegateLookupImpl = new CertificationsDelegateLookupImpl(this);
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Certifications", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public CertificationsNameTypeAPI getNameTypeAPI() {
        return getAPI().getCertificationsNameTypeAPI();
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Certifications", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public CertificationsDescriptionTypeAPI getDescriptionTypeAPI() {
        return getAPI().getCertificationsDescriptionTypeAPI();
    }

    public long getCertificationTypeId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("Certifications", ordinal, "certificationTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("Certifications", ordinal, "certificationTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CertificationsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}