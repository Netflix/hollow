package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.AutoPinbackJob;

public class HermesAutoPinbackJob extends AutoPinbackJob {

    public HermesAutoPinbackJob(PublishWorkflowContext ctx, AnnounceJob announcement, long waitMillis, long cycleVersion) {
        super(ctx, announcement, waitMillis, cycleVersion);
    }

    @Override public boolean executeJob() {
        /*/// if we've previously triggered auto pinback, don't run this check
        if(VMSAutoPinBackEmergencyNotification.isPinbackTriggered()) {
            ERRCODELOGGER.logfWithExplicitCycleVersion(ErrorCode.AutoPinbackTriggered, String.valueOf(getCycleVersion()), "Auto Pinback was previously triggered.  Not testing for auto pinback");
            return false;
        }

        /// if errors are currently observed in some region, don't run auto pinback, as it is only likely to cause noise.
        if(!VMSAutoPinBackEmergencyNotification.isAllClear()) {
            ERRCODELOGGER.logfWithExplicitCycleVersion(ErrorCode.AutoPinbackTriggered, String.valueOf(getCycleVersion()), "Problems currently detected, but did not occur during auto pinback observation period.  Auto pinback is disabled for this cycle.");
            return false;
        }

        long endTime = System.currentTimeMillis() + waitMillis;

        /// poll until the observation period is over.
        while(System.currentTimeMillis() < endTime) {
            sleepForASecond();

            ErrorNotificationStatus errorStatus = VMSAutoPinBackEmergencyNotification.getErrorNotificationStatus(announcementJob.getRegion());
            if(errorStatus.isErrorDetected()) {
                Date notificationReceived = new Date(errorStatus.getPriorNotificationReceived());
                /// we see some issue in the region under test.  Is it the only region in which we observe issues?
                if(VMSAutoPinBackEmergencyNotification.isAllClearExceptForRegion(announcementJob.getRegion())) {
                    ///yes, this is where we trigger the pinback.
                    ERRCODELOGGER.logWithExplicitCycleVersion(ErrorCode.AutoPinbackTriggered, String.valueOf(getCycleVersion()), "Error notification was received at " + notificationReceived + ", if pinback was enabled, we would have pinned back");
                    VMSAutoPinBackEmergencyNotification.setPinbackTriggered(true);
                    return true;
                } else {
                    ///no, we can assume this is just noise.
                    ERRCODELOGGER.logWithExplicitCycleVersion(ErrorCode.AutoPinbackTriggered, String.valueOf(getCycleVersion()), "Error notification was received at " + notificationReceived + ", but all regions are having trouble.  Not likely related to VMS, therefore ignoring.");
                    return false;
                }
            }
        }*/

        return false;
    }

    @SuppressWarnings("unused")
    private void sleepForASecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
