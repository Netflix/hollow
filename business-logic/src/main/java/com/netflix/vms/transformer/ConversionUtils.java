package com.netflix.vms.transformer;

import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.Strings;

public class ConversionUtils {
    public static Date getDate(DateHollow dateHollow) {
        return dateHollow != null ? new Date(dateHollow._getValue()) : null;
    }
    
    public static Strings getStrings(StringHollow stringHollow) {
        return (stringHollow != null && stringHollow._getValue() != null) ? new Strings(stringHollow._getValue().toCharArray()) : null;
    }
    
    public static String getString(StringHollow stringHollow) {
        return stringHollow != null ? stringHollow._getValue() : null;
    }
    
    public static int getInt(StringHollow stringHollow) {
        return (stringHollow != null) ? Integer.valueOf(stringHollow._getValue()) : -1;
    }
    
    public static char[] getCharArray(StringHollow stringHollow) {
        return (stringHollow != null && stringHollow._getValue() != null) ? stringHollow._getValue().toCharArray() : null;
    }
}
