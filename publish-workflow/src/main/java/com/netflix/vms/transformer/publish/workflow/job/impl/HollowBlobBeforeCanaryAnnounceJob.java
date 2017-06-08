package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.common.publish.workflow.PublicationJob;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CircuitBreakerJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.ValuableVideoHolder.ValuableVideo;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HollowBlobBeforeCanaryAnnounceJob extends BeforeCanaryAnnounceJob {
    private final PlaybackMonkeyTester dataTester;
	private Map<VideoCountryKey, Boolean> testResultVideoCountryKeys;
	private final ValuableVideoHolder videoRanker;

	public HollowBlobBeforeCanaryAnnounceJob(PublishWorkflowContext ctx, long newVersion, RegionEnum region, CircuitBreakerJob circuitBreakerJob,
			List<PublicationJob> newPublishJobs, PlaybackMonkeyTester dataTester, ValuableVideoHolder videoRanker) {
		super(ctx, ctx.getVip(), newVersion, region, circuitBreakerJob, newPublishJobs);
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
				Set<ValuableVideo> mostValuableChangedVideos = videoRanker.getMostValuableChangedVideos(ctx, getCycleVersion());
				ctx.getLogger().info(PlaybackMonkey, "{}: got {} most valuable videos to test.", getJobName(), mostValuableChangedVideos.size());

				if(mostValuableChangedVideos.size() > 0)
					testResultVideoCountryKeys = dataTester.testVideoCountryKeysWithRetry(ctx.getLogger(), mostValuableChangedVideos, ctx.getConfig().getPlaybackMonkeyMaxRetriesPerTest());

				long timeTaken = System.currentTimeMillis() - now;
				float failedPercent = PlaybackMonkeyUtil.getFailedPercent(testResultVideoCountryKeys);
				if(testResultsTooNoise(failedPercent)) success = false;
				PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.TIME_TAKEN, timeTaken, vip, "before");
				ctx.getLogger().info(PlaybackMonkey, "{}: completed with {} video country pairs.", getJobName(), testResultVideoCountryKeys.size());
				ctx.getLogger().info(PlaybackMonkey, "{}: Success of test: {}", getJobName(), success);
				ctx.getLogger().info(PlaybackMonkey, "{}: Took time: {}ms", getJobName(), timeTaken);
			} catch (Exception e) {
				success = false;
				ctx.getLogger().error(PlaybackMonkey, "{}: failed with Exception", getJobName(), e);
			}
		}
		boolean finalResultAferPBMOverride = PlaybackMonkeyUtil.getFinalResultAferPBMOverride(success, ctx.getConfig());
        if (finalResultAferPBMOverride) {
            ctx.getLogger().info(PlaybackMonkey, "{}: success: {} finalResultAfterPBMOverride: {}", getJobName(), success, finalResultAferPBMOverride);
        } else {
            ctx.getLogger().error(PlaybackMonkey, "{}: success: {} finalResultAfterPBMOverride: {}", getJobName(), success, finalResultAferPBMOverride);
        }
		return finalResultAferPBMOverride;
	}

	private boolean testResultsTooNoise(float failurePercent) {
		float noiseTolerance = ctx.getConfig().getPlaybackMonkeyNoiseTolerance();
		if(testResultVideoCountryKeys.size() > 0 && failurePercent > noiseTolerance){
		    ctx.getLogger().error(PlaybackMonkey, "{}: before test too noisy.  Failure percent: {}. noise tolerance: {}", getJobName(), failurePercent, noiseTolerance);
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
