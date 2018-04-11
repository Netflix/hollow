package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="CodecPrivateDataString")
public class CodecPrivateDataString implements Cloneable {

    public char[] value = null;

    public CodecPrivateDataString() { }

    public CodecPrivateDataString(char[] value) {
        this.value = value;
    }

    public CodecPrivateDataString(String value) {
        this.value = value.toCharArray();
    }

    public CodecPrivateDataString setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CodecPrivateDataString))
            return false;

        CodecPrivateDataString o = (CodecPrivateDataString) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CodecPrivateDataString{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public CodecPrivateDataString clone() {
        try {
            CodecPrivateDataString clone = (CodecPrivateDataString)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}