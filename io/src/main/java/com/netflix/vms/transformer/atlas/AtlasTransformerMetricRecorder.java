package com.netflix.vms.transformer.atlas;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.DoubleGauge;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.tag.BasicTag;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.Tag;
import com.netflix.suro.servo.Servo;
import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class AtlasTransformerMetricRecorder implements TransformerMetricRecorder {

    private final ConcurrentHashMap<Metric, Counter> counters;

    @Inject
    public AtlasTransformerMetricRecorder() {
        this.counters = new ConcurrentHashMap<>();
    }

    @Override
    public void recordMetric(Metric metric, double value) {
    	DoubleGauge gauge = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build());
        gauge.set(value);
    }
    
    @Override
    public void recordMetric(Metric metric, double value, String... keyValues) {
    	ArrayList<Tag> listOfTags = getAsTagList(keyValues);
    	DoubleGauge gauge  = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build().withAdditionalTags(new BasicTagList(listOfTags)));
        gauge.set(value);
    }

	private ArrayList<Tag> getAsTagList(String... keyValues) {
		ArrayList<Tag> listOfTags = new ArrayList<>();
    	for(int i = 0; i < keyValues.length; i=i+2){
    		listOfTags.add(new BasicTag(keyValues[i], keyValues[i+1]));
    	}
		return listOfTags;
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
