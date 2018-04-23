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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AtlasTransformerMetricRecorder implements TransformerMetricRecorder {

    private static final Logger logger = LoggerFactory.getLogger(AtlasTransformerMetricRecorder.class);

    private final Timer duractionMetricTimer = new Timer(true);
    private Map<DurationMetric, DurationMeter> meterMap = new ConcurrentHashMap<>();

    @Inject
    public AtlasTransformerMetricRecorder() {
        runDurationMetricTimerTask();
    }

    @Override
    public void startTimer(DurationMetric metric) {
        DurationMeter meter = meterMap.get(metric);
        if (meter == null) {
            DoubleGauge gauge = Servo.getDoubleGauge(MonitorConfig.builder(metric.toString()).build());
            meter = new DurationMeter(metric, gauge);
            meterMap.put(metric, meter);
        }
        meter.start();
    }

    @Override
    public long stopTimer(DurationMetric metric) {
        DurationMeter meter = meterMap.get(metric);
        if (meter == null) throw new RuntimeException("Metric=" + metric + " has not been started");
        meter.stop();

        return meter.getDuration();
    }

    @Override
    public void resetTimer(DurationMetric metric) {
        DurationMeter meter = meterMap.get(metric);
        if (meter != null) meter.reset();
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

    private void runDurationMetricTimerTask() {
        duractionMetricTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (DurationMeter meter : meterMap.values()) {
                    meter.recordDuration();
                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(10));
    }

    private static class DurationMeter {
        private final DoubleGauge gauge;
        private final DurationMetric metric;

        private long start = 0;
        private long stoppedDuration = 0;

        public DurationMeter(DurationMetric metric, DoubleGauge gauge) {
            this.metric = metric;
            this.gauge = gauge;
        }

        public void start() { reset(); start = System.currentTimeMillis(); }
        public void stop() { stoppedDuration = System.currentTimeMillis() - start; }

        public long recordDuration() {
            long duration = getDuration();
            gauge.set((double) duration);
            return duration;
        }

        public void reset() {
            start = 0;
            stoppedDuration = 0;
            gauge.set(0D);
        }

        public long getDuration() {
            if (stoppedDuration != 0) return stoppedDuration;
            return start == 0 ? 0 : System.currentTimeMillis() - start;
        }

        @Override
        public String toString() {
            return metric + " - duration=" + getDuration();
        }
    }
}