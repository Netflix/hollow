package com.netflix.vms.transformer.common;


public interface TransformerMetricRecorder {

    void recordMetric(Metric name, double value);

    void recordMetric(Metric metric, double value, String... tagKeyValues);

    void incrementCounter(Metric name, long incrementBy);

    void incrementCounter(Metric name, long incrementBy, String... tagKeyValues);

    public static enum Metric {
        P1_ReadInputDataDuration,
        P2_ProcessDataDuration,
        P3_WriteOutputDataDuration,
        P4_WaitForPublishWorkflowDuration,
        P5_WaitForNextCycleDuration,

        ConsecutiveCycleFailures,
        ConsecutivePublishFailures,
        FailedProcessingIndividualHierarchies,

        TopNMissingViewShare,
        SnapShotSize,
        ViewShareCoveredByPBM,
        PBMFailuresMissingViewShare,

        AnnounceSuccess,

        CycleSuccessCounter;

        private final String metricName;

        private Metric() {
            this.metricName = "vms.transformer." + this.name();
        }

        @Override
        public String toString() {
            return metricName;
        }

    }

}
