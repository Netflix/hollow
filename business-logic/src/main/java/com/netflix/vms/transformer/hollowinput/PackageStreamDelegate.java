package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface PackageStreamDelegate extends HollowObjectDelegate {

    public long getDownloadableId(int ordinal);

    public Long getDownloadableIdBoxed(int ordinal);

    public long getStreamProfileId(int ordinal);

    public Long getStreamProfileIdBoxed(int ordinal);

    public int getFileIdentificationOrdinal(int ordinal);

    public int getDimensionsOrdinal(int ordinal);

    public int getTagsOrdinal(int ordinal);

    public int getAssetTypeOrdinal(int ordinal);

    public int getImageInfoOrdinal(int ordinal);

    public int getNonImageInfoOrdinal(int ordinal);

    public int getDeploymentOrdinal(int ordinal);

    public int getModificationsOrdinal(int ordinal);

    public int getMetadataIdOrdinal(int ordinal);

    public PackageStreamTypeAPI getTypeAPI();

}