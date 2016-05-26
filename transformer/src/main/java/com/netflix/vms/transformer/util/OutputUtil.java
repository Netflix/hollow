package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowoutput.Integer;

public class OutputUtil {

    public static Integer getNullableInteger(int value) {
        if (java.lang.Integer.MIN_VALUE == value) return null;

        return new Integer(value);
    }

    public static int sanitize(int value) {
        if (java.lang.Integer.MIN_VALUE == value) return 0;

        return value;
    }
}
