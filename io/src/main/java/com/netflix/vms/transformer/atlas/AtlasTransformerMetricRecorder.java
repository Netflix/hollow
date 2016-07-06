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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AtlasTransformerMetricRecorder implements TransformerMetricRecorder {

    private static final Logger logger = LoggerFactory.getLogger(AtlasTransformerMetricRecorder.class);
    
    @Inject
    public AtlasTransformerMetricRecorder() { }

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
    public void recordMetric(Metric metric, double value, String... tagKeyValues) {
        try {
        	ArrayList<Tag> listOfTags = getAsTagList(tagKeyValues);
        	DoubleGauge gauge  = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build().withAdditionalTags(new BasicTagList(listOfTags)));
            gauge.set(value);
        } catch(Throwable th) {
            logger.error("Failed to record metric: " + metric + "(value " + value + ")", th);
        }
    }

    @Override
    public void incrementCounter(Metric metric, long incrementBy) {
        try {
            Counter counter = Servo.getCounter(metric.toString());
            counter.increment(incrementBy);
        } catch(Throwable th) {
            logger.error("Failed to increment metric: " + metric + "(incrementBy " + incrementBy + ")", th);
        }
    }
    
    @Override
    public void incrementCounter(Metric metric, long incrementBy, String... tagKeyValues) {
        try {
            Counter counter = Servo.getCounter(metric.toString(), tagKeyValues);
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
