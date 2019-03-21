package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.CANARY_VALIDATION;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.AfterCanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CanaryAnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.impl.ValuableVideoHolder.ValuableVideo;
import com.netflix.vms.transformer.publish.workflow.playbackmonkey.PlaybackMonkeyTester;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HollowBlobAfterCanaryAnnounceJob extends AfterCanaryAnnounceJob {

	private static final long MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION = 300000; // 5 minute

	private final TransformerCassandraColumnFamilyHelper cassandraHelper;
	private final PlaybackMonkeyTester dataTester;
	private Map<VideoCountryKey, Boolean> testResultVideoCountryKeys;
	private final HollowBlobDataProvider hollowBlobDataProvider;
	private final ValuableVideoHolder videoRanker;

	public HollowBlobAfterCanaryAnnounceJob(PublishWorkflowContext ctx, String vip, long newVersion,
			CanaryAnnounceJob canaryAnnounceJob,
			PlaybackMonkeyTester dataTester,
			HollowBlobDataProvider hollowBlobDataProvider,
			ValuableVideoHolder videoRanker) {
		super(ctx, vip, newVersion, canaryAnnounceJob);
		this.cassandraHelper = ctx.getCassandraHelper().getColumnFamilyHelper(CANARY_VALIDATION);
		this.dataTester = dataTester;
		this.testResultVideoCountryKeys = Collections.emptyMap();
		this.hollowBlobDataProvider = hollowBlobDataProvider;
		this.videoRanker = videoRanker;
	}

	@Override
	public boolean executeJob() {
		return executeJob(hollowBlobDataProvider.getStateEngine());
	}

	public boolean executeJob(HollowReadStateEngine readStateEngine) {
		boolean success = true;
		if(ctx.getConfig().isPlaybackMonkeyEnabled()){
			final long now = System.currentTimeMillis();
			try {
				if(isPlaybackMonkeyInstancesReadyForTest()){
						Set<ValuableVideo> mostValuableChangedVideos = videoRanker.getMostValuableChangedVideos(ctx, getCycleVersion(), readStateEngine);
						ctx.getLogger().info(PlaybackMonkey, "{}: got {} most valuable videos to test.", getJobName(), mostValuableChangedVideos.size());

						testResultVideoCountryKeys = dataTester.testVideoCountryKeysWithRetry(ctx.getLogger(), mostValuableChangedVideos, ctx.getConfig().getPlaybackMonkeyMaxRetriesPerTest());

						long timeTaken = System.currentTimeMillis()-now;
						PlaybackMonkeyUtil.logResultsToAtlas(PlaybackMonkeyUtil.TIME_TAKEN, timeTaken, vip, "after");
						ctx.getLogger().info(PlaybackMonkey, "{}: completed with {} video country pairs", getJobName(), testResultVideoCountryKeys.size());
						ctx.getLogger().info(PlaybackMonkey, "{}: success of test: {}", getJobName(), success);
						ctx.getLogger().info(PlaybackMonkey, "{}: time taken {}ms", getJobName(), timeTaken);

				} else {// For some reason instances did not get to desired version in timeout.
					success = false;
				}
			} catch (Exception e) {
			    ctx.getLogger().error(PlaybackMonkey, "{}: failed with Exception", getJobName(), e);
				success = false;
			} 
		}
		boolean finalResultAferPBMOverride = PlaybackMonkeyUtil.getFinalResultAferPBMOverride(success, ctx.getConfig());
		ctx.getLogger().info(PlaybackMonkey, "{}: success: {}. finalResultAfterPBMOverride: {}", getJobName(), success, finalResultAferPBMOverride);
		if(!finalResultAferPBMOverride)
			// Do not send success metric because, this step success does not mean it is successful. 
			PlaybackMonkeyUtil.sendPBMFailureMetric(ctx, finalResultAferPBMOverride, vip);
		return finalResultAferPBMOverride;
	}

	private boolean isPlaybackMonkeyInstancesReadyForTest() throws Exception {
        final long stopCheckingTime = System.currentTimeMillis() + MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION;
        final String desiredVersion = String.valueOf(getCycleVersion());
        List<String> instancesNotInDesiredVersion = null;
        ctx.getLogger().info(PlaybackMonkey, "{}: Waiting for pbm instances to get to version {}", getJobName(), desiredVersion);
        while(System.currentTimeMillis() < stopCheckingTime) {
			try {
				String instanceInPlayBackMonkeyStack = dataTester.getInstanceInPlayBackMonkeyStack();
				ctx.getLogger().info(PlaybackMonkey, "{}: {} Got these PBM instances: {}", getJobName(), desiredVersion, instanceInPlayBackMonkeyStack);
				instancesNotInDesiredVersion = parseStringToListExcludeEmptyValues(instanceInPlayBackMonkeyStack, ",");
				
				if(instancesNotInDesiredVersion == null || instancesNotInDesiredVersion.isEmpty()){
					// No PBM instance to get to desired version, so try again and get a list of PBM PBCS instances
					ctx.getLogger().info(PlaybackMonkey, "{}: {} Got empty PBM instance list. So trying again.", getJobName(), desiredVersion);
					continue;
				}
				
				final Map<String, String> columns = cassandraHelper.getColumns(cassandraHelper.vipSpecificKey(vip, desiredVersion));
				for(final String instance: columns.keySet()){
					instancesNotInDesiredVersion.remove(instance.trim());
				}
				if(instancesNotInDesiredVersion.isEmpty())
					return true;
			} catch (final NotFoundException ignore) {
            } catch (final ConnectionException e) {
                ctx.getLogger().warn(PlaybackMonkey, "ConnectionException in {} {}", getJobName(), e.getMessage(), e);
            }
			try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) { }
        }
        ctx.getLogger().error(PlaybackMonkey, "PBM instances: {} did not get to desired version {} with in time out (ms) {}",
                instancesNotInDesiredVersion == null ? "" : instancesNotInDesiredVersion,
                desiredVersion,
                MAX_TIME_NEEDED_FOR_CLIENT_TO_LOAD_A_VERSION);
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
