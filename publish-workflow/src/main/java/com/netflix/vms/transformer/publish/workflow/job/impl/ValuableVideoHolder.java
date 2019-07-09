package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkeyTestVideo;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkeyWarn;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ComparisonChain;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.type.ISOCountry;
import com.netflix.type.NFCountry;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.FloatHollow;
import com.netflix.vms.generated.notemplate.ISOCountryHollow;
import com.netflix.vms.generated.notemplate.IntegerHollow;
import com.netflix.vms.generated.notemplate.MapOfIntegerToFloatHollow;
import com.netflix.vms.generated.notemplate.MapOfIntegerToWindowPackageContractInfoHollow;
import com.netflix.vms.generated.notemplate.PackageDataHollow;
import com.netflix.vms.generated.notemplate.TopNVideoDataHollow;
import com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.generated.notemplate.WindowPackageContractInfoHollow;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ValuableVideoHolder {
	private final Map<Long, Set<ValuableVideo>> mostValuableVideosToTestByCycle;
	private final Set<VideoCountryKey> pastFailedIDsToCheck;
	
	public static class ValuableVideo extends VideoCountryKey {
	    private final boolean isAvailableForDownload;
	    
        public boolean isAvailableForDownload() {
            return isAvailableForDownload;
        }
        
        public ValuableVideo(String country, int videoId, boolean isAvailableForDownload) {
            super(country, videoId);
            this.isAvailableForDownload = isAvailableForDownload;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (isAvailableForDownload ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            ValuableVideo other = (ValuableVideo) obj;
            if (isAvailableForDownload != other.isAvailableForDownload)
                return false;
            return true;
        }
	}

    public ValuableVideoHolder() {
        this.mostValuableVideosToTestByCycle = new HashMap<>();
        this.pastFailedIDsToCheck = new HashSet<>();
    }
    
	public Set<ValuableVideo> getMostValuableChangedVideos(PublishWorkflowContext ctx, long version,
			HollowReadStateEngine readStateEngine) {
		Set<ValuableVideo> valuableVideos = mostValuableVideosToTestByCycle.get(version);
		if(valuableVideos == null){
			valuableVideos = computeMostValuableChangedVideos(ctx, version, readStateEngine);
			mostValuableVideosToTestByCycle.put(version, valuableVideos);
		}
		return valuableVideos;
	}

	public Map<String, TopNVideoDataHollow> getTopNData(HollowReadStateEngine readStateEngine) {
		// Read from blob
		Map <String, TopNVideoDataHollow> result = new HashMap<>();

		VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);

		for(TopNVideoDataHollow topn: api.getAllTopNVideoDataHollow()){

			String countryId = topn._getCountryId();

			result.put(countryId, topn);

		}
		return result;
	}

	public Map<String, Set<Integer>> changedVideoCountryKeysBasedOnPackages(HollowReadStateEngine readStateEngine) {
		HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("PackageData");
		PopulatedOrdinalListener packageListener = typeState.getListener(PopulatedOrdinalListener.class);
		BitSet modifiedPackages = new BitSet(packageListener.getPopulatedOrdinals().length());
		modifiedPackages.or(packageListener.getPopulatedOrdinals());
		modifiedPackages.xor(packageListener.getPreviousOrdinals());

		if (modifiedPackages.cardinality() == 0|| modifiedPackages.cardinality() == packageListener.getPopulatedOrdinals().cardinality())
			return Collections.emptyMap();

		Map<String, Set<Integer>> modifiedPackageVideoIds = new HashMap<>();
		VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);

		int ordinal = modifiedPackages.nextSetBit(0);
		while (ordinal != -1) {
			PackageDataHollow packageData = api.getPackageDataHollow(ordinal);
			Set<ISOCountryHollow> deployCountries = packageData._getAllDeployableCountries();
			VideoHollow video = packageData._getVideo();

			if (deployCountries == null || video == null)
				continue;

			Iterator<ISOCountryHollow> iterator = deployCountries.iterator();
			while (iterator.hasNext()) {
				String countryId = iterator.next()._getId();
				Set<Integer> modIdsForCountry = modifiedPackageVideoIds.get(countryId);
				if (modIdsForCountry == null) {
					modIdsForCountry = new HashSet<Integer>();
					modifiedPackageVideoIds.put(countryId, modIdsForCountry);
				}
				modIdsForCountry.add(video._getValue());
			}
			ordinal = modifiedPackages.nextSetBit(ordinal + 1);
		}
		return modifiedPackageVideoIds;
	}

	public Map<String, Set<Integer>> changedVideoCountryKeysBasedOnCompleteVideos(HollowReadStateEngine readStateEngine) {
		HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("CompleteVideo");
		PopulatedOrdinalListener completeVideoListener = typeState.getListener(PopulatedOrdinalListener.class);
		BitSet modifiedCompleteVideos = new BitSet(completeVideoListener.getPopulatedOrdinals().length());
		modifiedCompleteVideos.or(completeVideoListener.getPopulatedOrdinals());
		modifiedCompleteVideos.xor(completeVideoListener.getPreviousOrdinals());

		if(modifiedCompleteVideos.cardinality() == 0 || modifiedCompleteVideos.cardinality() == completeVideoListener.getPopulatedOrdinals().cardinality())
			return Collections.emptyMap();

		VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);
		Map<String, Set<Integer>> modifiedIds = new HashMap<>();

		int ordinal = modifiedCompleteVideos.nextSetBit(0);
		while(ordinal != -1) {
			CompleteVideoHollow cv = api.getCompleteVideoHollow(ordinal);

			int videoId = cv._getId()._getValue();
			String countryId = cv._getCountry()._getId();

			Set<Integer> videoIds = modifiedIds.get(countryId);
			if(videoIds == null) {
				videoIds = new HashSet<>();
				modifiedIds.put(countryId, videoIds);
			}
			videoIds.add(videoId);

			ordinal = modifiedCompleteVideos.nextSetBit(ordinal + 1);
		}

		return modifiedIds;
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
	private Set<ValuableVideo> computeMostValuableChangedVideos(PublishWorkflowContext ctx, long version,
			HollowReadStateEngine readStateEngine) {
		long start = System.currentTimeMillis();
		String importantCountriesCSV = ctx.getConfig().getPlaybackMonkeyTestForCountries();
		Set<String> importantCountriesToTest = new HashSet<String>(); 
		
		for(String country : importantCountriesCSV.split(",")) {
			importantCountriesToTest.add(country);
		}
		int maxVideos = ctx.getConfig().getPlaybackMonkeyMaxTestVideosSize();

		Set<ValuableVideo> mostValueableVideosToTest = new HashSet<ValuableVideo>(maxVideos);

		final Map<String, TopNVideoDataHollow> topnByCountry = getTopNData(readStateEngine);

		final Map<String, Set<Integer>> videosBasedOnPackageChanges = changedVideoCountryKeysBasedOnPackages(readStateEngine);

		final Map<String, Set<Integer>> videosBasedOnCompVideoChanges = changedVideoCountryKeysBasedOnCompleteVideos(readStateEngine);

		final int videosPerCountry = maxVideos/ (importantCountriesToTest.size());
		
		final Map<String, Set<VideoCountryKey>> pastFailedIDsByCountry = getPastFailedIDsByCountry();
		
		Map<String, Set<Integer>> excludedVideosByCountry = getExcludedVideosBycountry(ctx);

		for (String countryId : importantCountriesToTest) {
			Set<ValuableVideo> valuedVideosForCountry = new HashSet<ValuableVideo>(videosPerCountry);
			
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

		        VMSRawHollowAPI api = new VMSRawHollowAPI(readStateEngine);
		        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(readStateEngine, "CompleteVideo", "id.value", "country.id");
				
				int size = (videosPerCountry > sortedTopNVideos.size()) ? sortedTopNVideos.size() : videosPerCountry;
				for (int i = 0; i < size; i++) {
					Integer videoId = sortedTopNVideos.get(i);
					if(!isExcluded(excludedVideosForCountry, videoId)) {
						valuedVideosForCountry.add(new ValuableVideo(countryId, videoId, isAvailableForDownload(videoId, countryId, idx, api)));
					}
				}
				
				// Add failed IDs from past cycle if they are not in exclusion list
				if(pastFailedIDsForCountry != null && !pastFailedIDsForCountry.isEmpty()){
					ctx.getLogger().info(PlaybackMonkeyTestVideo, "Adding {} failed IDs for country {} if not in exclude list: [{}]",
					        pastFailedIDsForCountry.size(), countryId, getVideoIDsForVideoCountryKeys(pastFailedIDsForCountry));
					for(VideoCountryKey v: pastFailedIDsForCountry){
						int videoId = v.getVideoId();
						if(!isExcluded(excludedVideosForCountry, videoId)) {
							valuedVideosForCountry.add(new ValuableVideo(v.getCountry(), videoId, isAvailableForDownload(videoId, v.getCountry(), idx, api)));
						}
					}
				}
				
				ctx.getLogger().info(PlaybackMonkeyTestVideo,
						"Picked {} valuable videos to test for country (including failed IDs and excluding excluded videos) {}: [{}]",
						 valuedVideosForCountry.size(),
						 countryId,
						 getValuableVideosForCountryAsString(valuedVideosForCountry));
				ctx.getMetricRecorder().recordMetric(Metric.ViewShareCoveredByPBM,
						getViewShareOfVideos(readStateEngine, valuedVideosForCountry).get(countryId),
						"country", countryId);
			} else {
				ctx.getLogger().warn(PlaybackMonkeyWarn, "For country {} topN videos are empty and so no videos were "
						+ "added for the country even though the country is in playbackmonkeyTestForCountries property.", countryId);
			}
			mostValueableVideosToTest.addAll(valuedVideosForCountry);
		}

		long timeTaken = System.currentTimeMillis() - start;
		ctx.getLogger().info(PlaybackMonkey, "Returning {} TopN Videos.  Took {}ms.", mostValueableVideosToTest.size(), timeTaken);
		
		return Collections.unmodifiableSet(mostValueableVideosToTest);
	}
	
	private boolean isAvailableForDownload(int videoId, String countryId, HollowPrimaryKeyIndex idx, VMSRawHollowAPI api) {
	   	int compVideoOrdinal = idx.getMatchingOrdinal(videoId, countryId);
	   	if(compVideoOrdinal != -1) {
		   	CompleteVideoHollow completeVideoHollow = api.getCompleteVideoHollow(compVideoOrdinal);
		   	return isAvailableForDownload(completeVideoHollow);
	   	}
	   	return false;
	}

	private boolean isAvailableForDownload(CompleteVideoHollow cv) {
	   	//Find the current or future window, then pick the contract info for highest package id.
		if (cv._getData()._getCountrySpecificData() == null)
			return false;

		for(VMSAvailabilityWindowHollow window : cv._getData()._getCountrySpecificData()._getAvailabilityWindows()) {
		   	if(isCurrentOrFutureWindow(window._getStartDate()._getVal(), window._getEndDate()._getVal())) {
			   	MapOfIntegerToWindowPackageContractInfoHollow map = window._getWindowInfosByPackageId();
               	Entry<IntegerHollow, WindowPackageContractInfoHollow> highestPackageEntry = getHighestPackageEntry(map);
               	if(highestPackageEntry != null) {
                   	return highestPackageEntry.getValue()._getVideoContractInfo()._getIsAvailableForDownload();
               	}
		   	}
	   	}
       	return false;
    }

    private Entry<IntegerHollow, WindowPackageContractInfoHollow> getHighestPackageEntry(
    	    MapOfIntegerToWindowPackageContractInfoHollow map) {
    	long highestPackageId = -1;
       	Entry<IntegerHollow, WindowPackageContractInfoHollow> highestPackageEntry = null;
       	for(Entry<IntegerHollow, WindowPackageContractInfoHollow> packageEntry : map.entrySet()) {
           	int packageId = packageEntry.getValue()._getVideoPackageInfo()._getPackageId();
           	if(packageId > highestPackageId) {
               highestPackageEntry = packageEntry;
		       highestPackageId = packageId;
		   	}
       	}
       	return highestPackageEntry;
    }
    
    public boolean isInWindow(final long startDate, final long endDate, final long timestamp) {
    	return ((startDate <= timestamp) && (timestamp <= endDate));
    }

    public boolean isCurrentOrFutureWindow(final long startDate, final long endDate) {
    	final long now = System.currentTimeMillis();
        return isInWindow(startDate, endDate, now) || (now < startDate);
    }	

	private Map<Integer, Float> getVideoViewHours1DayFromHollow(MapOfIntegerToFloatHollow videoViewHrs1DayHollow) {
		
		if(nullOrEmpty(videoViewHrs1DayHollow))
			return Collections.emptyMap();
		
		Map<Integer, Float> result = new HashMap<>(videoViewHrs1DayHollow.size());
		
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
    Set<Integer> getVideoIDsForVideoCountryKeys(Set<? extends VideoCountryKey> videoCountryKeys) {
        if(videoCountryKeys  == null || videoCountryKeys.isEmpty())
            return Collections.emptySet();
        Set<Integer> result = new HashSet<>(videoCountryKeys.size());
        for(VideoCountryKey v: videoCountryKeys){
            result.add(v.getVideoId());
        }
        return result;
    }
    
    String getValuableVideosForCountryAsString(Set<ValuableVideo> valuableVideos) {
        if(valuableVideos  == null || valuableVideos.isEmpty())
            return "";
        Set<String> valuableVideosAStrings = new HashSet<>();
        for(ValuableVideo v: valuableVideos){
        	StringBuilder sb = new StringBuilder();
        	sb.append(v.getVideoId());
        	sb.append(":");
        	sb.append(v.isAvailableForDownload());
        	valuableVideosAStrings.add(sb.toString());
        }
        return String.join(",", valuableVideosAStrings);
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

	public Map<String, Float> getViewShareOfVideos(HollowReadStateEngine readStateEngine,
			Collection<? extends VideoCountryKey> videoCountryKeys) {
		Map<String, Float> result = new HashMap<>();

		if (videoCountryKeys == null || videoCountryKeys.isEmpty())
			return result;

		final Map<String, TopNVideoDataHollow> topnByCountry = getTopNData(readStateEngine);
		
		final Map<String, Map<Integer, Float>> viewViewShare1DayByCountry = new HashMap<>(topnByCountry.size());
		
		for(Entry<String, TopNVideoDataHollow> topnEntry: topnByCountry.entrySet()){
			String countryId = topnEntry.getKey();
			
			Map<Integer, Float> videoViewHrs1Day = getVideoViewHours1DayFromHollow(topnEntry.getValue()._getVideoViewHrs1Day());
			
			viewViewShare1DayByCountry.put(countryId, videoViewHrs1Day);
		}

		for (VideoCountryKey videoCountry : videoCountryKeys) {
			
			String countryId = videoCountry.getCountry();
			
			TopNVideoDataHollow topNForCountry = topnByCountry.get(countryId);

			if (isInvalidTopNData(topNForCountry)) {
				if (result.get(countryId) == null) {
					result.put(countryId, 0f);
				}
				continue;
			}
			Map<Integer, Float> videoViewHrs1Day = viewViewShare1DayByCountry.get(countryId);
			
			Float videoViewHrs = videoViewHrs1Day.get(videoCountry.getVideoId());
			
			Float countryViewHrs1Day = topNForCountry._getCountryViewHrs1Day();
			
			float videoViewShareAsPercent = 0f;

			if(videoViewHrs != null && countryViewHrs1Day != null && Float.compare(0f, countryViewHrs1Day)!=0)
				videoViewShareAsPercent = (videoViewHrs / countryViewHrs1Day) * 100;
			
			Float viewShare = result.get(countryId);
			if (viewShare == null)  viewShare = 0f;
			viewShare = videoViewShareAsPercent + viewShare;
			result.put(countryId, viewShare);
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
        ctx.getLogger().info(PlaybackMonkeyTestVideo, "Exclude video property value: {}", stringToParse);
        
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

    private Collection<String> fetchCountriesForNameSpace(final String namespace, PublishWorkflowContext ctx){
        if (namespace == null || namespace.trim().isEmpty()) 
            ctx.getLogger().warn(PlaybackMonkeyWarn, "PBM exclude video namespace can not be null or empty");

        if (GLOBAL_SCOPE.equalsIgnoreCase(namespace)) {
            return ctx.getOctoberSkyData().getSupportedCountries();
        }

        try {
            final ISOCountry country = NFCountry.findInstance(namespace);
            if(country != null)
            	return Collections.singleton(namespace);
        } catch (final Exception ex) {
            ctx.getLogger().warn(PlaybackMonkeyWarn, "Unable to convert namespace={} to country List.  "
                    + "Supported value are GLOBAL or COUNTRY_CODE.", namespace, ex);
        }
		return Collections.emptySet();
    }
}
