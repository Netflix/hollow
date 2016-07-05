package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PackageStreamDelegateLookupImpl extends HollowObjectAbstractDelegate implements PackageStreamDelegate {

    private final PackageStreamTypeAPI typeAPI;

    public PackageStreamDelegateLookupImpl(PackageStreamTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getDownloadableId(int ordinal) {
        return typeAPI.getDownloadableId(ordinal);
    }

    public Long getDownloadableIdBoxed(int ordinal) {
        return typeAPI.getDownloadableIdBoxed(ordinal);
    }

    public long getStreamProfileId(int ordinal) {
        return typeAPI.getStreamProfileId(ordinal);
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        return typeAPI.getStreamProfileIdBoxed(ordinal);
    }

    public int getFileIdentificationOrdinal(int ordinal) {
        return typeAPI.getFileIdentificationOrdinal(ordinal);
    }

    public int getDimensionsOrdinal(int ordinal) {
        return typeAPI.getDimensionsOrdinal(ordinal);
    }

    public int getTagsOrdinal(int ordinal) {
        return typeAPI.getTagsOrdinal(ordinal);
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return typeAPI.getAssetTypeOrdinal(ordinal);
    }

    public int getImageInfoOrdinal(int ordinal) {
        return typeAPI.getImageInfoOrdinal(ordinal);
    }

    public int getNonImageInfoOrdinal(int ordinal) {
        return typeAPI.getNonImageInfoOrdinal(ordinal);
    }

    public int getDeploymentOrdinal(int ordinal) {
        return typeAPI.getDeploymentOrdinal(ordinal);
    }

    public int getModificationsOrdinal(int ordinal) {
        return typeAPI.getModificationsOrdinal(ordinal);
    }

    public int getMetadataIdOrdinal(int ordinal) {
        return typeAPI.getMetadataIdOrdinal(ordinal);
    }

    public PackageStreamTypeAPI getTypeAPI() {
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