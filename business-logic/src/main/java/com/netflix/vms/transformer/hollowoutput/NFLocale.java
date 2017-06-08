package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowInline;

public class NFLocale implements Cloneable, Comparable<NFLocale> {

    @HollowInline
    public String value;

    public NFLocale() { }

    public NFLocale(char[] value) {
        this.value = new String(value);
    }

    public NFLocale(String value) {
        this.value = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof NFLocale))
            return false;

        NFLocale o = (NFLocale) other;
        if(!o.value.equals(value)) return false;
        return true;
    }

    public int hashCode() {
       return this.value.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("NFLocale{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public NFLocale clone() {
        try {
            NFLocale clone = (NFLocale)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;

    @Override
    public int compareTo(NFLocale o) {
        return this.value.compareTo(o.value);
    }
}