package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class OriginServer {

    public char[] nameStr;
    public CdnData cdnData;
    public StorageGroup storageGroup;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}