package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="StreamBoxInfoKey")
public class StreamBoxInfoKey implements Cloneable {

    public char[] value = null;

    public StreamBoxInfoKey() { }

    public StreamBoxInfoKey(char[] value) {
        this.value = value;
    }

    public StreamBoxInfoKey(String value) {
        this.value = value.toCharArray();
    }

    public StreamBoxInfoKey setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamBoxInfoKey))
            return false;

        StreamBoxInfoKey o = (StreamBoxInfoKey) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamBoxInfoKey{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public StreamBoxInfoKey clone() {
        try {
            StreamBoxInfoKey clone = (StreamBoxInfoKey)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}