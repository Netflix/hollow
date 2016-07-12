package com.netflix.vms.transformer.publish.status;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutivePublishFailures;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import java.util.HashSet;
import java.util.Set;

public class PublishWorkflowStatusIndicator {
    
    private final TransformerMetricRecorder metricRecorder;
    private final Set<Long> loggedCycleMetrics = new HashSet<Long>();
    private final Set<Long> failedCycles = new HashSet<Long>();
    
    private int consecutivePublicationFailures = 0;
    
    public PublishWorkflowStatusIndicator(TransformerMetricRecorder metricRecorder) {
        this.metricRecorder = metricRecorder;
    }
    
    public synchronized void markSuccess(long cycleVersion) {
        if(!loggedCycleMetrics.contains(cycleVersion)) {
            loggedCycleMetrics.add(cycleVersion);
            consecutivePublicationFailures = 0;
            metricRecorder.recordMetric(ConsecutivePublishFailures, consecutivePublicationFailures);
            notifyAll();
        }
    }

    public synchronized void markFailure(long cycleVersion) {
        if(!loggedCycleMetrics.contains(cycleVersion)) {
            loggedCycleMetrics.add(cycleVersion);
            failedCycles.add(cycleVersion);
            metricRecorder.recordMetric(ConsecutivePublishFailures, ++consecutivePublicationFailures);
            notifyAll();
        }
    }
    
    public synchronized boolean awaitCycleStatus(long version) {
        while(!loggedCycleMetrics.contains(version)) {
            try {
                wait();
            } catch(InterruptedException ignore) { }
        }
        
        return !failedCycles.contains(version);
    }
}
