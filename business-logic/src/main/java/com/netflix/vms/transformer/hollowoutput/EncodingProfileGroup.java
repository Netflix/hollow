package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Arrays;
import java.util.Set;

@HollowPrimaryKey(fields={"groupNameStr"})
public class EncodingProfileGroup implements Cloneable {

    public char[] groupNameStr = null;
    public Set<Integer> encodingProfileIds = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof EncodingProfileGroup))
            return false;

        EncodingProfileGroup o = (EncodingProfileGroup) other;
        if(!Arrays.equals(o.groupNameStr, groupNameStr)) return false;
        if(o.encodingProfileIds == null) {
            if(encodingProfileIds != null) return false;
        } else if(!o.encodingProfileIds.equals(encodingProfileIds)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(groupNameStr);
        hashCode = hashCode * 31 + (encodingProfileIds == null ? 1237 : encodingProfileIds.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("EncodingProfileGroup{");
        builder.append("groupNameStr=").append(groupNameStr);
        builder.append(",encodingProfileIds=").append(encodingProfileIds);
        builder.append("}");
        return builder.toString();
    }

    public EncodingProfileGroup clone() {
        try {
            EncodingProfileGroup clone = (EncodingProfileGroup)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}