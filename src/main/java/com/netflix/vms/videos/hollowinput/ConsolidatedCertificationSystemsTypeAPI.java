package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ConsolidatedCertificationSystemsTypeAPI extends HollowObjectTypeAPI {

    private final ConsolidatedCertificationSystemsDelegateLookupImpl delegateLookupImpl;

    ConsolidatedCertificationSystemsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCode",
            "rating",
            "name",
            "certificationSystemId",
            "description",
            "officialURL"
        });
        this.delegateLookupImpl = new ConsolidatedCertificationSystemsDelegateLookupImpl(this);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystems", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRatingOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystems", ordinal, "rating");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ConsolidatedCertificationSystemsArrayOfRatingTypeAPI getRatingTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsArrayOfRatingTypeAPI();
    }

    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystems", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ConsolidatedCertificationSystemsNameTypeAPI getNameTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsNameTypeAPI();
    }

    public long getCertificationSystemId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("ConsolidatedCertificationSystems", ordinal, "certificationSystemId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("ConsolidatedCertificationSystems", ordinal, "certificationSystemId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystems", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ConsolidatedCertificationSystemsDescriptionTypeAPI getDescriptionTypeAPI() {
        return getAPI().getConsolidatedCertificationSystemsDescriptionTypeAPI();
    }

    public int getOfficialURLOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("ConsolidatedCertificationSystems", ordinal, "officialURL");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getOfficialURLTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public ConsolidatedCertificationSystemsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}