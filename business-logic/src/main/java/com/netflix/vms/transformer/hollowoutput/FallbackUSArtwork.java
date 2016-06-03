package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FallbackUSArtwork implements Cloneable {

    public Video id = null;
    public Map<Strings, List<Artwork>> artworksByType = null;
    public Map<ArtWorkImageTypeEntry, Set<ArtWorkImageFormatEntry>> typeFormatIdx = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof FallbackUSArtwork))
            return false;

        FallbackUSArtwork o = (FallbackUSArtwork) other;
        if(o.id == null) {
            if(id != null) return false;
        } else if(!o.id.equals(id)) return false;
        if(o.artworksByType == null) {
            if(artworksByType != null) return false;
        } else if(!o.artworksByType.equals(artworksByType)) return false;
        if(o.typeFormatIdx == null) {
            if(typeFormatIdx != null) return false;
        } else if(!o.typeFormatIdx.equals(typeFormatIdx)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (id == null ? 1237 : id.hashCode());
        hashCode = hashCode * 31 + (artworksByType == null ? 1237 : artworksByType.hashCode());
        hashCode = hashCode * 31 + (typeFormatIdx == null ? 1237 : typeFormatIdx.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("FallbackUSArtwork{");
        builder.append("id=").append(id);
        builder.append(",artworksByType=").append(artworksByType);
        builder.append(",typeFormatIdx=").append(typeFormatIdx);
        builder.append("}");
        return builder.toString();
    }

    public FallbackUSArtwork clone() {
        try {
            FallbackUSArtwork clone = (FallbackUSArtwork)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}