package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.BeforeCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.DataTester;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HollowBlobAfterCanaryAnnounceJob extends AfterCanaryAnnounceJob {

	private static final long MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION = 300000; // 5 minute

	private final PublishWorkflowContext ctx;
	private final DataTester dataTester;
	private Map<VideoCountryKey, Boolean> testResultVideoCountryKeys;
	private final ValidationVideoRanker videoRanker;

	public HollowBlobAfterCanaryAnnounceJob(PublishWorkflowContext ctx, long newVersion,
			RegionEnum region, BeforeCanaryAnnounceJob beforeCanaryAnnounceJob,
			CanaryAnnounceJob canaryAnnounceJob, DataTester dataTester,
			ValidationVideoRanker videoRanker) {
		super(ctx.getVip(), newVersion, region, beforeCanaryAnnounceJob, canaryAnnounceJob);
		this.ctx = ctx;
		this.dataTester = dataTester;
		this.testResultVideoCountryKeys = Collections.emptyMap();
		this.videoRanker = videoRanker;
	}

	@Override
	protected boolean executeJob() {
		boolean success = true;
		if(region.equals(RegionEnum.US_EAST_1) && ctx.getConfig().isPlaybackMonkeyEnabled()){
			final long now = System.currentTimeMillis();
			try {
				if(isPlaybackMonkeyInstancesReadyForTest()){
						List<VideoCountryKey> mostValuableChangedVideos = videoRanker.getMostValuableChangedVideos(ctx);
						ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": got " + mostValuableChangedVideos.size() + " most valuable videos to test.");

						testResultVideoCountryKeys = dataTester.testVideoCountryKeysWithRetry(mostValuableChangedVideos, ctx.getConfig().getPlaybackMonkeyMaxRetriesPerTest());

						long timeTaken = System.currentTimeMillis()-now;
						PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.FAILURE_PERCENT, PlaybackMonkeyUtil.getFailedPercent(testResultVideoCountryKeys), vip, "after");
						PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.TIME_TAKEN, timeTaken, vip, "after");
						ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": completed with " + testResultVideoCountryKeys.size() + " video country pairs");
						ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": success of test: " + success);
						ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": time taken " + timeTaken + "ms");

				} else {// For some reason instances did not get to desired version in timeout.
					success = false;
				}
			} catch (Exception e) {
			    ctx.getLogger().error("PlaybackMonkeyError", getJobName() + " failed with Exception", e);
				success = false;
			}
		}
		boolean finalResultAferPBMOverride = PlaybackMonkeyUtil.getFinalResultAferPBMOverride(success, ctx.getConfig());
		ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() +": success: " + success + ". finalResultAfterPBMOverride: " + finalResultAferPBMOverride);
		return finalResultAferPBMOverride;
	}

	private boolean isPlaybackMonkeyInstancesReadyForTest() throws Exception {
        final long stopCheckingTime = System.currentTimeMillis() + MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION;
        final String desiredVersion = String.valueOf(getCycleVersion());
        List<String> instancesNotInDesiredVersion = null;
        ctx.getLogger().info("PlaybackMonkeyInfo", getJobName() + ": Waiting for pbm instances to get to version " + desiredVersion);
        while(System.currentTimeMillis() < stopCheckingTime) {
			try {
				instancesNotInDesiredVersion = parseStringToListExcludeEmptyValues(dataTester.getInstanceInPlayBackMonkeyStack(), ",");
				final Map<String, String> columns = ctx.getCanaryResultsCassandraHelper().getColumns(ctx.getCanaryResultsCassandraHelper().vipSpecificKey(vip, desiredVersion));
				for(final String instance: columns.keySet()){
					instancesNotInDesiredVersion.remove(instance.trim());
				}
				if(instancesNotInDesiredVersion.isEmpty())
					return true;
			} catch (final NotFoundException ignore) {
            } catch (final ConnectionException e) {
                ctx.getLogger().warn("HollowValidationDebug", "ConnectionException in " + getJobName() + " " + e.getMessage());
            }
			try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) { }
        }
        // Giving up after time-out
        final String instancesNotInDesiredVersionStr = (instancesNotInDesiredVersion == null)?"":instancesNotInDesiredVersion.toString();
        String msg = "PBM instances: "+instancesNotInDesiredVersionStr+" did not get to desired version "+desiredVersion+" with in time out (ms) "+MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION;
        ctx.getLogger().error("PlaybackMonkeyError", msg);
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

    private static List<String> parseStringToListExcludeEmptyValues(final String value, final String delim){
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
