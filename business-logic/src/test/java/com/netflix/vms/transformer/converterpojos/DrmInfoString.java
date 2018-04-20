package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="DrmInfoString")
public class DrmInfoString implements Cloneable {

    public char[] value = null;

    public DrmInfoString() { }

    public DrmInfoString(char[] value) {
        this.value = value;
    }

    public DrmInfoString(String value) {
        this.value = value.toCharArray();
    }

    public DrmInfoString setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmInfoString))
            return false;

        DrmInfoString o = (DrmInfoString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DrmInfoString{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public DrmInfoString clone() {
        try {
            DrmInfoString clone = (DrmInfoString)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}