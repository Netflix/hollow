package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.DataCanary;

import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.FastProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.VMSDataCanaryResult;

public class CassandraCanaryValidationJob extends CanaryValidationJob {

    /// required canaries value can be mix of app or app|region e.g. "api,nccp|us-east-1,nccp|eu-west-1,gps"
    private static final String NO_CANARIES_VALUE = "none";
    private static final FastProperty.StringProperty REQUIRED_CANARIES = new FastProperty.StringProperty("com.netflix.vms.server.canary.apps", NO_CANARIES_VALUE);
    private static final long CANARY_TIMEOUT_MS = 300000; /// 5 minutes

    private final Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs;
    private final Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs;
	private final ValuableVideoHolder validationVideoHolder;
	private final TransformerCassandraColumnFamilyHelper cassandraHelper;

    public CassandraCanaryValidationJob(PublishWorkflowContext ctx, long cycleVersion, Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs,
            Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs, ValuableVideoHolder videoRanker) {
        super(ctx, ctx.getVip(), cycleVersion, beforeCanaryAnnounceJobs, afterCanaryAnnounceJobs);
        this.cassandraHelper = ctx.getCassandraHelper().getColumnFamilyHelper("canary_validation", "canary_results");
		this.validationVideoHolder = videoRanker;
        this.beforeCanaryAnnounceJobs = beforeCanaryAnnounceJobs;
        this.afterCanaryAnnounceJobs = afterCanaryAnnounceJobs;
    }

    @Override
    protected boolean executeJob() {
        return getCanaryValidationResult() && getPlayBackMonkeyResult();
    }

    private boolean getPlayBackMonkeyResult() {
        boolean pbmSuccess = true;
        if (!ctx.getConfig().isPlaybackMonkeyEnabled())
            return pbmSuccess;
        try {
            RegionEnum region = RegionEnum.US_EAST_1;
            final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob = beforeCanaryAnnounceJobs.get(region);
            final AfterCanaryAnnounceJob afterCanaryAnnounceJob = afterCanaryAnnounceJobs.get(region);
            final Map<VideoCountryKey, Boolean> befTestResults = beforeCanaryAnnounceJob.getTestResults();
            final Map<VideoCountryKey, Boolean> aftTestResults = afterCanaryAnnounceJob.getTestResults();

            List<VideoCountryKey> failedIDs = new ArrayList<>();
            if (bothResultsAreNonEmpty(befTestResults, aftTestResults)) {
                for (final VideoCountryKey videoCountry: befTestResults.keySet()) {
                    final Boolean afterTestSuccess = aftTestResults.get(videoCountry);
                    final Boolean beforeTestSuccess = befTestResults.get(videoCountry);

                    // If before passed and after failed, then fail the data version.
                    if (videoFailedWithNewDataButPassedWithOld(beforeTestSuccess, afterTestSuccess)) {
                        pbmSuccess = false;
                        failedIDs.add(videoCountry);
                    }
                    // If before passed and after passed or if before failed and after passed, the data is good.
                    // If before failed and after failed: if only a few videos are this way then with high confidence it's not data.
                    // If all videos are failing before and after then there is a potential data issue but playback monkey cannot give signal due
                    // its own environment issues. To handle this case we fail BeforeTest canary thus failing cycle if majority of videos are failing playback.
                }
            } else {
                pbmSuccess = false;
            }

            // Clean-up to release the results as the before and after jobs are held onto by the history.
            beforeCanaryAnnounceJob.clearResults();
            afterCanaryAnnounceJob.clearResults();
            validationVideoHolder.onCycleComplete(getCycleVersion(), failedIDs);
            
			if(!failedIDs.isEmpty()){
			    float missingViewShareThreshold = ctx.getConfig().getPlaybackmonkeyMissingViewShareThreshold();
			    Map<String, Float> viewShareOfFailedVideos = validationVideoHolder.getViewShareOfVideos(failedIDs);
			    for(String countryId: viewShareOfFailedVideos.keySet()){
			        Float missingViewShareForCountry = viewShareOfFailedVideos.get(countryId);
                                if(missingViewShareForCountry != null && 
			                Float.compare(missingViewShareForCountry, missingViewShareThreshold) > 0){
                                	pbmSuccess = false;
                                }
			        ctx.getMetricRecorder().recordMetric(Metric.PBMFailuresMissingViewShare, missingViewShareForCountry, "country",countryId);
			    }
			}
            
            if (!pbmSuccess) {
                // Log which results failed
                ctx.getLogger().error(PlaybackMonkey, "PBM validation: for region {} failed. {}",
                        region.name(), getFailureReason(befTestResults, failedIDs));
            } else {
                ctx.getLogger().info(PlaybackMonkey, "PBM validation {} region completed. Success validation: {}",
                        region.name(), pbmSuccess);
            }
        } catch(Exception ex) {
            ctx.getLogger().error(PlaybackMonkey, "Error validating PBM results.", ex);
            pbmSuccess = false;
        }
        return PlaybackMonkeyUtil.getFinalResultAferPBMOverride(pbmSuccess, ctx.getConfig());
    }

