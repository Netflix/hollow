package com.netflix.hollow;

import java.text.DecimalFormat;

public class HollowDiffUtil {
    private static final String[] HEAP_SIZE_UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };
    public static String formatBytes(long sizeInBytes) {
        if(sizeInBytes <= 0) return "0";
        int digitGroups = (int) (Math.log10(sizeInBytes)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeInBytes/Math.pow(1024, digitGroups)) + " " + HEAP_SIZE_UNITS[digitGroups];
    }
}
