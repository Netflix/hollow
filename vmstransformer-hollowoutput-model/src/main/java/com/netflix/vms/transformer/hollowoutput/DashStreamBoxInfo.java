package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;

public class DashStreamBoxInfo {
    
    @HollowTypeName(name="DashStreamBoxInfoKey")
    public String key;
    
    public int offset = java.lang.Integer.MIN_VALUE;
    public int size = java.lang.Integer.MIN_VALUE;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + offset;
        result = prime * result + size;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DashStreamBoxInfo other = (DashStreamBoxInfo) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (offset != other.offset)
            return false;
        if (size != other.size)
            return false;
        return true;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder("DashStreamBoxInfo{");
        builder.append("key=").append(key);
        builder.append(",offset=").append(offset);
        builder.append(",size=").append(size);
        builder.append("}");
        return builder.toString();
    }

    
    @SuppressWarnings("unused")
    private final long __assigned_ordinal = -1;

}
