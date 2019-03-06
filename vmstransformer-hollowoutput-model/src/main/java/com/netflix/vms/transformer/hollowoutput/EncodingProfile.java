package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Objects;
import java.util.Set;

@HollowPrimaryKey(fields = "id")
public class EncodingProfile implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    @HollowInline
    public String name26AndBelowStr = null;
    @HollowInline
    public String name27AndAboveStr = null;
    public Set<Strings> dRMType = null;
    public int drmKeyGroup = java.lang.Integer.MIN_VALUE;
    public ProfileTypeDescriptor profileTypeDescriptor = null;
    public AudioChannelsDescriptor audioChannelsDescriptor = null;
    @HollowInline
    public String fileExtensionStr = null;
    @HollowInline
    public String mimeTypeStr = null;
    @HollowInline
    public String descriptionStr = null;
    public boolean isAdaptiveSwitching = false;
    public VideoDimensionsDescriptor videoDimensionsDescriptor = null;
    @HollowInline
    public String audioCodec = null;
    @HollowInline
    public String videoCodec = null;
    @HollowInline
    public String colorAttributes = null;
    public int bitDepth = java.lang.Integer.MIN_VALUE;
    @HollowInline
    public String drmKeyType = null;
    @HollowInline
    public String encryptionScheme = null;
    @HollowInline
    public String playreadyHeaderVersion = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EncodingProfile that = (EncodingProfile) o;
        return id == that.id &&
                drmKeyGroup == that.drmKeyGroup &&
                isAdaptiveSwitching == that.isAdaptiveSwitching &&
                bitDepth == that.bitDepth &&
                __assigned_ordinal == that.__assigned_ordinal &&
                Objects.equals(name26AndBelowStr, that.name26AndBelowStr) &&
                Objects.equals(name27AndAboveStr, that.name27AndAboveStr) &&
                Objects.equals(dRMType, that.dRMType) &&
                Objects.equals(profileTypeDescriptor, that.profileTypeDescriptor) &&
                Objects.equals(audioChannelsDescriptor, that.audioChannelsDescriptor) &&
                Objects.equals(fileExtensionStr, that.fileExtensionStr) &&
                Objects.equals(mimeTypeStr, that.mimeTypeStr) &&
                Objects.equals(descriptionStr, that.descriptionStr) &&
                Objects.equals(videoDimensionsDescriptor, that.videoDimensionsDescriptor) &&
                Objects.equals(audioCodec, that.audioCodec) &&
                Objects.equals(videoCodec, that.videoCodec) &&
                Objects.equals(colorAttributes, that.colorAttributes) &&
                Objects.equals(drmKeyType, that.drmKeyType) &&
                Objects.equals(encryptionScheme, that.encryptionScheme) &&
                Objects.equals(playreadyHeaderVersion, that.playreadyHeaderVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name26AndBelowStr, name27AndAboveStr, dRMType, drmKeyGroup, profileTypeDescriptor,
                audioChannelsDescriptor, fileExtensionStr, mimeTypeStr, descriptionStr, isAdaptiveSwitching,
                videoDimensionsDescriptor, audioCodec, videoCodec, colorAttributes, bitDepth, drmKeyType, encryptionScheme,
                playreadyHeaderVersion, __assigned_ordinal);
    }

    @Override
    public String toString() {
        return "EncodingProfile{" +
                "id=" + id +
                ", name26AndBelowStr='" + name26AndBelowStr + '\'' +
                ", name27AndAboveStr='" + name27AndAboveStr + '\'' +
                ", dRMType=" + dRMType +
                ", drmKeyGroup=" + drmKeyGroup +
                ", profileTypeDescriptor=" + profileTypeDescriptor +
                ", audioChannelsDescriptor=" + audioChannelsDescriptor +
                ", fileExtensionStr='" + fileExtensionStr + '\'' +
                ", mimeTypeStr='" + mimeTypeStr + '\'' +
                ", descriptionStr='" + descriptionStr + '\'' +
                ", isAdaptiveSwitching=" + isAdaptiveSwitching +
                ", videoDimensionsDescriptor=" + videoDimensionsDescriptor +
                ", audioCodec='" + audioCodec + '\'' +
                ", videoCodec='" + videoCodec + '\'' +
                ", colorAttributes='" + colorAttributes + '\'' +
                ", bitDepth=" + bitDepth +
                ", drmKeyType='" + drmKeyType + '\'' +
                ", encryptionScheme='" + encryptionScheme + '\'' +
                ", playreadyHeaderVersion='" + playreadyHeaderVersion + '\'' +
                ", __assigned_ordinal=" + __assigned_ordinal +
                '}';
    }

    public EncodingProfile clone() {
        try {
            EncodingProfile clone = (EncodingProfile) super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
