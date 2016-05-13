package com.netflix.vms.transformer.common;

public interface TransformerMetricRecorder {

    void recordMetric(Metric name, long value);

    void incrementCounter(Metric name, long incrementBy);

    public static enum Metric {
        WaitForNextCycleDuration,
        ReadInputDataDuration,
        ProcessDataDuration,
        WriteOutputDataDuration,

        ConsecutiveCycleFailures,
        FailedProcessingIndividualHierarchies,

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
