package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class ArtWorkIndex implements Cloneable {

    public Map<ArtWorkImageTypeEntry, Map<ArtWorkImageFormatEntry, ArtWorkExtendedIndex>> index = null;

    public ArtWorkIndex() { }

    public ArtWorkIndex(Map<ArtWorkImageTypeEntry, Map<ArtWorkImageFormatEntry, ArtWorkExtendedIndex>> value) {
        this.index = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtWorkIndex))
            return false;

        ArtWorkIndex o = (ArtWorkIndex) other;
        if(o.index == null) {
            if(index != null) return false;
        } else if(!o.index.equals(index)) return false;
        return true;
    }

    public ArtWorkIndex clone() {
        try {
            return (ArtWorkIndex)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}