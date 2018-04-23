package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.List;
import java.util.Map;


@SuppressWarnings("all")
@HollowTypeName(name="PassthroughData")
public class PassthroughData implements Cloneable {

    @HollowTypeName(name="SingleValuePassthroughMap")
    public Map<MapKey, String> singleValues = null;
    @HollowTypeName(name="MultiValuePassthroughMap")
    public Map<MapKey, List<String>> multiValues = null;

    public PassthroughData setSingleValues(Map<MapKey, String> singleValues) {
        this.singleValues = singleValues;
        return this;
    }
    public PassthroughData setMultiValues(Map<MapKey, List<String>> multiValues) {
        this.multiValues = multiValues;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PassthroughData))
            return false;

        PassthroughData o = (PassthroughData) other;
        if(o.singleValues == null) {
            if(singleValues != null) return false;
        } else if(!o.singleValues.equals(singleValues)) return false;
        if(o.multiValues == null) {
            if(multiValues != null) return false;
        } else if(!o.multiValues.equals(multiValues)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (singleValues == null ? 1237 : singleValues.hashCode());
        hashCode = hashCode * 31 + (multiValues == null ? 1237 : multiValues.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PassthroughData{");
        builder.append("singleValues=").append(singleValues);
        builder.append(",multiValues=").append(multiValues);
        builder.append("}");
        return builder.toString();
    }

    public PassthroughData clone() {
        try {
            PassthroughData clone = (PassthroughData)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}