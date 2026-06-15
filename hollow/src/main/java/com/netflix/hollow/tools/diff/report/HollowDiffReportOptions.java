package com.netflix.hollow.tools.diff.report;

/**
 * Limits applied when exporting a {@link com.netflix.hollow.tools.diff.HollowDiff} to JSON.
 */
public final class HollowDiffReportOptions {

    public static final int DEFAULT_MAX_UNMATCHED_SAMPLE = 200;
    public static final int DEFAULT_MAX_FIELD_DIFF_PAIRS_PER_FIELD = 50;
    private static final int MAX_CAP = 5000;

    private final int maxUnmatchedSample;
    private final int maxFieldDiffPairsPerField;

    private HollowDiffReportOptions(int maxUnmatchedSample, int maxFieldDiffPairsPerField) {
        this.maxUnmatchedSample = cap(maxUnmatchedSample);
        this.maxFieldDiffPairsPerField = cap(maxFieldDiffPairsPerField);
    }

    public static HollowDiffReportOptions defaults() {
        return new HollowDiffReportOptions(
                DEFAULT_MAX_UNMATCHED_SAMPLE, DEFAULT_MAX_FIELD_DIFF_PAIRS_PER_FIELD);
    }

    public static HollowDiffReportOptions of(int maxUnmatchedSample, int maxFieldDiffPairsPerField) {
        return new HollowDiffReportOptions(maxUnmatchedSample, maxFieldDiffPairsPerField);
    }

    public int getMaxUnmatchedSample() {
        return maxUnmatchedSample;
    }

    public int getMaxFieldDiffPairsPerField() {
        return maxFieldDiffPairsPerField;
    }

    private static int cap(int value) {
        return Math.min(Math.max(value, 0), MAX_CAP);
    }
}
