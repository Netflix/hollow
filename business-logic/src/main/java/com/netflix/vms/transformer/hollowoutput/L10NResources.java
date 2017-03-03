package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.Arrays;
import java.util.Map;

@HollowPrimaryKey(fields="resourceIdStr")
public class L10NResources implements Cloneable {

    public char[] resourceIdStr = null;
    public Map<NFLocale, L10NStrings> localizedStrings = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof L10NResources))
            return false;

        L10NResources o = (L10NResources) other;
        if(!Arrays.equals(o.resourceIdStr, resourceIdStr)) return false;
        if(o.localizedStrings == null) {
            if(localizedStrings != null) return false;
        } else if(!o.localizedStrings.equals(localizedStrings)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(resourceIdStr);
        hashCode = hashCode * 31 + (localizedStrings == null ? 1237 : localizedStrings.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("L10NResources{");
        builder.append("resourceIdStr=").append(resourceIdStr);
        builder.append(",localizedStrings=").append(localizedStrings);
        builder.append("}");
        return builder.toString();
    }

    public L10NResources clone() {
        try {
            L10NResources clone = (L10NResources)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}