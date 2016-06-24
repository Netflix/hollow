package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CertificationsTypeAPI extends HollowObjectTypeAPI {

    private final CertificationsDelegateLookupImpl delegateLookupImpl;

    CertificationsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "certificationTypeId",
            "name",
            "description"
        });
        this.delegateLookupImpl = new CertificationsDelegateLookupImpl(this);
    }

    public long getCertificationTypeId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Certifications", ordinal, "certificationTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCertificationTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Certifications", ordinal, "certificationTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Certifications", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Certifications", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getDescriptionTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public CertificationsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}