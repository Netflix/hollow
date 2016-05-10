package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.PlaybackMonkey;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.PlaybackMonkeyTestVideo;

import com.google.common.collect.ComparisonChain;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.TopNVideoViewHoursData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValidationVideoRanker {
    private final HollowBlobDataProvider hollowBlobDataProvider;
    private final Set<VideoCountryKey> pastFailedIDsToCheck = new HashSet<>();

    public ValidationVideoRanker(final HollowBlobDataProvider hollowBlobDataProvider) {
        this.hollowBlobDataProvider = hollowBlobDataProvider;
    }

	public List<VideoCountryKey> getMostValuableChangedVideos(PublishWorkflowContext ctx) {
		long start = System.currentTimeMillis();
		// Change will take effect on restart. But can't yet move this to getMostValuableChangedVideos, since before and after test need to get same video country pairs.
		Set<String> importantCountriesToTest =  ctx.getConfig().getPlaybackMonkeyTestForCountries();
		// TODO: would be nice optimization to not calculate the most valued videos twice in 1 cycle: 1 for before test and 2nd time for after tests.
		int maxVideos = ctx.getConfig().getPlaybackMonkeyMaxTestVideosSize();

		List<VideoCountryKey> mostValueableVideosToTest = new ArrayList<HollowBlobDataProvider.VideoCountryKey>(maxVideos);

		final Map<String, TopNVideoViewHoursData> topnByCountry = hollowBlobDataProvider.getTopNData();

		final Map<String, Set<Integer>> videosBasedOnPackageChanges = hollowBlobDataProvider.changedVideoCountryKeysBasedOnPackages();

		final Map<String, Set<Integer>> videosBasedOnCompVideoChanges = hollowBlobDataProvider.changedVideoCountryKeysBasedOnCompleteVideos();


		// Assuming max videos for test will be at least equal to or greater than number of supported countries.
		final int videosPerCountry = maxVideos/ (importantCountriesToTest.size());

		for (String countryId : importantCountriesToTest) {
			Set<Integer> videosWithPckgDataChange = videosBasedOnPackageChanges.get(countryId);
			Set<Integer> videosWithCompVideoChange = videosBasedOnCompVideoChanges.get(countryId);
			if (videosWithPckgDataChange == null)
				videosWithPckgDataChange = Collections.emptySet();
			if(videosWithCompVideoChange == null)
				videosWithCompVideoChange = Collections.emptySet();

			TopNVideoViewHoursData topNForCountry = topnByCountry.get(countryId);
			if (topNForCountry != null) {
				List<Integer> sortedTopNVideos = getSortedTopNVideos(topNForCountry.getVideoViewHrs1Day(),
						videosWithPckgDataChange, videosWithCompVideoChange);

				int size = (videosPerCountry > sortedTopNVideos.size()) ? sortedTopNVideos.size() : videosPerCountry;
				for (int i = 0; i < size; i++) {
					Integer videoId = sortedTopNVideos.get(i);
					mostValueableVideosToTest.add(new VideoCountryKey(countryId, videoId));

					ctx.getLogger().info(PlaybackMonkeyTestVideo, "ID: " + videoId + " country: " + countryId);
				}
			}
		}

		// If videos from past cycles failed, add the failed IDs to current cycle.
		if(pastFailedIDsToCheck != null && pastFailedIDsToCheck.size() > 0){
		    ctx.getLogger().info(PlaybackMonkey, "Adding accumulated failed IDs to most valued video list: " + getFailedIDsStr(pastFailedIDsToCheck));
			//mostValueableVideosToTest.addAll(pastFailedIDsToCheck);
			addFailedIDs(mostValueableVideosToTest, pastFailedIDsToCheck, importantCountriesToTest);
		}

		long timeTaken = System.currentTimeMillis() - start;
		ctx.getLogger().info(PlaybackMonkey, "Returning " + mostValueableVideosToTest.size() + " TopN Videos.  Took " + timeTaken + "ms.");

		return Collections.unmodifiableList(mostValueableVideosToTest);
	}

    private void addFailedIDs(List<VideoCountryKey> mostValueableVideosToTest,
            Set<VideoCountryKey> pastFailedIDsToCheck2, Set<String> importantCountriesToTest) {
        for (VideoCountryKey failedOne: pastFailedIDsToCheck2) {
            if (importantCountriesToTest.contains(failedOne.getCountry())) //do not failed id if the country has been excluded from PBM tests
                mostValueableVideosToTest.add(failedOne);
        }
    }

    private Object getFailedIDsStr(Set<VideoCountryKey> failedIDs) {
        StringBuilder idStr = new StringBuilder();
        for (VideoCountryKey v: failedIDs) {
          idStr.append(v.toShortString()).append(",");
        }
        return idStr;
    }

	private List<Integer> getSortedTopNVideos(Map<Integer, Float> videoViewHrs1Day, Set<Integer> videosWithPckgDataChange, Set<Integer> videosWithCompVideoChange) {
        if(nullOrEmpty(videoViewHrs1Day)) return Collections.emptyList();

        List<Integer> sortedVideoIds = new ArrayList<>(videoViewHrs1Day.size());
        sortedVideoIds.addAll(videoViewHrs1Day.keySet());

        TopNPackageChangePreferringComparator viewHrsModifiedComparator = new TopNPackageChangePreferringComparator(videosWithPckgDataChange, videosWithCompVideoChange, videoViewHrs1Day);
        Collections.sort(sortedVideoIds, viewHrsModifiedComparator);

        return sortedVideoIds;
    }

    private boolean nullOrEmpty(Map<?, ?> topnByCountry) {
        return (topnByCountry == null || topnByCountry.size() < 1);
    }


	public void setFailedIDs(List<VideoCountryKey> failedIDs) {
		if(failedIDs == null || failedIDs.size() < 1)
			pastFailedIDsToCheck.clear();
		else {
			pastFailedIDsToCheck.addAll(failedIDs);
		}
	}

	/**
	 * This comparator assumes that all videos being compared are from topN list
	 * @author lkanchanapalli
	 *
	 */
    public class TopNPackageChangePreferringComparator implements Comparator<Integer> {
        private final Set<Integer> videosWithPckgDataChange;
        private final Map<Integer, Float> videoViewHoursMap;
		private final Set<Integer> videosWithCompVideoChange;

        public TopNPackageChangePreferringComparator(Set<Integer> videosWithPckgDataChange, Set<Integer> videosWithCompVideoChange, Map<Integer, Float> videoViewHoursMap) {
                this.videosWithPckgDataChange = videosWithPckgDataChange;
                this.videosWithCompVideoChange = videosWithCompVideoChange;
                this.videoViewHoursMap = videoViewHoursMap;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            /**
             * Among the topN videos, prioritize package changes first, then complete video changes.
             * If package and complete video changes are equal, then compare by view share.
             */
            ComparisonChain chain = ComparisonChain.start()
                .compareTrueFirst(videosWithPckgDataChange.contains(o1), videosWithPckgDataChange.contains(o2))
                .compareTrueFirst(videosWithCompVideoChange.contains(o1), videosWithCompVideoChange.contains(o2))
                .compare(videoViewHoursMap.get(o2), videoViewHoursMap.get(o1));
            return chain.result();
        }
    }

    /**
     * This comparator assumes that all videos being compared are from topN list
     * @author lkanchanapalli
     */
    public class TopNViewShareAndModifiedInCycleComparator implements Comparator<Integer> {
        private final Set<Integer> videosModInThisCycle;
        private final Map<Integer, Float> videoViewHoursMap;

        public TopNViewShareAndModifiedInCycleComparator(Set<Integer> videosModInThisCycle, Map<Integer, Float> videoViewHoursMap) {
                this.videosModInThisCycle = videosModInThisCycle;
                this.videoViewHoursMap = videoViewHoursMap;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            boolean o1Changed = videosModInThisCycle.contains(o1);
            boolean o2Changed = videosModInThisCycle.contains(o2);
            float o1ViewHrs = videoViewHoursMap.get(o1);
            float o2ViewHrs = videoViewHoursMap.get(o2);

            if((o1Changed && o2Changed) || (!o1Changed && !o2Changed)){
                    // Modified videos is out of the equation
                    if(o1ViewHrs > o2ViewHrs)
                            return -1;
                    if(o1ViewHrs < o2ViewHrs)
                            return 1;
                    return 0;
            }

            if(o1Changed && !o2Changed)
                    return -1;

            if(!o1Changed && o2Changed)
                    return 1;

            return 0;
        }

    }

}
