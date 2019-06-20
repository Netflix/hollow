package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DrmHeader implements Cloneable {
    public static Strings HEADER_VERSION = new Strings("headerVersion");

    private static final Map<String, Strings> headerVersionattributeValueToInstanceMap = new ConcurrentHashMap<>();

    public int drmSystemId = java.lang.Integer.MIN_VALUE;
    public byte[] keyId = null;
    public byte[] checksum = null;
    public Map<Strings, Strings> attributes = null;

    public static Strings newHeaderVersionAttributeValue(String value) {
        return headerVersionattributeValueToInstanceMap.computeIfAbsent(value, val -> new Strings(val));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrmHeader)) {
            return false;
        }
        DrmHeader drmHeader = (DrmHeader) o;
        return drmSystemId == drmHeader.drmSystemId &&
                Arrays.equals(keyId, drmHeader.keyId) &&
                Arrays.equals(checksum, drmHeader.checksum) &&
                Objects.equals(attributes, drmHeader.attributes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(drmSystemId, attributes);
        result = 31 * result + Arrays.hashCode(keyId);
        result = 31 * result + Arrays.hashCode(checksum);
        return result;
    }

    @Override
    public String toString() {
        return "DrmHeader{" +
                "drmSystemId=" + drmSystemId +
                ", keyId=" + Arrays.toString(keyId) +
                ", checksum=" + Arrays.toString(checksum) +
                ", attributes=" + attributes +
                '}';
    }

    public DrmHeader clone() {
        try {
            DrmHeader clone = (DrmHeader) super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
