package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoImages implements Cloneable {

    public Map<Strings, List<Artwork>> artworks = null;
    public Map<ArtWorkImageTypeEntry, Set<ArtWorkImageFormatEntry>> artworkFormatsByType = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoImages))
            return false;

        VideoImages o = (VideoImages) other;
        if(o.artworks == null) {
            if(artworks != null) return false;
        } else if(!o.artworks.equals(artworks)) return false;
        if(o.artworkFormatsByType == null) {
            if(artworkFormatsByType != null) return false;
        } else if(!o.artworkFormatsByType.equals(artworkFormatsByType)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (artworks == null ? 1237 : artworks.hashCode());
        hashCode = hashCode * 31 + (artworkFormatsByType == null ? 1237 : artworkFormatsByType.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoImages{");
        builder.append("artworks=").append(artworks);
        builder.append(",artworkFormatsByType=").append(artworkFormatsByType);
        builder.append("}");
        return builder.toString();
    }

    public VideoImages clone() {
        try {
            VideoImages clone = (VideoImages)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}