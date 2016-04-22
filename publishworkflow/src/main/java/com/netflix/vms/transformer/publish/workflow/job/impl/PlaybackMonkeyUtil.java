package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.PublishWorkflowConfig;

import com.netflix.servo.monitor.DynamicGauge;
import com.netflix.servo.tag.BasicTag;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.Tag;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

public class PlaybackMonkeyUtil {
	static final String TIME_TAKEN = "vms.hollow.playbackmonkey.timeTakenInMillis";
	static final String FAILURE_PERCENT = "vms.hollow.playbackmonkey.failurePercent";

	static boolean getFinalResultAferPBMOverride(boolean success, PublishWorkflowConfig config) {
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

}
