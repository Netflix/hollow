package com.netflix.hollow.tools.diff.report;

import java.util.List;

/**
 * Structured export of a calculated {@link com.netflix.hollow.tools.diff.HollowDiff}.
 */
public final class HollowDiffReport {

    private String fromNamespace;
    private String toNamespace;
    private Long fromVersion;
    private Long toVersion;
    private List<BlobHeader> blobHeaders;
    private List<TypeDiff> typeDiffs;

    public String getFromNamespace() {
        return fromNamespace;
    }

    public void setFromNamespace(String fromNamespace) {
        this.fromNamespace = fromNamespace;
    }

    public String getToNamespace() {
        return toNamespace;
    }

    public void setToNamespace(String toNamespace) {
        this.toNamespace = toNamespace;
    }

    public Long getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(Long fromVersion) {
        this.fromVersion = fromVersion;
    }

    public Long getToVersion() {
        return toVersion;
    }

    public void setToVersion(Long toVersion) {
        this.toVersion = toVersion;
    }

    public List<BlobHeader> getBlobHeaders() {
        return blobHeaders;
    }

    public void setBlobHeaders(List<BlobHeader> blobHeaders) {
        this.blobHeaders = blobHeaders;
    }

    public List<TypeDiff> getTypeDiffs() {
        return typeDiffs;
    }

    public void setTypeDiffs(List<TypeDiff> typeDiffs) {
        this.typeDiffs = typeDiffs;
    }

    /** Hollow snapshot header tag, as shown in the diff UI &quot;Blob Information&quot; table. */
    public static final class BlobHeader {
        private String headerName;
        private String fromValue;
        private String toValue;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getFromValue() {
            return fromValue;
        }

        public void setFromValue(String fromValue) {
            this.fromValue = fromValue;
        }

        public String getToValue() {
            return toValue;
        }

        public void setToValue(String toValue) {
            this.toValue = toValue;
        }
    }

    public static final class TypeDiff {
        private String type;
        private int totalMatches;
        private int totalItemsInFromState;
        private int totalItemsInToState;
        private long typeDiffScore;
        private int unmatchedOrdinalsInFromCount;
        private int unmatchedOrdinalsInToCount;
        private List<Integer> unmatchedOrdinalsInFromSample;
        private List<Integer> unmatchedOrdinalsInToSample;
        private List<FieldDiff> fieldDiffs;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotalMatches() {
            return totalMatches;
        }

        public void setTotalMatches(int totalMatches) {
            this.totalMatches = totalMatches;
        }

        public int getTotalItemsInFromState() {
            return totalItemsInFromState;
        }

        public void setTotalItemsInFromState(int totalItemsInFromState) {
            this.totalItemsInFromState = totalItemsInFromState;
        }

        public int getTotalItemsInToState() {
            return totalItemsInToState;
        }

        public void setTotalItemsInToState(int totalItemsInToState) {
            this.totalItemsInToState = totalItemsInToState;
        }

        public long getTypeDiffScore() {
            return typeDiffScore;
        }

        public void setTypeDiffScore(long typeDiffScore) {
            this.typeDiffScore = typeDiffScore;
        }

        public int getUnmatchedOrdinalsInFromCount() {
            return unmatchedOrdinalsInFromCount;
        }

        public void setUnmatchedOrdinalsInFromCount(int unmatchedOrdinalsInFromCount) {
            this.unmatchedOrdinalsInFromCount = unmatchedOrdinalsInFromCount;
        }

        public int getUnmatchedOrdinalsInToCount() {
            return unmatchedOrdinalsInToCount;
        }

        public void setUnmatchedOrdinalsInToCount(int unmatchedOrdinalsInToCount) {
            this.unmatchedOrdinalsInToCount = unmatchedOrdinalsInToCount;
        }

        public List<Integer> getUnmatchedOrdinalsInFromSample() {
            return unmatchedOrdinalsInFromSample;
        }

        public void setUnmatchedOrdinalsInFromSample(List<Integer> unmatchedOrdinalsInFromSample) {
            this.unmatchedOrdinalsInFromSample = unmatchedOrdinalsInFromSample;
        }

        public List<Integer> getUnmatchedOrdinalsInToSample() {
            return unmatchedOrdinalsInToSample;
        }

        public void setUnmatchedOrdinalsInToSample(List<Integer> unmatchedOrdinalsInToSample) {
            this.unmatchedOrdinalsInToSample = unmatchedOrdinalsInToSample;
        }

        public List<FieldDiff> getFieldDiffs() {
            return fieldDiffs;
        }

        public void setFieldDiffs(List<FieldDiff> fieldDiffs) {
            this.fieldDiffs = fieldDiffs;
        }
    }

    public static final class FieldDiff {
        private String fieldPath;
        private long totalDiffScore;
        private int numDiffs;
        private List<FieldDiffPair> pairsSample;

        public String getFieldPath() {
            return fieldPath;
        }

        public void setFieldPath(String fieldPath) {
            this.fieldPath = fieldPath;
        }

        public long getTotalDiffScore() {
            return totalDiffScore;
        }

        public void setTotalDiffScore(long totalDiffScore) {
            this.totalDiffScore = totalDiffScore;
        }

        public int getNumDiffs() {
            return numDiffs;
        }

        public void setNumDiffs(int numDiffs) {
            this.numDiffs = numDiffs;
        }

        public List<FieldDiffPair> getPairsSample() {
            return pairsSample;
        }

        public void setPairsSample(List<FieldDiffPair> pairsSample) {
            this.pairsSample = pairsSample;
        }
    }

    public static final class FieldDiffPair {
        private int fromOrdinal;
        private int toOrdinal;
        private int score;

        public int getFromOrdinal() {
            return fromOrdinal;
        }

        public void setFromOrdinal(int fromOrdinal) {
            this.fromOrdinal = fromOrdinal;
        }

        public int getToOrdinal() {
            return toOrdinal;
        }

        public void setToOrdinal(int toOrdinal) {
            this.toOrdinal = toOrdinal;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
