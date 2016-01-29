package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.Arrays;

public class DrmHeader {

    public int drmSystemId;
    public byte[] keyId;
    public byte[] checksum;
    public String drmHeaderMagicField;
    public Map<Strings, Strings> attributes;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmHeader))
            return false;

        DrmHeader o = (DrmHeader) other;
        if(o.drmSystemId != drmSystemId) return false;
        if(!Arrays.equals(o.keyId, keyId)) return false;
        if(!Arrays.equals(o.checksum, checksum)) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}