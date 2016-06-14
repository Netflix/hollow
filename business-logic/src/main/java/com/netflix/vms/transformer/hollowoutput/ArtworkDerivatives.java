package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class ArtworkDerivatives implements Cloneable {

    public List<ArtworkDerivative> list = null;
    public Map<ArtWorkImageTypeEntry, Map<ArtWorkImageFormatEntry, List<Integer>>> typeFormatIndex = null;
    public Map<ArtWorkImageFormatEntry, List<Integer>> formatToDerivativeIndex = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkDerivatives))
            return false;

        ArtworkDerivatives o = (ArtworkDerivatives) other;
        if(o.list == null) {
            if(list != null) return false;
        } else if(!o.list.equals(list)) return false;
        if(o.typeFormatIndex == null) {
            if(typeFormatIndex != null) return false;
        } else if(!o.typeFormatIndex.equals(typeFormatIndex)) return false;
        if(o.formatToDerivativeIndex == null) {
            if(formatToDerivativeIndex != null) return false;
        } else if(!o.formatToDerivativeIndex.equals(formatToDerivativeIndex)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (list == null ? 1237 : list.hashCode());
        hashCode = hashCode * 31 + (typeFormatIndex == null ? 1237 : typeFormatIndex.hashCode());
        hashCode = hashCode * 31 + (formatToDerivativeIndex == null ? 1237 : formatToDerivativeIndex.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkDerivatives{");
        builder.append("list=").append(list);
        builder.append(",typeFormatIndex=").append(typeFormatIndex);
        builder.append(",formatToDerivativeIndex=").append(formatToDerivativeIndex);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkDerivatives clone() {
        try {
            ArtworkDerivatives clone = (ArtworkDerivatives)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}