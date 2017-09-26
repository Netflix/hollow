package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PackageStreamDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PackageStreamDelegate {

    private final Long downloadableId;
    private final Long streamProfileId;
    private final int fileIdentificationOrdinal;
    private final int dimensionsOrdinal;
    private final int tagsOrdinal;
    private final int assetTypeOrdinal;
    private final int imageInfoOrdinal;
    private final int nonImageInfoOrdinal;
    private final int deploymentOrdinal;
    private final int modificationsOrdinal;
    private final int metadataIdOrdinal;
    private PackageStreamTypeAPI typeAPI;

    public PackageStreamDelegateCachedImpl(PackageStreamTypeAPI typeAPI, int ordinal) {
        this.downloadableId = typeAPI.getDownloadableIdBoxed(ordinal);
        this.streamProfileId = typeAPI.getStreamProfileIdBoxed(ordinal);
        this.fileIdentificationOrdinal = typeAPI.getFileIdentificationOrdinal(ordinal);
        this.dimensionsOrdinal = typeAPI.getDimensionsOrdinal(ordinal);
        this.tagsOrdinal = typeAPI.getTagsOrdinal(ordinal);
        this.assetTypeOrdinal = typeAPI.getAssetTypeOrdinal(ordinal);
        this.imageInfoOrdinal = typeAPI.getImageInfoOrdinal(ordinal);
        this.nonImageInfoOrdinal = typeAPI.getNonImageInfoOrdinal(ordinal);
        this.deploymentOrdinal = typeAPI.getDeploymentOrdinal(ordinal);
        this.modificationsOrdinal = typeAPI.getModificationsOrdinal(ordinal);
        this.metadataIdOrdinal = typeAPI.getMetadataIdOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getDownloadableId(int ordinal) {
        if(downloadableId == null)
            return Long.MIN_VALUE;
        return downloadableId.longValue();
    }

    public Long getDownloadableIdBoxed(int ordinal) {
        return downloadableId;
    }

    public long getStreamProfileId(int ordinal) {
        if(streamProfileId == null)
            return Long.MIN_VALUE;
        return streamProfileId.longValue();
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        return streamProfileId;
    }

    public int getFileIdentificationOrdinal(int ordinal) {
        return fileIdentificationOrdinal;
    }

    public int getDimensionsOrdinal(int ordinal) {
        return dimensionsOrdinal;
    }

    public int getTagsOrdinal(int ordinal) {
        return tagsOrdinal;
    }

    public int getAssetTypeOrdinal(int ordinal) {
        return assetTypeOrdinal;
    }

    public int getImageInfoOrdinal(int ordinal) {
        return imageInfoOrdinal;
    }

    public int getNonImageInfoOrdinal(int ordinal) {
        return nonImageInfoOrdinal;
    }

    public int getDeploymentOrdinal(int ordinal) {
        return deploymentOrdinal;
    }

    public int getModificationsOrdinal(int ordinal) {
        return modificationsOrdinal;
    }

    public int getMetadataIdOrdinal(int ordinal) {
        return metadataIdOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PackageStreamTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PackageStreamTypeAPI) typeAPI;
    }

}