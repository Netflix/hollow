package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.servo.monitor.DynamicGauge;
import com.netflix.servo.tag.BasicTag;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.Tag;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

public class PlaybackMonkeyUtil {
	static final String TIME_TAKEN = "vms.hollow.playbackmonkey.timeTakenInMillis";
	static final String FAILURE_PERCENT = "vms.hollow.playbackmonkey.failurePercent";

	static boolean getFinalResultAferPBMOverride(boolean success, TransformerConfig config) {
		return success || !config.shouldFailCycleOnPlaybackMonkeyFailure();
	}

	static void logResultsToAtlas(String keyName, float value, String vip, String tagPositionOfTest){
		if(keyName == null || Float.compare(0f, value) > 0)
			return;
		BasicTagList tagList = new BasicTagList(Arrays.<Tag> asList(new BasicTag("vip", vip), new BasicTag("workflowPosition", tagPositionOfTest)));
        DynamicGauge.set(keyName, tagList, value);
	}

	static float getFailedPercent(Map<VideoCountryKey, Boolean> testResultVideoCountryKeys) {
		if(testResultVideoCountryKeys == null || testResultVideoCountryKeys.isEmpty())
			return 0;

		int failedVideos = 0;
		for(final Entry<VideoCountryKey, Boolean> entry: testResultVideoCountryKeys.entrySet()){
			if(!entry.getValue())
				failedVideos++;
		}
		float failedPercent = (failedVideos/testResultVideoCountryKeys.size())*100;
		return failedPercent;
	}

	/**
	 * Atlas metric: Report 1 for failure. Report 0 for success.
     * No data will be reported (equivalent to 0 in Atlas) if PBM validation job did not run (ex: topN CB fails and PBM does not run).
	 * @param ctx
	 * @param success
	 * @param vip
	 */
	
	static void sendPBMFailureMetric(PublishWorkflowContext ctx, boolean success, String vip){
    	// Atlas metric: Report 1 for failure. Report 0 for success.
    	// No data will be reported (equivalent to 0 in Atlas) if PBM validation job did not run (ex: topN CB fails and PBM does not run).
    	int count = (success)? 0 : 1;
   		ctx.getMetricRecorder().recordMetric(Metric.PBMFailureCount, count, "vip", vip);
	}
}
