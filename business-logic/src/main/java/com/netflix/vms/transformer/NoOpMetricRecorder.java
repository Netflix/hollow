package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;

public class NoOpMetricRecorder implements TransformerMetricRecorder {

    @Override
    public void startTimer(DurationMetric metric) {}

    @Override
    public long stopTimer(DurationMetric metric) {return 0;}

    @Override
    public void resetTimer(DurationMetric metric) {}

    @Override
    public void recordMetric(Metric name, double value) { }

    @Override
    public void incrementCounter(Metric name, long incrementBy) { }

    @Override
    public void recordMetric(Metric metric, double value, String... keyValues) {}

    @Override
    public void incrementCounter(Metric name, long incrementBy, String... tagKeyValues) { }
}