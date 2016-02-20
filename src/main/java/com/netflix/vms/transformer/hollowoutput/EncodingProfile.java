package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Set;

public class EncodingProfile {

    public int id;
    public char[] name26AndBelowStr;
    public char[] name27AndAboveStr;
    public Set<Strings> dRMType;
    public int drmKeyGroup;
    public ProfileTypeDescriptor profileTypeDescriptor;
    public AudioChannelsDescriptor audioChannelsDescriptor;
    public char[] fileExtensionStr;
    public char[] mimeTypeStr;
    public char[] descriptionStr;
    public boolean isAdaptiveSwitching;
    public VideoDimensionsDescriptor videoDimensionsDescriptor;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}