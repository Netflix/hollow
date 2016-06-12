package com.netflix.vms.transformer.publish.workflow;

public interface PublishWorkflowStager {
	
	void triggerPublish(long previousCycleId, long currentCycleId);

}
