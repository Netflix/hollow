package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.Arrays;
import java.util.Set;

@HollowPrimaryKey(fields="id")
public class EncodingProfile implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public char[] name26AndBelowStr = null;
    public char[] name27AndAboveStr = null;
    public Set<Strings> dRMType = null;
    public int drmKeyGroup = java.lang.Integer.MIN_VALUE;
    public ProfileTypeDescriptor profileTypeDescriptor = null;
    public AudioChannelsDescriptor audioChannelsDescriptor = null;
    public char[] fileExtensionStr = null;
    public char[] mimeTypeStr = null;
    public char[] descriptionStr = null;
    public boolean isAdaptiveSwitching = false;
    public VideoDimensionsDescriptor videoDimensionsDescriptor = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof EncodingProfile))
            return false;

        EncodingProfile o = (EncodingProfile) other;
        if(o.id != id) return false;
        if(!Arrays.equals(o.name26AndBelowStr, name26AndBelowStr)) return false;
        if(!Arrays.equals(o.name27AndAboveStr, name27AndAboveStr)) return false;
        if(o.dRMType == null) {
            if(dRMType != null) return false;
        } else if(!o.dRMType.equals(dRMType)) return false;
        if(o.drmKeyGroup != drmKeyGroup) return false;
        if(o.profileTypeDescriptor == null) {
            if(profileTypeDescriptor != null) return false;
        } else if(!o.profileTypeDescriptor.equals(profileTypeDescriptor)) return false;
        if(o.audioChannelsDescriptor == null) {
            if(audioChannelsDescriptor != null) return false;
        } else if(!o.audioChannelsDescriptor.equals(audioChannelsDescriptor)) return false;
        if(!Arrays.equals(o.fileExtensionStr, fileExtensionStr)) return false;
        if(!Arrays.equals(o.mimeTypeStr, mimeTypeStr)) return false;
        if(!Arrays.equals(o.descriptionStr, descriptionStr)) return false;
        if(o.isAdaptiveSwitching != isAdaptiveSwitching) return false;
        if(o.videoDimensionsDescriptor == null) {
            if(videoDimensionsDescriptor != null) return false;
        } else if(!o.videoDimensionsDescriptor.equals(videoDimensionsDescriptor)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + Arrays.hashCode(name26AndBelowStr);
        hashCode = hashCode * 31 + Arrays.hashCode(name27AndAboveStr);
        hashCode = hashCode * 31 + (dRMType == null ? 1237 : dRMType.hashCode());
        hashCode = hashCode * 31 + drmKeyGroup;
        hashCode = hashCode * 31 + (profileTypeDescriptor == null ? 1237 : profileTypeDescriptor.hashCode());
        hashCode = hashCode * 31 + (audioChannelsDescriptor == null ? 1237 : audioChannelsDescriptor.hashCode());
        hashCode = hashCode * 31 + Arrays.hashCode(fileExtensionStr);
        hashCode = hashCode * 31 + Arrays.hashCode(mimeTypeStr);
        hashCode = hashCode * 31 + Arrays.hashCode(descriptionStr);
        hashCode = hashCode * 31 + (isAdaptiveSwitching? 1231 : 1237);
        hashCode = hashCode * 31 + (videoDimensionsDescriptor == null ? 1237 : videoDimensionsDescriptor.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("EncodingProfile{");
        builder.append("id=").append(id);
        builder.append(",name26AndBelowStr=").append(name26AndBelowStr);
        builder.append(",name27AndAboveStr=").append(name27AndAboveStr);
        builder.append(",dRMType=").append(dRMType);
        builder.append(",drmKeyGroup=").append(drmKeyGroup);
        builder.append(",profileTypeDescriptor=").append(profileTypeDescriptor);
        builder.append(",audioChannelsDescriptor=").append(audioChannelsDescriptor);
        builder.append(",fileExtensionStr=").append(fileExtensionStr);
        builder.append(",mimeTypeStr=").append(mimeTypeStr);
        builder.append(",descriptionStr=").append(descriptionStr);
        builder.append(",isAdaptiveSwitching=").append(isAdaptiveSwitching);
        builder.append(",videoDimensionsDescriptor=").append(videoDimensionsDescriptor);
        builder.append("}");
        return builder.toString();
    }

    public EncodingProfile clone() {
        try {
            EncodingProfile clone = (EncodingProfile)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}