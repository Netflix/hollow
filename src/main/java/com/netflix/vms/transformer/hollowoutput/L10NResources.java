package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.Map;

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

    public L10NResources clone() {
        try {
            return (L10NResources)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}