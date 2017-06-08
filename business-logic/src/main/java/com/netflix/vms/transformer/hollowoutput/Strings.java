package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class Strings implements Cloneable, Comparable<Strings> {

    public char[] value = null;

    public Strings() { }

    public Strings(char[] value) {
        this.value = value;
    }

    public Strings(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Strings))
            return false;

        Strings o = (Strings) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Strings{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public Strings clone() {
        try {
            Strings clone = (Strings)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;

    @Override
    public int compareTo(Strings o) {
        int len1 = value.length;
        int len2 = o.value.length;
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = o.value;

        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
}