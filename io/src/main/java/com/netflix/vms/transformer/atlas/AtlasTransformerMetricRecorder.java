package com.netflix.vms.transformer.atlas;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
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

    private static final Logger logger = LoggerFactory.getLogger(AtlasTransformerMetricRecorder.class);
    
    private final ConcurrentHashMap<Metric, Counter> counters;

    @Inject
    public AtlasTransformerMetricRecorder() {
        this.counters = new ConcurrentHashMap<>();
    }

    @Override
    public void recordMetric(Metric metric, double value) {
        try {
        	DoubleGauge gauge = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build());
            gauge.set(value);
        } catch(Throwable th) {
            logger.error("Failed to record metric: " + metric + "(value " + value + ")", th);
        }
    }
    
    @Override
    public void recordMetric(Metric metric, double value, String... keyValues) {
        try {
        	ArrayList<Tag> listOfTags = getAsTagList(keyValues);
        	DoubleGauge gauge  = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build().withAdditionalTags(new BasicTagList(listOfTags)));
            gauge.set(value);
        } catch(Throwable th) {
            logger.error("Failed to record metric: " + metric + "(value " + value + ")", th);
        }
    }

    @Override
    public void incrementCounter(Metric metric, long incrementBy) {
        try {
            Counter counter = counters.get(metric);
            if(counter == null) {
                counter = Servo.getCounter(metric.toString());
                counters.put(metric, counter);
            }
            counter.increment(incrementBy);
        } catch(Throwable th) {
            logger.error("Failed to increment metric: " + metric + "(incrementBy " + incrementBy + ")", th);
        }
    }
    
    private ArrayList<Tag> getAsTagList(String... keyValues) {
        ArrayList<Tag> listOfTags = new ArrayList<>();
        for(int i = 0; i < keyValues.length; i=i+2){
            listOfTags.add(new BasicTag(keyValues[i], keyValues[i+1]));
        }
        return listOfTags;
    }

}
