package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.PublishRegionProvider;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;

public class HermesCanaryRollbackJob extends CanaryRollbackJob {

	public HermesCanaryRollbackJob(String vip, long cycleVersion, long priorVersion, CanaryValidationJob validationJob) {
        super(vip, cycleVersion, priorVersion, HermesAnnounceUtil.getPreviouslyAnnouncedCanaryVersion(vip), validationJob);
    }

    @Override
    protected boolean executeJob() {
    	long destVersion = rollbackVersion;
        if(rollbackVersion == Long.MIN_VALUE || rollbackVersion == Long.MAX_VALUE)
        	destVersion = priorVersion;

        boolean allSucceeded = true;

        for(RegionEnum region : PublishRegionProvider.ALL_REGIONS) {
            if(!HermesAnnounceUtil.announce(vip, region, true, destVersion, Long.MIN_VALUE)) {
                allSucceeded = false;
            }
        }

        return allSucceeded;
    }

}
