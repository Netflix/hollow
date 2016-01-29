package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CertificationSystemDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CertificationSystemDelegate {

    private final int countryCodeOrdinal;
    private final int ratingOrdinal;
    private final Long certificationSystemId;
    private final int officialURLOrdinal;
   private CertificationSystemTypeAPI typeAPI;

    public CertificationSystemDelegateCachedImpl(CertificationSystemTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.ratingOrdinal = typeAPI.getRatingOrdinal(ordinal);
        this.certificationSystemId = typeAPI.getCertificationSystemIdBoxed(ordinal);
        this.officialURLOrdinal = typeAPI.getOfficialURLOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getRatingOrdinal(int ordinal) {
        return ratingOrdinal;
    }

    public long getCertificationSystemId(int ordinal) {
        return certificationSystemId.longValue();
    }

    public Long getCertificationSystemIdBoxed(int ordinal) {
        return certificationSystemId;
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

    public CertificationSystemTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CertificationSystemTypeAPI) typeAPI;
    }

}