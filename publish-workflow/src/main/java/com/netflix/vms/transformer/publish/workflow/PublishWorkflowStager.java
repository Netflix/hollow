package com.netflix.vms.transformer.publish.workflow;

import com.netflix.hollow.read.engine.HollowReadStateEngine;

public interface PublishWorkflowStager {
	
	void triggerPublish(long inputDataVersion, long previousCycleId, long currentCycleId);
	
	void notifyRestoredStateEngine(HollowReadStateEngine stateEngine);

}
