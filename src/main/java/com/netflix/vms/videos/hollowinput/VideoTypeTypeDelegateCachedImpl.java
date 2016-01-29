package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoTypeTypeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoTypeTypeDelegate {

    private final Boolean isOriginal;
    private final Long showMemberTypeId;
    private final int copyrightOrdinal;
    private final int countryCodeOrdinal;
    private final Boolean isContentApproved;
    private final int mediaOrdinal;
    private final Boolean isCanon;
    private final Boolean isExtended;
   private VideoTypeTypeTypeAPI typeAPI;

    public VideoTypeTypeDelegateCachedImpl(VideoTypeTypeTypeAPI typeAPI, int ordinal) {
        this.isOriginal = typeAPI.getIsOriginalBoxed(ordinal);
        this.showMemberTypeId = typeAPI.getShowMemberTypeIdBoxed(ordinal);
        this.copyrightOrdinal = typeAPI.getCopyrightOrdinal(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.isContentApproved = typeAPI.getIsContentApprovedBoxed(ordinal);
        this.mediaOrdinal = typeAPI.getMediaOrdinal(ordinal);
        this.isCanon = typeAPI.getIsCanonBoxed(ordinal);
        this.isExtended = typeAPI.getIsExtendedBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getIsOriginal(int ordinal) {
        return isOriginal.booleanValue();
    }

    public Boolean getIsOriginalBoxed(int ordinal) {
        return isOriginal;
    }

    public long getShowMemberTypeId(int ordinal) {
        return showMemberTypeId.longValue();
    }

    public Long getShowMemberTypeIdBoxed(int ordinal) {
        return showMemberTypeId;
    }

    public int getCopyrightOrdinal(int ordinal) {
        return copyrightOrdinal;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public boolean getIsContentApproved(int ordinal) {
        return isContentApproved.booleanValue();
    }

    public Boolean getIsContentApprovedBoxed(int ordinal) {
        return isContentApproved;
    }

    public int getMediaOrdinal(int ordinal) {
        return mediaOrdinal;
    }

    public boolean getIsCanon(int ordinal) {
        return isCanon.booleanValue();
    }

    public Boolean getIsCanonBoxed(int ordinal) {
        return isCanon;
    }

    public boolean getIsExtended(int ordinal) {
        return isExtended.booleanValue();
    }

    public Boolean getIsExtendedBoxed(int ordinal) {
        return isExtended;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoTypeTypeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoTypeTypeTypeAPI) typeAPI;
    }

}