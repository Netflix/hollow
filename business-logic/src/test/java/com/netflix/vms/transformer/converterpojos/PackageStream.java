package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="PackageStream")
public class PackageStream implements Cloneable {

    public long downloadableId = java.lang.Long.MIN_VALUE;
    public long streamProfileId = java.lang.Long.MIN_VALUE;
    public StreamFileIdentification fileIdentification = null;
    public StreamDimensions dimensions = null;
    public String tags = null;
    public StreamAssetType assetType = null;
    public ImageStreamInfo imageInfo = null;
    public StreamNonImageInfo nonImageInfo = null;
    public StreamDeployment deployment = null;
    public List<String> modifications = null;
    public StreamAssetMetadata metadataId = null;

    public PackageStream setDownloadableId(long downloadableId) {
        this.downloadableId = downloadableId;
        return this;
    }
    public PackageStream setStreamProfileId(long streamProfileId) {
        this.streamProfileId = streamProfileId;
        return this;
    }
    public PackageStream setFileIdentification(StreamFileIdentification fileIdentification) {
        this.fileIdentification = fileIdentification;
        return this;
    }
    public PackageStream setDimensions(StreamDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }
    public PackageStream setTags(String tags) {
        this.tags = tags;
        return this;
    }
    public PackageStream setAssetType(StreamAssetType assetType) {
        this.assetType = assetType;
        return this;
    }
    public PackageStream setImageInfo(ImageStreamInfo imageInfo) {
        this.imageInfo = imageInfo;
        return this;
    }
    public PackageStream setNonImageInfo(StreamNonImageInfo nonImageInfo) {
        this.nonImageInfo = nonImageInfo;
        return this;
    }
    public PackageStream setDeployment(StreamDeployment deployment) {
        this.deployment = deployment;
        return this;
    }
    public PackageStream setModifications(List<String> modifications) {
        this.modifications = modifications;
        return this;
    }
    public PackageStream setMetadataId(StreamAssetMetadata metadataId) {
        this.metadataId = metadataId;
        return this;
    }
    public PackageStream addToModifications(String string) {
        if (this.modifications == null) {
            this.modifications = new ArrayList<String>();
        }
        this.modifications.add(string);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PackageStream))
            return false;

        PackageStream o = (PackageStream) other;
        if(o.downloadableId != downloadableId) return false;
        if(o.streamProfileId != streamProfileId) return false;
        if(o.fileIdentification == null) {
            if(fileIdentification != null) return false;
        } else if(!o.fileIdentification.equals(fileIdentification)) return false;
        if(o.dimensions == null) {
            if(dimensions != null) return false;
        } else if(!o.dimensions.equals(dimensions)) return false;
        if(o.tags == null) {
            if(tags != null) return false;
        } else if(!o.tags.equals(tags)) return false;
        if(o.assetType == null) {
            if(assetType != null) return false;
        } else if(!o.assetType.equals(assetType)) return false;
        if(o.imageInfo == null) {
            if(imageInfo != null) return false;
        } else if(!o.imageInfo.equals(imageInfo)) return false;
        if(o.nonImageInfo == null) {
            if(nonImageInfo != null) return false;
        } else if(!o.nonImageInfo.equals(nonImageInfo)) return false;
        if(o.deployment == null) {
            if(deployment != null) return false;
        } else if(!o.deployment.equals(deployment)) return false;
        if(o.modifications == null) {
            if(modifications != null) return false;
        } else if(!o.modifications.equals(modifications)) return false;
        if(o.metadataId == null) {
            if(metadataId != null) return false;
        } else if(!o.metadataId.equals(metadataId)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (downloadableId ^ (downloadableId >>> 32));
        hashCode = hashCode * 31 + (int) (streamProfileId ^ (streamProfileId >>> 32));
        hashCode = hashCode * 31 + (fileIdentification == null ? 1237 : fileIdentification.hashCode());
        hashCode = hashCode * 31 + (dimensions == null ? 1237 : dimensions.hashCode());
        hashCode = hashCode * 31 + (tags == null ? 1237 : tags.hashCode());
        hashCode = hashCode * 31 + (assetType == null ? 1237 : assetType.hashCode());
        hashCode = hashCode * 31 + (imageInfo == null ? 1237 : imageInfo.hashCode());
        hashCode = hashCode * 31 + (nonImageInfo == null ? 1237 : nonImageInfo.hashCode());
        hashCode = hashCode * 31 + (deployment == null ? 1237 : deployment.hashCode());
        hashCode = hashCode * 31 + (modifications == null ? 1237 : modifications.hashCode());
        hashCode = hashCode * 31 + (metadataId == null ? 1237 : metadataId.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PackageStream{");
        builder.append("downloadableId=").append(downloadableId);
        builder.append(",streamProfileId=").append(streamProfileId);
        builder.append(",fileIdentification=").append(fileIdentification);
        builder.append(",dimensions=").append(dimensions);
        builder.append(",tags=").append(tags);
        builder.append(",assetType=").append(assetType);
        builder.append(",imageInfo=").append(imageInfo);
        builder.append(",nonImageInfo=").append(nonImageInfo);
        builder.append(",deployment=").append(deployment);
        builder.append(",modifications=").append(modifications);
        builder.append(",metadataId=").append(metadataId);
        builder.append("}");
        return builder.toString();
    }

    public PackageStream clone() {
        try {
            PackageStream clone = (PackageStream)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}