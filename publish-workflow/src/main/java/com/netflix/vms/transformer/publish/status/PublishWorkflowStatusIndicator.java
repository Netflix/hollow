package com.netflix.vms.transformer.publish.status;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutivePublishFailures;

import java.util.concurrent.atomic.AtomicLong;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;

public class PublishWorkflowStatusIndicator {
    
    private final TransformerMetricRecorder metricRecorder;
    private final AtomicLong priorFailingCycleVersion = new AtomicLong(0);
    
    private int consecutivePublicationFailures = 0;
    
    public PublishWorkflowStatusIndicator(TransformerMetricRecorder metricRecorder) {
        this.metricRecorder = metricRecorder;
    }
    
    public void markSuccess() {
        consecutivePublicationFailures = 0;
        metricRecorder.recordMetric(ConsecutivePublishFailures, consecutivePublicationFailures);
    }

    public void markFailure(long cycleVersion) {
        long priorVersion = priorFailingCycleVersion.get();
        if(priorVersion != cycleVersion) {
            if(priorFailingCycleVersion.compareAndSet(priorVersion, cycleVersion)) {
                metricRecorder.recordMetric(ConsecutivePublishFailures, ++consecutivePublicationFailures);
                return;
            }
        }
    }
}
