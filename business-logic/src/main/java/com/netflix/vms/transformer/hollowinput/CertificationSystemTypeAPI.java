package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CertificationSystemTypeAPI extends HollowObjectTypeAPI {

    private final CertificationSystemDelegateLookupImpl delegateLookupImpl;

    CertificationSystemTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "certificationSystemId",
            "countryCode",
            "rating",
            "officialURL"
        });
        this.delegateLookupImpl = new CertificationSystemDelegateLookupImpl(this);
    }

    public long getCertificationSystemId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CertificationSystem", ordinal, "certificationSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CertificationSystem", ordinal, "certificationSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CertificationSystem", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRatingOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CertificationSystem", ordinal, "rating");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public CertificationSystemRatingListTypeAPI getRatingTypeAPI() {
        return getAPI().getCertificationSystemRatingListTypeAPI();
    }

    public int getOfficialURLOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("CertificationSystem", ordinal, "officialURL");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getOfficialURLTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CertificationSystemDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}