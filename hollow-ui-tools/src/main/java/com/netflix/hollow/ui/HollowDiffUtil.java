package com.netflix.hollow.ui;

import java.text.DecimalFormat;

public class HollowDiffUtil {
    private static final String[] HEAP_SIZE_UNITS = new String[] { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};

    public static String formatBytes(long sizeInBytes) {
        if (sizeInBytes==0) return "0 B";

        String sign = (sizeInBytes < 0) ? "-" : "";
        sizeInBytes = Math.abs(sizeInBytes);

        int digitGroups = (int) (Math.log10(sizeInBytes)/Math.log10(1024));
        DecimalFormat formatter = new DecimalFormat("#,##0.##");
        return sign + formatter.format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + HEAP_SIZE_UNITS[digitGroups];
    }
}