package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.CANARY_VALIDATION;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.DataCanary;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.FastProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.logmessage.PbmsMessage;
import com.netflix.vms.transformer.publish.workflow.logmessage.ViewShareMessage;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.VMSDataCanaryResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CassandraCanaryValidationJob extends CanaryValidationJob {

    /// required canaries value can be mix of app or app|region e.g. "api,nccp|us-east-1,nccp|eu-west-1,gps"
    private static final String NO_CANARIES_VALUE = "none";
    private static final FastProperty.StringProperty REQUIRED_CANARIES = new FastProperty.StringProperty("com.netflix.vms.server.canary.apps", NO_CANARIES_VALUE);
    private static final long CANARY_TIMEOUT_MS = 300000; /// 5 minutes

	private final TransformerCassandraColumnFamilyHelper cassandraHelper;
    private final Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs;
    private final Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs;
    private final HollowBlobDataProvider hollowBlobDataProvider;
	private final ValuableVideoHolder validationVideoHolder;

    public CassandraCanaryValidationJob(PublishWorkflowContext ctx, long cycleVersion,
			Map<RegionEnum, BeforeCanaryAnnounceJob> beforeCanaryAnnounceJobs,
            Map<RegionEnum, AfterCanaryAnnounceJob> afterCanaryAnnounceJobs,
			HollowBlobDataProvider hollowBlobDataProvider,
			ValuableVideoHolder videoRanker) {
        super(ctx, ctx.getVip(), cycleVersion, afterCanaryAnnounceJobs);
        this.cassandraHelper = ctx.getCassandraHelper().getColumnFamilyHelper(CANARY_VALIDATION);
		this.beforeCanaryAnnounceJobs = beforeCanaryAnnounceJobs;
		this.afterCanaryAnnounceJobs = afterCanaryAnnounceJobs;
		this.hollowBlobDataProvider = hollowBlobDataProvider;
		this.validationVideoHolder = videoRanker;
    }

    @Override
	public boolean executeJob() {
    	return executeJob(hollowBlobDataProvider.getStateEngine());
    }

	public boolean executeJob(HollowReadStateEngine readStateEngine) {
		return getCanaryValidationResult() && getPlayBackMonkeyResult(readStateEngine);
	}

    private boolean getPlayBackMonkeyResult(HollowReadStateEngine readStateEngine) {
        boolean pbmSuccess = true;
        if (ctx.getConfig().isPlaybackMonkeyEnabled()){
	        try {
	            RegionEnum region = RegionEnum.US_EAST_1;
	            final BeforeCanaryAnnounceJob beforeCanaryAnnounceJob = beforeCanaryAnnounceJobs.get(region);
	            final AfterCanaryAnnounceJob afterCanaryAnnounceJob = afterCanaryAnnounceJobs.get(region);
	            final Map<VideoCountryKey, Boolean> befTestResults = beforeCanaryAnnounceJob.getTestResults();
	            final Map<VideoCountryKey, Boolean> aftTestResults = afterCanaryAnnounceJob.getTestResults();
	
	            List<VideoCountryKey> failedIDs = new ArrayList<>();
	            if (bothResultsAreNonEmpty(befTestResults, aftTestResults)) {
	            	List<VideoCountryKey> failedInBothBeforeAfter = new ArrayList<>();
	                for (final VideoCountryKey videoCountry: befTestResults.keySet()) {
	                    final Boolean afterTestSuccess = aftTestResults.get(videoCountry);
	                    final Boolean beforeTestSuccess = befTestResults.get(videoCountry);
	
	                    // If before passed and after failed, then fail the data version.
	                    if (videoFailedWithNewDataButPassedWithOld(beforeTestSuccess, afterTestSuccess)) {
	                        failedIDs.add(videoCountry);
	                    }
	                    // If before passed and after passed or if before failed and after passed, the data is good.
	                    // If before failed and after failed: if only a few videos are this way then with high confidence it's not data.
	                    // If all videos are failing before and after then there is a potential data issue but playback monkey cannot give signal due
	                    // its own environment issues. To handle this case we fail BeforeTest canary thus failing cycle if majority of videos are failing playback.
	                    if(videoFailedWithBothOldAndNew(beforeTestSuccess, afterTestSuccess)) // Collecting just for visibility
	                    	failedInBothBeforeAfter.add(videoCountry);
	                }
	                if(!failedInBothBeforeAfter.isEmpty())
	                    ctx.getLogger().warn(PlaybackMonkey, new PbmsMessage(true,
	                            "IDs failed both before and after tests (added for visibility and these do not break cycles)", failedInBothBeforeAfter));
	            } else {
	                pbmSuccess = false;
	            }
	
	            // Clean-up to release the results as the before and after jobs are held onto by the history.
	            beforeCanaryAnnounceJob.clearResults();
	            afterCanaryAnnounceJob.clearResults();
	            validationVideoHolder.onCycleComplete(getCycleVersion(), failedIDs);
	
	            if(!failedIDs.isEmpty()){
	                float missingViewShareThreshold = ctx.getConfig().getPlaybackmonkeyMissingViewShareThreshold();
	                Map<String, Float> viewShareOfFailedVideos = validationVideoHolder.getViewShareOfVideos(readStateEngine, failedIDs);
	                for(String countryId: viewShareOfFailedVideos.keySet()){
	                	boolean pbmSuccessForThisCountry = true;
	                    Float missingViewShareForCountry = viewShareOfFailedVideos.get(countryId);
	                    if(missingViewShareForCountry != null && Float.compare(missingViewShareForCountry, missingViewShareThreshold) > 0){
	                        pbmSuccess = false;
	                        pbmSuccessForThisCountry = false;
	                    }
	                    logMissingViewShare(pbmSuccessForThisCountry, missingViewShareThreshold, countryId, missingViewShareForCountry, failedIDs);
	                }
	            }
	            logFailedIDs(pbmSuccess, befTestResults, failedIDs);
	        } catch(Exception ex) {
	            ctx.getLogger().error(PlaybackMonkey, "Error validating PBM results.", ex);
	            pbmSuccess = false;
	        }
        }
        boolean finalResultAferPBMOverride = PlaybackMonkeyUtil.getFinalResultAferPBMOverride(pbmSuccess, ctx.getConfig());
        // Send success or failure result from here. As this is the final PBM step.
        PlaybackMonkeyUtil.sendPBMFailureMetric(ctx, finalResultAferPBMOverride, vip);
		return finalResultAferPBMOverride;
    }


	private void logFailedIDs(boolean pbmSuccess, Map<VideoCountryKey, Boolean> befTestResults, List<VideoCountryKey> failedIDs) {
		if(!failedIDs.isEmpty()){
			if(!pbmSuccess)
                ctx.getLogger().error(PlaybackMonkey, getFailureReason(befTestResults, failedIDs));
			else
                ctx.getLogger().warn(PlaybackMonkey, new PbmsMessage(true, "failedIds not empty", failedIDs));
		}
	}

	private void logMissingViewShare(boolean pbmSuccessForThisCountry, float missingViewShareThreshold, String countryId,
            Float missingViewShareForCountry, List<VideoCountryKey> failedIDs) {
		if(!pbmSuccessForThisCountry)
            ctx.getLogger().error(PlaybackMonkey, new ViewShareMessage("PBM", countryId, failedIDs, missingViewShareForCountry, missingViewShareThreshold));
		else if(missingViewShareForCountry != null && Float.compare(missingViewShareForCountry, 0f) > 0)
            ctx.getLogger().warn(PlaybackMonkey, new ViewShareMessage("PBM", countryId, failedIDs, missingViewShareForCountry, missingViewShareThreshold));
		ctx.getMetricRecorder().recordMetric(Metric.PBMFailuresMissingViewShare, missingViewShareForCountry, "country",countryId);
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
	
	private boolean videoFailedWithBothOldAndNew(
			Boolean beforeTestSuccess, Boolean afterTestSuccess) {
		return(afterTestSuccess != null && (!beforeTestSuccess && !afterTestSuccess));
	}
			

	private boolean bothResultsAreNonEmpty(Map<VideoCountryKey, Boolean> befTestResults,
			Map<VideoCountryKey, Boolean> aftTestResults) {
		if(befTestResults != null && aftTestResults != null)
			return(!befTestResults.isEmpty() && !aftTestResults.isEmpty());
		return false;
	}

    private PbmsMessage getFailureReason(final Map<VideoCountryKey, Boolean> befTestResults, List<VideoCountryKey> failedIDs) {
		if(failedIDs.size() > 0){
            return new PbmsMessage(false, "ids failed with new data but passed with old", failedIDs);
        } else {
            return (befTestResults == null) ? new PbmsMessage(false, "before test results were not available")
                    : new PbmsMessage(false, "after test results were not available");
        }
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
