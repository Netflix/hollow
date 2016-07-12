package com.netflix.vms.transformer.publish.status;

public class WorkflowCycleStatusFuture implements CycleStatusFuture {
    
    private final PublishWorkflowStatusIndicator statusIndicator;
    private final long cycleVersion;
    
    public WorkflowCycleStatusFuture(PublishWorkflowStatusIndicator statusIndicator, long cycleVersion) {
        this.statusIndicator = statusIndicator;
        this.cycleVersion = cycleVersion;
    }
    
    public boolean awaitStatus() {
        return statusIndicator.awaitCycleStatus(cycleVersion);
    }

}
