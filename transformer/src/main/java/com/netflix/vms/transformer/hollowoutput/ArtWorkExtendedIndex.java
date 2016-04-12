package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class ArtWorkExtendedIndex implements Cloneable {

    public List<ArtWorkDescriptor> artWorkList = null;

    public ArtWorkExtendedIndex() { }

    public ArtWorkExtendedIndex(List<ArtWorkDescriptor> value) {
        this.artWorkList = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkExtendedIndex))
            return false;

        ArtWorkExtendedIndex o = (ArtWorkExtendedIndex) other;
        if(o.artWorkList == null) {
            if(artWorkList != null) return false;
        } else if(!o.artWorkList.equals(artWorkList)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (artWorkList == null ? 1237 : artWorkList.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtWorkExtendedIndex{");
        builder.append("artWorkList=").append(artWorkList);
        builder.append("}");
        return builder.toString();
    }

    public ArtWorkExtendedIndex clone() {
        try {
            ArtWorkExtendedIndex clone = (ArtWorkExtendedIndex)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}