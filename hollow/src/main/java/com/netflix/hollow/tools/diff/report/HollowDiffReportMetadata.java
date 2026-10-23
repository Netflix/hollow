package com.netflix.hollow.tools.diff.report;

/**
 * Caller-supplied labels for the two sides of a {@link com.netflix.hollow.tools.diff.HollowDiff}.
 */
public final class HollowDiffReportMetadata {

    private final String fromNamespace;
    private final String toNamespace;
    private final Long fromVersion;
    private final Long toVersion;

    private HollowDiffReportMetadata(
            String fromNamespace, Long fromVersion, String toNamespace, Long toVersion) {
        this.fromNamespace = fromNamespace;
        this.fromVersion = fromVersion;
        this.toNamespace = toNamespace;
        this.toVersion = toVersion;
    }

    public static HollowDiffReportMetadata of(
            String fromNamespace, Long fromVersion, String toNamespace, Long toVersion) {
        return new HollowDiffReportMetadata(fromNamespace, fromVersion, toNamespace, toVersion);
    }

    public static HollowDiffReportMetadata empty() {
        return new HollowDiffReportMetadata(null, null, null, null);
    }

    public String getFromNamespace() {
        return fromNamespace;
    }

    public String getToNamespace() {
        return toNamespace;
    }

    public Long getFromVersion() {
        return fromVersion;
    }

    public Long getToVersion() {
        return toVersion;
    }
}
