package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="StreamAssetMetadata")
public class StreamAssetMetadata implements Cloneable {

    public char[] id = null;

    public StreamAssetMetadata() { }

    public StreamAssetMetadata(char[] value) {
        this.id = value;
    }

    public StreamAssetMetadata(String value) {
        this.id = value.toCharArray();
    }

    public StreamAssetMetadata setId(char[] id) {
        this.id = id;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamAssetMetadata))
            return false;

        StreamAssetMetadata o = (StreamAssetMetadata) other;
        if(!Arrays.equals(o.id, id)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(id);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamAssetMetadata{");
        builder.append("id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public StreamAssetMetadata clone() {
        try {
            StreamAssetMetadata clone = (StreamAssetMetadata)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}