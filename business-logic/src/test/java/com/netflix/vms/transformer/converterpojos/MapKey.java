package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="MapKey")
public class MapKey implements Cloneable {

    public char[] value = null;

    public MapKey() { }

    public MapKey(char[] value) {
        this.value = value;
    }

    public MapKey(String value) {
        this.value = value.toCharArray();
    }

    public MapKey setValue(char[] value) {
        this.value = value;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof MapKey))
            return false;

        MapKey o = (MapKey) other;
        if(!Arrays.equals(o.value, value)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MapKey{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public MapKey clone() {
        try {
            MapKey clone = (MapKey)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}