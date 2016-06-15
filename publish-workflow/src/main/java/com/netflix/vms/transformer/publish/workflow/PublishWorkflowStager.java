package com.netflix.vms.transformer.publish.workflow;

public interface PublishWorkflowStager {
	
	void triggerPublish(long inputDataVersion, long previousCycleId, long currentCycleId);

}
