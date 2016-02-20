package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Set;

public class EncodingProfile {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}