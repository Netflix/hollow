package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class AwardsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, AwardsDelegate {

    private final Long awardId;
    private final int descriptionOrdinal;
    private final int alternateNameOrdinal;
    private final int awardNameOrdinal;
   private AwardsTypeAPI typeAPI;

    public AwardsDelegateCachedImpl(AwardsTypeAPI typeAPI, int ordinal) {
        this.awardId = typeAPI.getAwardIdBoxed(ordinal);
        this.descriptionOrdinal = typeAPI.getDescriptionOrdinal(ordinal);
        this.alternateNameOrdinal = typeAPI.getAlternateNameOrdinal(ordinal);
        this.awardNameOrdinal = typeAPI.getAwardNameOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAwardId(int ordinal) {
        return awardId.longValue();
    }

    public Long getAwardIdBoxed(int ordinal) {
        return awardId;
    }

    public int getDescriptionOrdinal(int ordinal) {
        return descriptionOrdinal;
    }

    public int getAlternateNameOrdinal(int ordinal) {
        return alternateNameOrdinal;
    }

    public int getAwardNameOrdinal(int ordinal) {
        return awardNameOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public AwardsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (AwardsTypeAPI) typeAPI;
    }

}