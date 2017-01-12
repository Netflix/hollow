package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamProfilesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StreamProfilesDelegate {

    private final Long id;
    private final Long drmType;
    private final int descriptionOrdinal;
    private final Boolean is3D;
    private final int name27AndAboveOrdinal;
    private final int mimeTypeOrdinal;
    private final Long drmKeyGroup;
    private final int name26AndBelowOrdinal;
    private final Long audioChannelCount;
    private final int profileTypeOrdinal;
    private final int fileExtensionOrdinal;
    private final Boolean isAdaptiveSwitching;
   private StreamProfilesTypeAPI typeAPI;

    public StreamProfilesDelegateCachedImpl(StreamProfilesTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.drmType = typeAPI.getDrmTypeBoxed(ordinal);
        this.descriptionOrdinal = typeAPI.getDescriptionOrdinal(ordinal);
        this.is3D = typeAPI.getIs3DBoxed(ordinal);
        this.name27AndAboveOrdinal = typeAPI.getName27AndAboveOrdinal(ordinal);
        this.mimeTypeOrdinal = typeAPI.getMimeTypeOrdinal(ordinal);
        this.drmKeyGroup = typeAPI.getDrmKeyGroupBoxed(ordinal);
        this.name26AndBelowOrdinal = typeAPI.getName26AndBelowOrdinal(ordinal);
        this.audioChannelCount = typeAPI.getAudioChannelCountBoxed(ordinal);
        this.profileTypeOrdinal = typeAPI.getProfileTypeOrdinal(ordinal);
        this.fileExtensionOrdinal = typeAPI.getFileExtensionOrdinal(ordinal);
        this.isAdaptiveSwitching = typeAPI.getIsAdaptiveSwitchingBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
    }

    public long getDrmType(int ordinal) {
        return drmType.longValue();
    }

    public Long getDrmTypeBoxed(int ordinal) {
        return drmType;
    }

    public int getDescriptionOrdinal(int ordinal) {
        return descriptionOrdinal;
    }

    public boolean getIs3D(int ordinal) {
        return is3D.booleanValue();
    }

    public Boolean getIs3DBoxed(int ordinal) {
        return is3D;
    }

    public int getName27AndAboveOrdinal(int ordinal) {
        return name27AndAboveOrdinal;
    }

    public int getMimeTypeOrdinal(int ordinal) {
        return mimeTypeOrdinal;
    }

    public long getDrmKeyGroup(int ordinal) {
        return drmKeyGroup.longValue();
    }

    public Long getDrmKeyGroupBoxed(int ordinal) {
        return drmKeyGroup;
    }

    public int getName26AndBelowOrdinal(int ordinal) {
        return name26AndBelowOrdinal;
    }

    public long getAudioChannelCount(int ordinal) {
        return audioChannelCount.longValue();
    }

    public Long getAudioChannelCountBoxed(int ordinal) {
        return audioChannelCount;
    }

    public int getProfileTypeOrdinal(int ordinal) {
        return profileTypeOrdinal;
    }

    public int getFileExtensionOrdinal(int ordinal) {
        return fileExtensionOrdinal;
    }

    public boolean getIsAdaptiveSwitching(int ordinal) {
        return isAdaptiveSwitching.booleanValue();
    }

    public Boolean getIsAdaptiveSwitchingBoxed(int ordinal) {
        return isAdaptiveSwitching;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public StreamProfilesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StreamProfilesTypeAPI) typeAPI;
    }

}