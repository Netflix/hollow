package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryRollbackJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryValidationJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;

public class HollowBlobBeforeCanaryAnnounceJob extends BeforeCanaryAnnounceJob {
    private final PlaybackMonkeyTester dataTester;
	private Map<VideoCountryKey, Boolean> testResultVideoCountryKeys;
	private final ValidationVideoRanker videoRanker;

	public HollowBlobBeforeCanaryAnnounceJob(PublishWorkflowContext ctx, long newVersion, RegionEnum region, CircuitBreakerJob circuitBreakerJob,
			CanaryValidationJob previousCycleValidationJob, List<PublicationJob> newPublishJobs, CanaryRollbackJob previousCanaryRollBackJob, PlaybackMonkeyTester dataTester,
			ValidationVideoRanker videoRanker) {
		super(ctx, ctx.getVip(), newVersion, region, circuitBreakerJob, previousCycleValidationJob, newPublishJobs, previousCanaryRollBackJob);
		this.dataTester = dataTester;
		this.videoRanker = videoRanker;
		this.testResultVideoCountryKeys = Collections.emptyMap();
	}

	@Override
	protected boolean executeJob() {
		boolean success = true;
		if(region.equals(RegionEnum.US_EAST_1) && ctx.getConfig().isPlaybackMonkeyEnabled()){
			try {
				long now = System.currentTimeMillis();
				List<VideoCountryKey> mostValuableChangedVideos = videoRanker.getMostValuableChangedVideos(ctx);
				ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": got " + mostValuableChangedVideos.size() + " most valuable videos to test.");

				if(mostValuableChangedVideos.size() > 0)
					testResultVideoCountryKeys = dataTester.testVideoCountryKeysWithRetry(ctx.getLogger(), mostValuableChangedVideos, ctx.getConfig().getPlaybackMonkeyMaxRetriesPerTest());

				long timeTaken = System.currentTimeMillis() - now;
				float failedPercent = PlaybackMonkeyUtil.getFailedPercent(testResultVideoCountryKeys);
				if(testResultsTooNoise(failedPercent)) success = false;
				PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.FAILURE_PERCENT, failedPercent, vip, "before");
				PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.TIME_TAKEN, timeTaken, vip, "before");
				ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": completed with " + testResultVideoCountryKeys.size() + " video country pairs.");
				ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": Success of test: " + success);
				ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": Took time: " + timeTaken + "ms");
			} catch (Exception e) {
				success = false;
				ctx.getLogger().error("PlaybackMonkeyError", getJobName() + " failed with Exception", e);
			}
		}
		boolean finalResultAferPBMOverride = PlaybackMonkeyUtil.getFinalResultAferPBMOverride(success, ctx.getConfig());
		ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": success: " + success + " finalResultAfterPBMOverride: " + finalResultAferPBMOverride);
		return finalResultAferPBMOverride;
	}

	private boolean testResultsTooNoise(float failurePercent) {
		float noiseTolerance = ctx.getConfig().getPlaybackMonkeyNoiseTolerance();
		if(testResultVideoCountryKeys.size() > 0 && failurePercent > noiseTolerance){
		    ctx.getLogger().error("PlaybackMonkeyError", getJobName() + ": before test too noisy.  Failure percent: " + failurePercent + ". noise tolerance: " + noiseTolerance);
			return true;
		}
		return false;
	}

	@Override
	public Map<VideoCountryKey, Boolean> getTestResults() {
		return testResultVideoCountryKeys;
	}

	/**
	 * This method is used to clear results even as job is held on for debugging purpose.
	 */
	@Override
	public void clearResults(){
		testResultVideoCountryKeys = Collections.emptyMap();
	}
}
