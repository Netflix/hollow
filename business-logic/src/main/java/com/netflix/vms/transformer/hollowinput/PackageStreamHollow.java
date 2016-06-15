package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class PackageStreamHollow extends HollowObject {

    public PackageStreamHollow(PackageStreamDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getDownloadableId() {
        return delegate().getDownloadableId(ordinal);
    }

    public Long _getDownloadableIdBoxed() {
        return delegate().getDownloadableIdBoxed(ordinal);
    }

    public long _getStreamProfileId() {
        return delegate().getStreamProfileId(ordinal);
    }

    public Long _getStreamProfileIdBoxed() {
        return delegate().getStreamProfileIdBoxed(ordinal);
    }

    public StreamFileIdentificationHollow _getFileIdentification() {
        int refOrdinal = delegate().getFileIdentificationOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamFileIdentificationHollow(refOrdinal);
    }

    public StreamDimensionsHollow _getDimensions() {
        int refOrdinal = delegate().getDimensionsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamDimensionsHollow(refOrdinal);
    }

    public StringHollow _getTags() {
        int refOrdinal = delegate().getTagsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StreamAssetTypeHollow _getAssetType() {
        int refOrdinal = delegate().getAssetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamAssetTypeHollow(refOrdinal);
    }

    public ImageStreamInfoHollow _getImageInfo() {
        int refOrdinal = delegate().getImageInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getImageStreamInfoHollow(refOrdinal);
    }

    public StreamNonImageInfoHollow _getNonImageInfo() {
        int refOrdinal = delegate().getNonImageInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamNonImageInfoHollow(refOrdinal);
    }

    public StreamDeploymentHollow _getDeployment() {
        int refOrdinal = delegate().getDeploymentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamDeploymentHollow(refOrdinal);
    }

    public ListOfStringHollow _getModifications() {
        int refOrdinal = delegate().getModificationsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfStringHollow(refOrdinal);
    }

    public StreamAssetMetadataHollow _getMetadataId() {
        int refOrdinal = delegate().getMetadataIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStreamAssetMetadataHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageStreamTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PackageStreamDelegate delegate() {
        return (PackageStreamDelegate)delegate;
    }

}