package com.netflix.vms.transformer.hollowoutput;

import java.util.Set;
import java.util.Map;

public class ArtWorkDescriptor {

    public NFLocale locale;
    public ArtWorkImageFormatEntry format;
    public boolean isUsDescriptor;
    public ArtWorkImageTypeEntry imageType;
    public long imageId;
    public int seqNum;
    public long effectiveDate;
    public int ordinalPriority;
    public Set<ArtWorkImageRecipe> recipes;
    public Map<Strings, AssetLocation> assetLocationMap;
    public ArtworkBasicPassthrough basic_passthrough;
    public ArtworkSourcePassthrough source;
    public int file_seq;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}