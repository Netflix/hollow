package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="DerivativeTag")
public class DerivativeTag implements Cloneable {

    public char[] value = null;

    public DerivativeTag() { }

    public DerivativeTag(char[] value) {
        this.value = value;
    }

    public DerivativeTag(String value) {
        this.value = value.toCharArray();
    }

    public DerivativeTag setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DerivativeTag))
            return false;

        DerivativeTag o = (DerivativeTag) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DerivativeTag{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public DerivativeTag clone() {
        try {
            DerivativeTag clone = (DerivativeTag)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}