package com.netflix.vms.transformer.common;


public interface TransformerMetricRecorder {

    void startTimer(DurationMetric metric);

    long stopTimer(DurationMetric metric);

    void resetTimer(DurationMetric metric);

    void recordMetric(Metric name, double value);

    void recordMetric(Metric metric, double value, String... tagKeyValues);

    void incrementCounter(Metric name, long incrementBy);

    void incrementCounter(Metric name, long incrementBy, String... tagKeyValues);

    public static enum DurationMetric {
        P1_ReadInputDataDuration,
        P2_ProcessDataDuration,
        P3_WriteOutputDataDuration,
        P4_WaitForPublishWorkflowDuration,
        P5_WaitForNextCycleDuration;

        private final String metricName = formatMetricName(name());
        @Override public String toString() { return metricName; }
    }

    public static enum Metric {
        ConsecutiveCycleFailures,
        ConsecutivePublishFailures,
        FailedProcessingIndividualHierarchies,

        TopNMissingViewShare,
        SnapShotSize,
        ViewShareCoveredByPBM,
        PBMFailuresMissingViewShare,
        PBMFailureCount,

        AnnounceSuccess,

        CycleSuccessCounter;

        private final String metricName = formatMetricName(name());
        @Override public String toString() { return metricName; }
    }

    static String formatMetricName(String metric) {
        return "vms.transformer." + metric;
    }
}