package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;
import java.util.Set;

public class ArtWorkDescriptor implements Cloneable {

    public NFLocale locale = null;
    public ArtWorkImageFormatEntry format = null;
    public boolean isUsDescriptor = false;
    public ArtWorkImageTypeEntry imageType = null;
    public long imageId = java.lang.Long.MIN_VALUE;
    public int seqNum = java.lang.Integer.MIN_VALUE;
    public long effectiveDate = java.lang.Long.MIN_VALUE;
    public int ordinalPriority = java.lang.Integer.MIN_VALUE;
    public Set<ArtWorkImageRecipe> recipes = null;
    public Map<Strings, AssetLocation> assetLocationMap = null;
    public ArtworkBasicPassthrough basic_passthrough = null;
    public ArtworkSourcePassthrough source = null;
    public int file_seq = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkDescriptor))
            return false;

        ArtWorkDescriptor o = (ArtWorkDescriptor) other;
        if(o.locale == null) {
            if(locale != null) return false;
        } else if(!o.locale.equals(locale)) return false;
        if(o.format == null) {
            if(format != null) return false;
        } else if(!o.format.equals(format)) return false;
        if(o.isUsDescriptor != isUsDescriptor) return false;
        if(o.imageType == null) {
            if(imageType != null) return false;
        } else if(!o.imageType.equals(imageType)) return false;
        if(o.imageId != imageId) return false;
        if(o.seqNum != seqNum) return false;
        if(o.effectiveDate != effectiveDate) return false;
        if(o.ordinalPriority != ordinalPriority) return false;
        if(o.recipes == null) {
            if(recipes != null) return false;
        } else if(!o.recipes.equals(recipes)) return false;
        if(o.assetLocationMap == null) {
            if(assetLocationMap != null) return false;
        } else if(!o.assetLocationMap.equals(assetLocationMap)) return false;
        if(o.basic_passthrough == null) {
            if(basic_passthrough != null) return false;
        } else if(!o.basic_passthrough.equals(basic_passthrough)) return false;
        if(o.source == null) {
            if(source != null) return false;
        } else if(!o.source.equals(source)) return false;
        if(o.file_seq != file_seq) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (locale == null ? 1237 : locale.hashCode());
        hashCode = hashCode * 31 + (format == null ? 1237 : format.hashCode());
        hashCode = hashCode * 31 + (isUsDescriptor? 1231 : 1237);
        hashCode = hashCode * 31 + (imageType == null ? 1237 : imageType.hashCode());
        hashCode = hashCode * 31 + (int) (imageId ^ (imageId >>> 32));
        hashCode = hashCode * 31 + seqNum;
        hashCode = hashCode * 31 + (int) (effectiveDate ^ (effectiveDate >>> 32));
        hashCode = hashCode * 31 + ordinalPriority;
        hashCode = hashCode * 31 + (recipes == null ? 1237 : recipes.hashCode());
        hashCode = hashCode * 31 + (assetLocationMap == null ? 1237 : assetLocationMap.hashCode());
        hashCode = hashCode * 31 + (basic_passthrough == null ? 1237 : basic_passthrough.hashCode());
        hashCode = hashCode * 31 + (source == null ? 1237 : source.hashCode());
        hashCode = hashCode * 31 + file_seq;
        return hashCode;
    }

    public ArtWorkDescriptor clone() {
        try {
            ArtWorkDescriptor clone = (ArtWorkDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}