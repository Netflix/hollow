package com.netflix.vms.transformer.atlas;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.DoubleGauge;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.suro.servo.Servo;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AtlasTransformerMetricRecorder implements TransformerMetricRecorder {

    private final ConcurrentHashMap<Metric, Counter> counters;
    private final ConcurrentHashMap<Metric, DoubleGauge> gauges;

    @Inject
    public AtlasTransformerMetricRecorder() {
        this.counters = new ConcurrentHashMap<>();
        this.gauges = new ConcurrentHashMap<>();
    }

    @Override
    public void recordMetric(Metric metric, double value) {
    	DoubleGauge gauge = gauges.get(metric);
        if(gauge == null) {
            gauge = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build());
            gauges.put(metric, gauge);
        }

        gauge.set(value);
    }

    @Override
    public void incrementCounter(Metric metric, long incrementBy) {
        Counter counter = counters.get(metric);
        if(counter == null) {
            counter = Servo.getCounter(metric.toString());
            counters.put(metric, counter);
        }
        counter.increment(incrementBy);
    }

}
