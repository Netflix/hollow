package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Arrays;

@HollowPrimaryKey(fields="nameStr")
public class OriginServer implements Cloneable {

    public char[] nameStr = null;
    public CdnData cdnData = null;
    public StorageGroup storageGroup = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof OriginServer))
            return false;

        OriginServer o = (OriginServer) other;
        if(!Arrays.equals(o.nameStr, nameStr)) return false;
        if(o.cdnData == null) {
            if(cdnData != null) return false;
        } else if(!o.cdnData.equals(cdnData)) return false;
        if(o.storageGroup == null) {
            if(storageGroup != null) return false;
        } else if(!o.storageGroup.equals(storageGroup)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(nameStr);
        hashCode = hashCode * 31 + (cdnData == null ? 1237 : cdnData.hashCode());
        hashCode = hashCode * 31 + (storageGroup == null ? 1237 : storageGroup.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("OriginServer{");
        builder.append("nameStr=").append(nameStr);
        builder.append(",cdnData=").append(cdnData);
        builder.append(",storageGroup=").append(storageGroup);
        builder.append("}");
        return builder.toString();
    }

    public OriginServer clone() {
        try {
            OriginServer clone = (OriginServer)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}