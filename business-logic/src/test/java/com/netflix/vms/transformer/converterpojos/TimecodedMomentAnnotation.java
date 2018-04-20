package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="TimecodedMomentAnnotation")
public class TimecodedMomentAnnotation implements Cloneable {

    public String type = null;
    public long startMillis = java.lang.Long.MIN_VALUE;
    public long endMillis = java.lang.Long.MIN_VALUE;

    public TimecodedMomentAnnotation setType(String type) {
        this.type = type;
        return this;
    }
    public TimecodedMomentAnnotation setStartMillis(long startMillis) {
        this.startMillis = startMillis;
        return this;
    }
    public TimecodedMomentAnnotation setEndMillis(long endMillis) {
        this.endMillis = endMillis;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TimecodedMomentAnnotation))
            return false;

        TimecodedMomentAnnotation o = (TimecodedMomentAnnotation) other;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(o.startMillis != startMillis) return false;
        if(o.endMillis != endMillis) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + (int) (startMillis ^ (startMillis >>> 32));
        hashCode = hashCode * 31 + (int) (endMillis ^ (endMillis >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TimecodedMomentAnnotation{");
        builder.append("type=").append(type);
        builder.append(",startMillis=").append(startMillis);
        builder.append(",endMillis=").append(endMillis);
        builder.append("}");
        return builder.toString();
    }

    public TimecodedMomentAnnotation clone() {
        try {
            TimecodedMomentAnnotation clone = (TimecodedMomentAnnotation)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}