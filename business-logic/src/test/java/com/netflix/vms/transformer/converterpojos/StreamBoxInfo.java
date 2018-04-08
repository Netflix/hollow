package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamBoxInfo")
public class StreamBoxInfo implements Cloneable {

    public int boxOffset = java.lang.Integer.MIN_VALUE;
    public int boxSize = java.lang.Integer.MIN_VALUE;
    public StreamBoxInfoKey key = null;

    public StreamBoxInfo setBoxOffset(int boxOffset) {
        this.boxOffset = boxOffset;
        return this;
    }
    public StreamBoxInfo setBoxSize(int boxSize) {
        this.boxSize = boxSize;
        return this;
    }
    public StreamBoxInfo setKey(StreamBoxInfoKey key) {
        this.key = key;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamBoxInfo))
            return false;

        StreamBoxInfo o = (StreamBoxInfo) other;
        if(o.boxOffset != boxOffset) return false;
        if(o.boxSize != boxSize) return false;
        if(o.key == null) {
            if(key != null) return false;
        } else if(!o.key.equals(key)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + boxOffset;
        hashCode = hashCode * 31 + boxSize;
        hashCode = hashCode * 31 + (key == null ? 1237 : key.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamBoxInfo{");
        builder.append("boxOffset=").append(boxOffset);
        builder.append(",boxSize=").append(boxSize);
        builder.append(",key=").append(key);
        builder.append("}");
        return builder.toString();
    }

    public StreamBoxInfo clone() {
        try {
            StreamBoxInfo clone = (StreamBoxInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}