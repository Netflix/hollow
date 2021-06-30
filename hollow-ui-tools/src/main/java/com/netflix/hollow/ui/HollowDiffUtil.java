package com.netflix.hollow.ui;

import java.text.DecimalFormat;

public class HollowDiffUtil {
    private static final String[] HEAP_SIZE_UNITS = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB"};

    public static String formatBytes(long sizeInBytes) {
        if (sizeInBytes==0) return "0 B";

        String sign = (sizeInBytes < 0) ? "-" : "";
        sizeInBytes = Math.abs(sizeInBytes);

        int digitGroups = (int) (Math.log10(sizeInBytes)/Math.log10(1024));
        DecimalFormat BYTE_FORMATTER = new DecimalFormat("#,##0.##");
        return sign + BYTE_FORMATTER.format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + HEAP_SIZE_UNITS[digitGroups];
    }
}