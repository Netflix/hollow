package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.PlaybackMonkey;
import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.PlaybackMonkeyTestVideo;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ComparisonChain;
import com.netflix.type.ISOCountry;
import com.netflix.type.NFCountry;
import com.netflix.vms.generated.notemplate.FloatHollow;
import com.netflix.vms.generated.notemplate.IntegerHollow;
import com.netflix.vms.generated.notemplate.MapOfIntegerToFloatHollow;
import com.netflix.vms.generated.notemplate.TopNVideoDataHollow;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ValuableVideoHolder {
	private final HollowBlobDataProvider hollowBlobDataProvider;
	private final Map<Long, Set<VideoCountryKey>> mostValuableVideosToTestByCycle;
	private final Set<VideoCountryKey> pastFailedIDsToCheck = new HashSet<>();

    public ValuableVideoHolder(final HollowBlobDataProvider hollowBlobDataProvider) {
        this.hollowBlobDataProvider = hollowBlobDataProvider;
        this.mostValuableVideosToTestByCycle = new HashMap<Long, Set<VideoCountryKey>>();
    }
    
	public Set<VideoCountryKey> getMostValuableChangedVideos(PublishWorkflowContext ctx, long version) {
		Set<VideoCountryKey> valuableVideos = mostValuableVideosToTestByCycle.get(version);
		if(valuableVideos == null){
			valuableVideos = computeMostValuableChangedVideos(ctx, version);
			mostValuableVideosToTestByCycle.put(version, valuableVideos);
		}
		return valuableVideos;
	}
	
	/**
	 * The following logic uses for each country video that had package change this cycle, videos that had complete video change this cycle,
	 * previous cycle failed videos to determine most valuable video to test for this cycle (for PBM).
	 * 
	 * A property provides a way to exclude one or more videos for one or more countries. These excluded videos are removed from test videos. 
	 * 
	 * @param ctx
	 * @param version
	 * @return
	 */
	private Set<VideoCountryKey> computeMostValuableChangedVideos(PublishWorkflowContext ctx, long version) {
		long start = System.currentTimeMillis();
		String importantCountriesCSV = ctx.getConfig().getPlaybackMonkeyTestForCountries();
		Set<String> importantCountriesToTest = new HashSet<String>(); 
		
		for(String country : importantCountriesCSV.split(",")) {
			importantCountriesToTest.add(country);
		}
		int maxVideos = ctx.getConfig().getPlaybackMonkeyMaxTestVideosSize();

		Set<VideoCountryKey> mostValueableVideosToTest = new HashSet<HollowBlobDataProvider.VideoCountryKey>(maxVideos);

		final Map<String, TopNVideoDataHollow> topnByCountry = hollowBlobDataProvider.getTopNData();

		final Map<String, Set<Integer>> videosBasedOnPackageChanges = hollowBlobDataProvider.changedVideoCountryKeysBasedOnPackages();

		final Map<String, Set<Integer>> videosBasedOnCompVideoChanges = hollowBlobDataProvider.changedVideoCountryKeysBasedOnCompleteVideos();

		final int videosPerCountry = maxVideos/ (importantCountriesToTest.size());
		
		final Map<String, Set<VideoCountryKey>> pastFailedIDsByCountry = getPastFailedIDsByCountry();
		
		Map<String, Set<Integer>> excludedVideosByCountry = getExcludedVideosBycountry(ctx);

		for (String countryId : importantCountriesToTest) {
			Set<VideoCountryKey> valuedVideosForCountry = new HashSet<HollowBlobDataProvider.VideoCountryKey>(videosPerCountry);
			
			Set<Integer> videosWithPckgDataChange = videosBasedOnPackageChanges.get(countryId);
			
			Set<Integer> videosWithCompVideoChange = videosBasedOnCompVideoChanges.get(countryId);
			
			if (videosWithPckgDataChange == null) videosWithPckgDataChange = Collections.emptySet();
			
			if(videosWithCompVideoChange == null) videosWithCompVideoChange = Collections.emptySet();
			
			TopNVideoDataHollow topNForCountry = topnByCountry.get(countryId);
			
			Set<Integer> excludedVideosForCountry = excludedVideosByCountry.get(countryId);
			
			Set<VideoCountryKey> pastFailedIDsForCountry = pastFailedIDsByCountry.get(countryId);
			
			if(pastFailedIDsForCountry == null) pastFailedIDsForCountry = Collections.emptySet();
			
			if (topNForCountry != null) {
				
				Map<Integer, Float> videoViewHours1Day = getVideoViewHours1DayFromHollow(topNForCountry._getVideoViewHrs1Day());
				
				List<Integer> sortedTopNVideos = getSortedTopNVideos(videoViewHours1Day, videosWithPckgDataChange, videosWithCompVideoChange);

				int size = (videosPerCountry > sortedTopNVideos.size()) ? sortedTopNVideos.size() : videosPerCountry;
				for (int i = 0; i < size; i++) {
					Integer videoId = sortedTopNVideos.get(i);
					if(!isExcluded(excludedVideosForCountry, videoId))
						valuedVideosForCountry.add(new VideoCountryKey(countryId, videoId));
				}
				
				// Add failed IDs from past cycle if they are not in exclusion list
				if(pastFailedIDsForCountry != null && !pastFailedIDsForCountry.isEmpty()){
					ctx.getLogger().info(PlaybackMonkeyTestVideo,"Adding "+ pastFailedIDsForCountry.size()+" failed IDs for country "+countryId+
							" if not in exclude list: ["+getVideoIDsForVideoCountryKeys(pastFailedIDsForCountry)+"]");
					for(VideoCountryKey v: pastFailedIDsForCountry){
						if(!isExcluded(excludedVideosForCountry, v.getVideoId()))
							valuedVideosForCountry.add(v);
					}
				}
				
				ctx.getLogger().info(PlaybackMonkeyTestVideo,
						"Picked "+valuedVideosForCountry.size()+" valuable videos to test for country (including failed IDs and excluding excluded videos)"+
						 countryId+" : ["+getVideoIDsForVideoCountryKeys(valuedVideosForCountry)+"]");
				ctx.getMetricRecorder().recordMetric(Metric.ViewShareCoveredByPBM, getViewShareOfVideos(valuedVideosForCountry).get(countryId), "country", countryId);
			} else {
				ctx.getLogger().warn(LogTag.PlaybackMonkeyWarn, "For country "+countryId+" topN videos are empty and so no videos were "
						+ "added for the country even though the country is in playbackmonkeyTestForCountries property.");
			}
			mostValueableVideosToTest.addAll(valuedVideosForCountry);
		}

		long timeTaken = System.currentTimeMillis() - start;
		ctx.getLogger().info(PlaybackMonkey, "Returning " + mostValueableVideosToTest.size() + " TopN Videos.  Took " + timeTaken + "ms.");
		
		return Collections.unmodifiableSet(mostValueableVideosToTest);
	}

	private Map<Integer, Float> getVideoViewHours1DayFromHollow(MapOfIntegerToFloatHollow videoViewHrs1DayHollow) {
		
		Map<Integer, Float> result = new HashMap<>(videoViewHrs1DayHollow.size());	
		
		if(nullOrEmpty(videoViewHrs1DayHollow))
			return result;
		
		for(Entry<IntegerHollow, FloatHollow> entryH: videoViewHrs1DayHollow.entrySet()){
			int videoId = entryH.getKey()._getVal();
			float viewHrs1Day = entryH.getValue()._getVal();
			result.put(videoId, viewHrs1Day);
		}
		return result;
	}

	private boolean isExcluded(Set<Integer> excludedVideosForCountry, Integer videoId) {
		return excludedVideosForCountry != null && excludedVideosForCountry.contains(videoId);
	}
	
    
	@VisibleForTesting
	Set<Integer> getVideoIDsForVideoCountryKeys(Set<VideoCountryKey> mostValueableVideosToTest) {
		if(mostValueableVideosToTest  == null || mostValueableVideosToTest.isEmpty())
			return Collections.emptySet();
		Set<Integer> result = new HashSet<>(mostValueableVideosToTest.size());
		for(VideoCountryKey v: mostValueableVideosToTest){
			result.add(v.getVideoId());
		}
		return result;
	}

	private Map<String, Set<VideoCountryKey>> getPastFailedIDsByCountry() {
		if(pastFailedIDsToCheck == null || pastFailedIDsToCheck.isEmpty())
			return Collections.emptyMap();
		Map<String, Set<VideoCountryKey>> result = new HashMap<>();
		for(VideoCountryKey key: pastFailedIDsToCheck){
			Set<VideoCountryKey> videoCountryKeys = result.get(key.getCountry());
			if(videoCountryKeys == null){
				videoCountryKeys = new HashSet<VideoCountryKey>();
				result.put(key.getCountry(), videoCountryKeys);
			}
			videoCountryKeys.add(key);
		}
		return result;
	}

	public Map<String, Float> getViewShareOfVideos( Collection<VideoCountryKey> videoCountryKeys) {
		Map<String, Float> result = new HashMap<>();

		if (videoCountryKeys == null || videoCountryKeys.isEmpty())
			return result;

		final Map<String, TopNVideoDataHollow> topnByCountry = hollowBlobDataProvider.getTopNData();

		for (VideoCountryKey videoCountry : videoCountryKeys) {
			
			String country = videoCountry.getCountry();
			
			TopNVideoDataHollow topNForCountry = topnByCountry.get(country);

			if (isInvalidTopNData(topNForCountry)) {
				if (result.get(country) == null) {
					//LOGGER.logf(ErrorCode.PlayBackMonkeyWarn, "Missing topN data for country %s when calculating view share.", country);
					result.put(country, 0f);
				}
				continue;
			}
			
			Map<Integer, Float> videoViewHrs1Day = getVideoViewHours1DayFromHollow(topNForCountry._getVideoViewHrs1Day());
			Float videoViewHrs = videoViewHrs1Day.get(videoCountry.getVideoId());
			Float countryViewHrs1Day = topNForCountry._getCountryViewHrs1Day();
			float videoViewShareAsPercent = 0f;

			if(videoViewHrs != null && countryViewHrs1Day != null && Float.compare(0f, countryViewHrs1Day)!=0)
				videoViewShareAsPercent = (videoViewHrs / countryViewHrs1Day) * 100;
			
			Float viewShare = result.get(country);
			if (viewShare == null)  viewShare = 0f;
			viewShare = videoViewShareAsPercent + viewShare;
			result.put(country, viewShare);
		}// end for
		return result;
	}
	
	private boolean isInvalidTopNData( TopNVideoDataHollow videoViewHoursForCountry) {
		return (videoViewHoursForCountry == null
				|| videoViewHoursForCountry._getVideoViewHrs1Day() == null
				|| videoViewHoursForCountry._getVideoViewHrs1Day().isEmpty() || Float
					.compare(videoViewHoursForCountry._getCountryViewHrs1Day(),
							0f) == 0);
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


	public void onCycleComplete(long version, List<VideoCountryKey> failedIDs) {
		if(failedIDs == null || failedIDs.size() < 1){
			pastFailedIDsToCheck.clear();
		} else {
			pastFailedIDsToCheck.addAll(failedIDs);
		}
		mostValuableVideosToTestByCycle.remove(version);
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

	/**
     * FORMAT of the property this method parses: "~" separated Segments where segment = NameSpace:VideoIds (Comma separated Video Ids) and NameSpace is (GLOBAL or Country)
     * E.g. GLOBAL:1,2;US:1,4;
     * @param onlyProcessVideoIds: Pass null to process all IDs in string. Setting this list will ignore all others from property and only keep given video ids. 
     * @param stringToParse: This is the value to be parsed. NOTE: This method cannot handle empty or null stringtoParse.
     * @return
     * @throws Exception
     */
	@VisibleForTesting
    Map<String, Set<Integer>> getExcludedVideosBycountry(PublishWorkflowContext ctx){
        final Map<String, Set<Integer>> tmpMap = new HashMap<>();
        
        String stringToParse = ctx.getConfig().getPlaybackMonkeyVideoCountryToExclude();
        ctx.getLogger().info(PlaybackMonkeyTestVideo, "Exclude video property value: "+ stringToParse);
        
		if(stringToParse == null || stringToParse.length() < 1)
        	return tmpMap;

        final String segments[] = stringToParse.split(";");
        for (final String seg : segments) {
            final String segParts[] = seg.trim().split(":");
            if (segParts == null || segParts.length < 2) {
                throw new IllegalArgumentException("Invalid segment=" + seg + ", expected format NAMESPACE:VIDEOID_1,VIDEOID_2");
            }

            // Parse video ids
            final String ids = segParts[1];
            Set<Integer> idSet = new HashSet<Integer>();
            for (final String id : ids.trim().split(",")) {
                final Integer videoId = Integer.parseInt(id);
                idSet.add(videoId);
            }

            // Convert name space to countries and apply the video ids
            if (!idSet.isEmpty()) {
                final String namespace = segParts[0];
            	// Expand name space to countries
                final Collection<String> countries = fetchCountriesForNameSpace(namespace, ctx);
                for(String country: countries){
					Set<Integer> countryIdSet = tmpMap.get(country);
					if (countryIdSet == null) {
						countryIdSet = new HashSet<Integer>();
						tmpMap.put(country, countryIdSet);
					}
					countryIdSet.addAll(idSet);
                }
            }// end if idSet
        }// end for seg
        return tmpMap;
    }
    
    public static final String GLOBAL_SCOPE = "GLOBAL";
    protected Collection<String> fetchCountriesForNameSpace(final String namespace, PublishWorkflowContext ctx){
        if (namespace == null || namespace.trim().isEmpty()) 
        	ctx.getLogger().warn(LogTag.PlaybackMonkeyWarn, "PBM esxclude video namespace can not be null or empty");

        if (GLOBAL_SCOPE.equalsIgnoreCase(namespace)) {
            return ctx.getOctoberSkyData().getSupportedCountries();
        }

        try {
            final ISOCountry country = NFCountry.findInstance(namespace);
            if(country != null)
            	return Collections.singleton(namespace);
        } catch (final Exception ex) {
        	ctx.getLogger().warn(LogTag.PlaybackMonkeyWarn, "Unable to convert namespace= "+namespace+" to country List.  "
        			+ "Supported value are GLOBAL or COUNTRY_CODE."+ex.getStackTrace());
        }
		return Collections.emptySet();
    }
}
