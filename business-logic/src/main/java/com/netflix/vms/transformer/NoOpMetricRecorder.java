package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;

public class NoOpMetricRecorder implements TransformerMetricRecorder {

    @Override
    public void recordMetric(Metric name, double value) { }

    @Override
    public void incrementCounter(Metric name, long incrementBy) { }

	@Override
	public void recordMetric(Metric metric, double value, String... keyValues) {}

    @Override
    public void incrementCounter(Metric name, long incrementBy, String... tagKeyValues) { }

}
