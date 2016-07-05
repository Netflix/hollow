package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class StreamProfilesDelegateLookupImpl extends HollowObjectAbstractDelegate implements StreamProfilesDelegate {

    private final StreamProfilesTypeAPI typeAPI;

    public StreamProfilesDelegateLookupImpl(StreamProfilesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getId(int ordinal) {
        return typeAPI.getId(ordinal);
    }

    public Long getIdBoxed(int ordinal) {
        return typeAPI.getIdBoxed(ordinal);
    }

    public long getDrmType(int ordinal) {
        return typeAPI.getDrmType(ordinal);
    }

    public Long getDrmTypeBoxed(int ordinal) {
        return typeAPI.getDrmTypeBoxed(ordinal);
    }

    public int getDescriptionOrdinal(int ordinal) {
        return typeAPI.getDescriptionOrdinal(ordinal);
    }

    public boolean getIs3D(int ordinal) {
        return typeAPI.getIs3D(ordinal);
    }

    public Boolean getIs3DBoxed(int ordinal) {
        return typeAPI.getIs3DBoxed(ordinal);
    }

    public int getName27AndAboveOrdinal(int ordinal) {
        return typeAPI.getName27AndAboveOrdinal(ordinal);
    }

    public int getMimeTypeOrdinal(int ordinal) {
        return typeAPI.getMimeTypeOrdinal(ordinal);
    }

    public long getDrmKeyGroup(int ordinal) {
        return typeAPI.getDrmKeyGroup(ordinal);
    }

    public Long getDrmKeyGroupBoxed(int ordinal) {
        return typeAPI.getDrmKeyGroupBoxed(ordinal);
    }

    public int getName26AndBelowOrdinal(int ordinal) {
        return typeAPI.getName26AndBelowOrdinal(ordinal);
    }

    public long getAudioChannelCount(int ordinal) {
        return typeAPI.getAudioChannelCount(ordinal);
    }

    public Long getAudioChannelCountBoxed(int ordinal) {
        return typeAPI.getAudioChannelCountBoxed(ordinal);
    }

    public int getProfileTypeOrdinal(int ordinal) {
        return typeAPI.getProfileTypeOrdinal(ordinal);
    }

    public int getFileExtensionOrdinal(int ordinal) {
        return typeAPI.getFileExtensionOrdinal(ordinal);
    }

    public boolean getIsAdaptiveSwitching(int ordinal) {
        return typeAPI.getIsAdaptiveSwitching(ordinal);
    }

    public Boolean getIsAdaptiveSwitchingBoxed(int ordinal) {
        return typeAPI.getIsAdaptiveSwitchingBoxed(ordinal);
    }

    public StreamProfilesTypeAPI getTypeAPI() {
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