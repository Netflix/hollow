package com.netflix.vms.transformer.publish.status;

import static com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric.ConsecutivePublishFailures;

import com.netflix.vms.transformer.common.TransformerMetricRecorder;
import java.util.HashSet;
import java.util.Set;

public class PublishWorkflowStatusIndicator {
    
    private final TransformerMetricRecorder metricRecorder;
    private final Set<Long> loggedCycleMetrics = new HashSet<Long>();
    
    private int consecutivePublicationFailures = 0;
    
    public PublishWorkflowStatusIndicator(TransformerMetricRecorder metricRecorder) {
        this.metricRecorder = metricRecorder;
    }
    
    public synchronized void markSuccess(long cycleVersion) {
        if(!loggedCycleMetrics.contains(cycleVersion)) {
            loggedCycleMetrics.add(cycleVersion);
            consecutivePublicationFailures = 0;
            metricRecorder.recordMetric(ConsecutivePublishFailures, consecutivePublicationFailures);
        }
    }

    public synchronized void markFailure(long cycleVersion) {
        if(!loggedCycleMetrics.contains(cycleVersion)) {
            metricRecorder.recordMetric(ConsecutivePublishFailures, ++consecutivePublicationFailures);
        }
    }
}
