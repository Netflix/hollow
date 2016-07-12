package com.netflix.vms.transformer.publish.workflow;

import com.netflix.vms.transformer.publish.status.CycleStatusFuture;

import com.netflix.hollow.read.engine.HollowReadStateEngine;

public interface PublishWorkflowStager {
	
	CycleStatusFuture triggerPublish(long inputDataVersion, long previousCycleId, long currentCycleId);
	
	void notifyRestoredStateEngine(HollowReadStateEngine stateEngine);

}
