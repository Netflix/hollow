package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class AcquisitionSource implements Cloneable {

    public char[] value = null;

    public AcquisitionSource() {
    }

    public AcquisitionSource(char[] value) {
        this.value = value;
    }

    public AcquisitionSource(String value) {
        this.value = value.toCharArray();
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (!(other instanceof AcquisitionSource))
            return false;

        AcquisitionSource o = (AcquisitionSource) other;
        if (!Arrays.equals(o.value, value))
            return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AcquisitionSource{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public AcquisitionSource clone() {
        try {
            AcquisitionSource clone = (AcquisitionSource) super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
