package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;

public class ArtWorkIndex {

    public Map<ArtWorkImageTypeEntry, Map<ArtWorkImageFormatEntry, ArtWorkExtendedIndex>> index;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}