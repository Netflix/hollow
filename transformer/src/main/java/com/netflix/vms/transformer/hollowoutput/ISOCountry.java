package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class ISOCountry implements Cloneable {

    public char[] id = null;

    public ISOCountry() { }

    public ISOCountry(char[] value) {
        this.id = value;
    }

    public ISOCountry(String value) {
        this.id = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ISOCountry))
            return false;

        ISOCountry o = (ISOCountry) other;
        if(!Arrays.equals(o.id, id)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(id);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ISOCountry{");
        builder.append("id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public ISOCountry clone() {
        try {
            ISOCountry clone = (ISOCountry)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}