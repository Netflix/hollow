package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.publish.status.CycleStatusFuture;

public interface PublishWorkflowStager {
    
    CycleStatusFuture triggerPublish(long inputDataVersion, long previousCycleId, long currentCycleId);
    
    void notifyRestoredStateEngine(HollowReadStateEngine stateEngine, HollowReadStateEngine nostreamsRestoredState);
    
    HollowReadStateEngine getCurrentReadStateEngine();

    PublishWorkflowContext getContext();
}
