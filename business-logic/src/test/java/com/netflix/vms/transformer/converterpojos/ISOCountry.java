package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="ISOCountry")
public class ISOCountry implements Cloneable {

    public char[] value = null;

    public ISOCountry() { }

    public ISOCountry(char[] value) {
        this.value = value;
    }

    public ISOCountry(String value) {
        this.value = value.toCharArray();
    }

    public ISOCountry setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ISOCountry))
            return false;

        ISOCountry o = (ISOCountry) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ISOCountry{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public ISOCountry clone() {
        try {
            ISOCountry clone = (ISOCountry)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}