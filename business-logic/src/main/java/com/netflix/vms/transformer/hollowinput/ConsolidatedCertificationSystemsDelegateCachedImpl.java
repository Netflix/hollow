package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class ConsolidatedCertificationSystemsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ConsolidatedCertificationSystemsDelegate {

    private final Long certificationSystemId;
    private final int countryCodeOrdinal;
    private final int ratingOrdinal;
    private final int nameOrdinal;
    private final int descriptionOrdinal;
    private final int officialURLOrdinal;
    private ConsolidatedCertificationSystemsTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsDelegateCachedImpl(ConsolidatedCertificationSystemsTypeAPI typeAPI, int ordinal) {
        this.certificationSystemId = typeAPI.getCertificationSystemIdBoxed(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.ratingOrdinal = typeAPI.getRatingOrdinal(ordinal);
        this.nameOrdinal = typeAPI.getNameOrdinal(ordinal);
        this.descriptionOrdinal = typeAPI.getDescriptionOrdinal(ordinal);
        this.officialURLOrdinal = typeAPI.getOfficialURLOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getCertificationSystemId(int ordinal) {
        if(certificationSystemId == null)
            return Long.MIN_VALUE;
        return certificationSystemId.longValue();
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return certificationSystemId;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getRatingOrdinal(int ordinal) {
        return ratingOrdinal;
    }

    public int getNameOrdinal(int ordinal) {
        return nameOrdinal;
    }

    public int getDescriptionOrdinal(int ordinal) {
        return descriptionOrdinal;
    }

    public int getOfficialURLOrdinal(int ordinal) {
        return officialURLOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public ConsolidatedCertificationSystemsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ConsolidatedCertificationSystemsTypeAPI) typeAPI;
    }

}