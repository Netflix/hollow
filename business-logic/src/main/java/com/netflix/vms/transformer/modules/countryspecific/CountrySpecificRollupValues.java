package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.DateWindow;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
import com.netflix.vms.transformer.util.RollUpOrDownValues;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CountrySpecificRollupValues extends RollUpOrDownValues {

    private boolean showEpisodeFound = false;
    private boolean seasonEpisodeFound = false;

    private int showBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
    private int seasonBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
    private int showBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
    private int seasonBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;

    private Set<Strings> aggregatedShowAssetCodes = new HashSet<Strings>();
    private Set<Strings> aggregatedSeasonAssetCodes = new HashSet<Strings>();

    private Set<VideoFormatDescriptor> showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
    private Set<VideoFormatDescriptor> seasonVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();

    private LinkedHashSetOfStrings showCupTokensFromFirstStreamableEpisode = null;
    private LinkedHashSetOfStrings seasonCupTokensFromFirstStreamableEpisode = null;

    private Map<Strings, List<VideoImage>> showFirstEpisodeVideoImagesMap = Collections.emptyMap();
    private Map<Strings, List<VideoImage>> seasonFirstEpisodeVideoImagesMap = Collections.emptyMap();
    private Map<Strings, List<VideoImage>> showLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
    private Map<Strings, List<VideoImage>> seasonLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();

    private int seasonSequenceNumber = 0;
    private Map<DateWindow, BitSet> seasonSequenceNumberMap = new HashMap<>();

    private int showPrePromoDays = 0;
    private int seasonPrePromoDays = 0;
    private boolean showHasRollingEpisodes = false;;
    private boolean seasonHasRollingEpisodes = false;
    private boolean showIsAvailableForDownload = false;
    private boolean seasonIsAvailableForDownload = false;
    
    private boolean showWindowFound = false;
    private boolean seasonWindowFound = false;

    public void setSeasonSequenceNumber(int seasonSequenceNumber) {
        this.seasonSequenceNumber = seasonSequenceNumber;
    }

    public int getSeasonSequenceNumber() {
        return seasonSequenceNumber;
    }

    public void reset() {
        resetSeason();
        resetShow();
    }

    public void resetSeason() {
        seasonEpisodeFound = false;
        aggregatedSeasonAssetCodes = new HashSet<Strings>();
        seasonVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        seasonPrePromoDays = 0;
        seasonHasRollingEpisodes = false;
        seasonIsAvailableForDownload = false;
        seasonCupTokensFromFirstStreamableEpisode = null;
        seasonFirstEpisodeVideoImagesMap = Collections.emptyMap();
        seasonLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
        seasonBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
        seasonBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
        seasonWindowFound = false;
    }

    public void resetShow() {
        showEpisodeFound = false;
        aggregatedShowAssetCodes = new HashSet<Strings>();
        showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        showPrePromoDays = 0;
        showHasRollingEpisodes = false;
        showIsAvailableForDownload = false;
        showCupTokensFromFirstStreamableEpisode = null;
        showFirstEpisodeVideoImagesMap = Collections.emptyMap();
        showLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
        seasonSequenceNumberMap = new HashMap<>();
        showBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
        showBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
        showWindowFound = false;
    }

    public void episodeFound() {
        this.showEpisodeFound = true;
        this.seasonEpisodeFound = true;
    }
    
    public boolean wasShowEpisodeFound() {
        return showEpisodeFound;
    }

    public boolean wasSeasonEpisodeFound() {
        return seasonEpisodeFound;
    }
    
    public void windowFound() {
        this.showWindowFound = true;
        this.seasonWindowFound = true;
    }
    
    public boolean wasShowWindowFound() {
        return showWindowFound;
    }
    
    public boolean wasSeasonWindowFound() {
        return seasonWindowFound;
    }

    public void newAssetBcp47Codes(Set<Strings> assetCodes) {
        aggregatedShowAssetCodes.addAll(assetCodes);
        aggregatedSeasonAssetCodes.addAll(assetCodes);
    }

    public void newVideoFormatDescriptors(Set<VideoFormatDescriptor> videoFormatDescriptors) {
        showVideoFormatDescriptors.addAll(videoFormatDescriptors);
        seasonVideoFormatDescriptors.addAll(videoFormatDescriptors);
    }

    public void newEpisodeData(boolean isGoLive, int bundledAssetId) {
        if (isGoLive) {
            if (showBundledAssetFromFirstAvailableEpisode == Integer.MIN_VALUE)
                showBundledAssetFromFirstAvailableEpisode = bundledAssetId;
            if (seasonBundledAssetFromFirstAvailableEpisode == Integer.MIN_VALUE)
                seasonBundledAssetFromFirstAvailableEpisode = bundledAssetId;
        } else {
            if (showBundledAssetFromFirstUnavailableEpisode == Integer.MIN_VALUE)
                showBundledAssetFromFirstUnavailableEpisode = bundledAssetId;
            if (seasonBundledAssetFromFirstUnavailableEpisode == Integer.MIN_VALUE)
                seasonBundledAssetFromFirstUnavailableEpisode = bundledAssetId;
        }
    }

    public void foundRollingEpisodes() {
        showHasRollingEpisodes = true;
        seasonHasRollingEpisodes = true;
    }

    public void foundAvailableForDownload() {
        showIsAvailableForDownload = true;
        seasonIsAvailableForDownload = true;
    }
    
    public void newPrePromoDays(int prePromoDays) {
        if (showPrePromoDays == 0 || prePromoDays < showPrePromoDays)
            showPrePromoDays = prePromoDays;
        if (seasonPrePromoDays == 0 || prePromoDays < seasonPrePromoDays)
            seasonPrePromoDays = prePromoDays;
    }

    public void newCupTokens(LinkedHashSetOfStrings cupTokens) {
        if (showCupTokensFromFirstStreamableEpisode == null)
            showCupTokensFromFirstStreamableEpisode = cupTokens;
        if (seasonCupTokensFromFirstStreamableEpisode == null)
            seasonCupTokensFromFirstStreamableEpisode = cupTokens;
    }

    public void newEpisodeStillImagesByTypeMap(Map<Strings, List<VideoImage>> map) {
        if (showFirstEpisodeVideoImagesMap.isEmpty())
            showFirstEpisodeVideoImagesMap = map;
        if (seasonFirstEpisodeVideoImagesMap.isEmpty())
            seasonFirstEpisodeVideoImagesMap = map;

        newEpisodeStillImagesByTypeMapForShowLevelExtraction(map);
    }

    public void newEpisodeStillImagesByTypeMapForShowLevelExtraction(Map<Strings, List<VideoImage>> map) {
        for (Map.Entry<Strings, List<VideoImage>> entry : map.entrySet()) {
            for (VideoImage img : entry.getValue()) {
                for (Strings momentTag : img.videoMoment.momentTags) {
                    if (new String(momentTag.value).equals("show_level")) {
                        newTagBasedShowLevelVideoImage(entry.getKey(), img);
                        break;
                    }
                }
            }
        }
    }

    private void newTagBasedShowLevelVideoImage(Strings type, VideoImage img) {
        List<VideoImage> list = showLevelTaggedVideoImagesRollup.get(type);
        if (list == null) {
            list = new ArrayList<VideoImage>();
            showLevelTaggedVideoImagesRollup.put(type, list);
        }

        list.add(img);

        list = seasonLevelTaggedVideoImagesRollup.get(type);
        if (list == null) {
            list = new ArrayList<VideoImage>();
            seasonLevelTaggedVideoImagesRollup.put(type, list);
        }
        list.add(img);
    }

    public void newSeasonWindow(long startDate, long endDate, int sequenceNumber) {
        DateWindow dateWindow = new DateWindow();
        dateWindow.startDateTimestamp = startDate;
        dateWindow.endDateTimestamp = endDate;

        BitSet seasonSeqNums = seasonSequenceNumberMap.get(dateWindow);
        if (seasonSeqNums == null) {
            seasonSeqNums = new BitSet();
            seasonSequenceNumberMap.put(dateWindow, seasonSeqNums);
        }

        seasonSeqNums.set(sequenceNumber);
    }

    public int getFirstEpisodeBundledAssetId() {
        if (doSeason()) {
            if (seasonBundledAssetFromFirstAvailableEpisode != Integer.MIN_VALUE)
                return seasonBundledAssetFromFirstAvailableEpisode;
            return seasonBundledAssetFromFirstUnavailableEpisode;
        }

        if (showBundledAssetFromFirstAvailableEpisode != Integer.MIN_VALUE)
            return showBundledAssetFromFirstAvailableEpisode;
        return showBundledAssetFromFirstUnavailableEpisode;
    }

    public Set<Strings> getAssetBcp47Codes() {
        if (doSeason())
            return aggregatedSeasonAssetCodes;
        return aggregatedShowAssetCodes;
    }

    public Set<VideoFormatDescriptor> getVideoFormatDescriptors() {
        if (doSeason())
            return seasonVideoFormatDescriptors;
        return showVideoFormatDescriptors;
    }

    public int getPrePromoDays() {
        if (doSeason())
            return seasonPrePromoDays;
        return showPrePromoDays;
    }
    
    public boolean hasRollingEpisodes() {
        if(doSeason())
            return seasonHasRollingEpisodes;
        return showHasRollingEpisodes;
    }

    public boolean isAvailableForDownload() {
        return doSeason() ? seasonIsAvailableForDownload : showIsAvailableForDownload;
    }

    public LinkedHashSetOfStrings getCupTokens() {
        if (doSeason())
            return seasonCupTokensFromFirstStreamableEpisode;
        return showCupTokensFromFirstStreamableEpisode;
    }

    public Map<Strings, List<VideoImage>> getVideoImageMap() {
        if (doSeason()) {
            if (seasonLevelTaggedVideoImagesRollup.isEmpty())
                return seasonFirstEpisodeVideoImagesMap;
            return seasonLevelTaggedVideoImagesRollup;
        }

        if (showLevelTaggedVideoImagesRollup.isEmpty())
            return showFirstEpisodeVideoImagesMap;
        return showLevelTaggedVideoImagesRollup;

    }

    public Map<DateWindow, List<com.netflix.vms.transformer.hollowoutput.Integer>> getDateWindowWiseSeasonSequenceNumbers() {
        if (seasonSequenceNumberMap.isEmpty())
            return Collections.emptyMap();

        /// date windows may be overlapping, need to merge them.
        Set<Long> windowDates = new HashSet<Long>();

        for (Map.Entry<DateWindow, ?> entry : seasonSequenceNumberMap.entrySet()) {
            windowDates.add(entry.getKey().startDateTimestamp);
            windowDates.add(entry.getKey().endDateTimestamp);
        }

        List<Long> sortedWindowDates = new ArrayList<Long>(windowDates);
        Collections.sort(sortedWindowDates);

        Map<DateWindow, List<com.netflix.vms.transformer.hollowoutput.Integer>> mergedWindowSeqNumMap = new HashMap<>();

        BitSet mergedSeqNums = new BitSet();
        for (int i = 0; i < sortedWindowDates.size() - 1; i++) {
            Long currentStartDate = sortedWindowDates.get(i);
            Long currentEndDate = sortedWindowDates.get(i + 1);
            mergedSeqNums.clear();

            for (Map.Entry<DateWindow, BitSet> entry : seasonSequenceNumberMap.entrySet()) {
                if (entry.getKey().startDateTimestamp <= currentStartDate && entry.getKey().endDateTimestamp >= currentEndDate) {
                    mergedSeqNums.or(entry.getValue());
                }
            }

            DateWindow key = new DateWindow();
            key.startDateTimestamp = currentStartDate.longValue();
            key.endDateTimestamp = currentEndDate.longValue();

            List<com.netflix.vms.transformer.hollowoutput.Integer> seqNums = new ArrayList<>();
            int currentSeqNum = mergedSeqNums.nextSetBit(0);
            while (currentSeqNum != -1) {
                seqNums.add(new com.netflix.vms.transformer.hollowoutput.Integer(currentSeqNum));
                currentSeqNum = mergedSeqNums.nextSetBit(currentSeqNum + 1);
            }

            if (!seqNums.isEmpty())
                mergedWindowSeqNumMap.put(key, seqNums);
        }

        return mergedWindowSeqNumMap;
    }
}
