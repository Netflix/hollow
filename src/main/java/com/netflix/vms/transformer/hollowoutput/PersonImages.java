package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class PersonImages implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public List<ArtWorkDescriptor> artWorkDescriptors = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonImages))
            return false;

        PersonImages o = (PersonImages) other;
        if(o.id != id) return false;
        if(o.artWorkDescriptors == null) {
            if(artWorkDescriptors != null) return false;
        } else if(!o.artWorkDescriptors.equals(artWorkDescriptors)) return false;
        return true;
    }

    public PersonImages clone() {
        try {
            PersonImages clone = (PersonImages)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}