package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;

public class NoOpMetricRecorder implements TransformerMetricRecorder {

    @Override
    public void recordMetric(Metric name, long value) { }

    @Override
    public void incrementCounter(Metric name, long incrementBy) { }

}