	private boolean videoFailedWithNewDataButPassedWithOld(
			Boolean beforeTestSuccess, Boolean afterTestSuccess) {
		if(afterTestSuccess == null){
			// Indicates after test was not run with this video country key. This is not expected to happen. But need to ensure it.
			// Before and after comparison cannot be made.
			return false;
		}
		return (!afterTestSuccess && beforeTestSuccess);
	}

	private boolean bothResultsAreNonEmpty(Map<VideoCountryKey, Boolean> befTestResults,
			Map<VideoCountryKey, Boolean> aftTestResults) {
		if(befTestResults != null && aftTestResults != null)
			return(!befTestResults.isEmpty() && !aftTestResults.isEmpty());
		return false;
	}

	private String getFailureReason(final Map<VideoCountryKey, Boolean> befTestResults, List<VideoCountryKey> failedIDs) {
		String msg = "";
		if(failedIDs.size() > 0){
			StringBuilder idStr = new StringBuilder();
			for(int i = 0; i < failedIDs.size(); i++){
				idStr.append(failedIDs.get(i).toShortString()).append(",");
			}
			msg = failedIDs.size()+" ids failed with new data but passed with old. Failed ids: "+idStr;
		} else
			msg = ((befTestResults == null)?"Before test results were not available.":"After test results were not available.");
		return msg;
	}

	private boolean getCanaryValidationResult() {
		String requiredCanaryAppsStr = REQUIRED_CANARIES.get();
		if(NO_CANARIES_VALUE.equals(requiredCanaryAppsStr))
            return true;


        final long stopCheckingTime = System.currentTimeMillis() + CANARY_TIMEOUT_MS;
        List<String> remainingCanaryAppList = null;
        while(System.currentTimeMillis() < stopCheckingTime) {
            try {
                final Map<String, String> columns = cassandraHelper.getColumns(cassandraHelper.vipSpecificKey(vip, String.valueOf(getCycleVersion())));
				remainingCanaryAppList = parseStringToListExcludeEmptyValues(requiredCanaryAppsStr, ",");

                for(final Map.Entry<String, String> column : columns.entrySet()) {
                    final VMSDataCanaryResult result = VMSDataCanaryResult.fromString(column.getValue());

                    if(!result.allWereSuccessful()) {
                        ctx.getLogger().error(DataCanary, "Failed due to 1 data canary app. Not validating other data canaries. Will fail the cycle. Details: {}", result);
                        return false;
                    } else {
                        remainingCanaryAppList.remove(result.getAppId());
                        remainingCanaryAppList.remove(result.getAppId() + "|" + result.getAppId());
                    }
                }

                if(remainingCanaryAppList.isEmpty()){
                    ctx.getLogger().info(DataCanary, "All data canary validations successful");
                    return true;
                }

            } catch (final NotFoundException ignore) {
            } catch (final ConnectionException e) {
                ctx.getLogger().warn(DataCanary, "Caught ConnectionException but continuing with validation. Will not result in poisoned blob: " + e.getMessage(), e);
            }

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) { }
        }

        String failedDataCanaries = (remainingCanaryAppList == null)?"":remainingCanaryAppList.toString();
        ctx.getLogger().error(DataCanary, "Data canary validation failed. Will fail the cycle. Following apps failed to return result in timeout of {}  seconds. {}", CANARY_TIMEOUT_MS / 1000, failedDataCanaries);
        return false;
	}

    private List<String> parseStringToListExcludeEmptyValues(final String value, final String delim){
        if(value == null || value.trim().isEmpty())
            return Collections.emptyList();

        final String[] valueArr = value.split(delim);
        ArrayList<String> valueList = new ArrayList<String>(valueArr.length);
        for(String v: valueArr){
            if(v == null || v.trim().isEmpty())
                continue;
            valueList.add(v.trim());
        }
        return valueList;
    }

}
