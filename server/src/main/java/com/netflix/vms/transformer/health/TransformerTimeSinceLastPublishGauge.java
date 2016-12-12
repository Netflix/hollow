package com.netflix.vms.transformer.health;

import static com.netflix.servo.annotations.DataSourceType.GAUGE;

import com.netflix.servo.annotations.Monitor;

public class TransformerTimeSinceLastPublishGauge {
    
    private long lastPublishTime = System.currentTimeMillis();
    
    public void notifyPublishSuccess() {
        this.lastPublishTime = System.currentTimeMillis();
    }
    
    @Monitor(name="vms.transformer.timeSinceLastPublish", type=GAUGE)
    public long getTimeSinceLastPublish() {
        return System.currentTimeMillis() - lastPublishTime;
    }

}
