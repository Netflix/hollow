package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Set;

public class EncodingProfileGroup {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}