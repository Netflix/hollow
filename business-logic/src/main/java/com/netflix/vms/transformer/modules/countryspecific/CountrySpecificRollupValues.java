package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.DateWindow;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
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

    private Set<Strings> aggregatedShowAssetCodes = new HashSet<>();
    private Set<Strings> aggregatedSeasonAssetCodes = new HashSet<>();

    private Set<VideoFormatDescriptor> showVideoFormatDescriptors = new HashSet<>();
    private Set<VideoFormatDescriptor> seasonVideoFormatDescriptors = new HashSet<>();

    private LinkedHashSetOfStrings showCupTokensFromFirstStreamableEpisode = null;
    private LinkedHashSetOfStrings seasonCupTokensFromFirstStreamableEpisode = null;

    private int seasonSequenceNumber = 0;
    private Map<DateWindow, BitSet> seasonSequenceNumberMap = new HashMap<>();

    private int showPrePromoDays = 0;
    private int seasonPrePromoDays = 0;
    private boolean showHasRollingEpisodes = false;;
    private boolean seasonHasRollingEpisodes = false;
    private boolean showIsAvailableForDownload = false;
    private boolean seasonIsAvailableForDownload = false;
    
    private DateWindowAggregator showWindowAggregator = new DateWindowAggregator();
    private DateWindowAggregator seasonWindowAggregator = new DateWindowAggregator();
    
    private long maxInWindowLaunchDate = 0;
    
    private boolean viewableFoundLocalAudio = false;
    private boolean seasonFoundLocalAudio = false;
    private boolean showFoundLocalAudio = false;
    private boolean showFoundLocalText = false;
    private boolean seasonFoundLocalText = false;
    private boolean viewableFoundLocalText = false;
    
    private long episodeLaunchDate = -1L;
    
    private long seasonEarliestSchedulePhaseDate = Long.MAX_VALUE;
    private long showEarliestSchedulePhaseDate = Long.MAX_VALUE;
    
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
        seasonBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
        seasonBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
        seasonFoundLocalAudio = false;
        seasonFoundLocalText = false;
        seasonWindowAggregator.reset();
        seasonEarliestSchedulePhaseDate = Long.MAX_VALUE;
    }

    public void resetShow() {
        showEpisodeFound = false;
        aggregatedShowAssetCodes = new HashSet<Strings>();
        showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        showPrePromoDays = 0;
        showHasRollingEpisodes = false;
        showIsAvailableForDownload = false;
        showCupTokensFromFirstStreamableEpisode = null;
        seasonSequenceNumberMap = new HashMap<>();
        showBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
        showBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
        maxInWindowLaunchDate = 0;
        showFoundLocalAudio = false;
        showFoundLocalText = false;
        showWindowAggregator.reset();
        showEarliestSchedulePhaseDate = Long.MAX_VALUE;
    }
    
    public void resetViewable() {
        viewableFoundLocalAudio = false;
        viewableFoundLocalText = false;
        episodeLaunchDate = -1L;
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
    
    public void windowFound(long startDate, long endDate) {
        showWindowAggregator.addDateWindow(startDate, endDate);
        seasonWindowAggregator.addDateWindow(startDate, endDate);
    }
    
    public DateWindow getValidShowWindow(long startDate, long endDate) {
        showWindowAggregator.mergeDateWindows();
        return showWindowAggregator.matchDateWindowAgainstMergedDateWindows(startDate, endDate);
    }
    
    public DateWindow getValidSeasonWindow(long startDate, long endDate) {
        seasonWindowAggregator.mergeDateWindows();
        return seasonWindowAggregator.matchDateWindowAgainstMergedDateWindows(startDate, endDate);
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

    public void newSeasonWindow(long startDate, long endDate, boolean onHold, int sequenceNumber) {
        DateWindow dateWindow = new DateWindow();
        dateWindow.startDateTimestamp = startDate;
        dateWindow.endDateTimestamp = endDate;
        dateWindow.onHold = onHold;

        BitSet seasonSeqNums = seasonSequenceNumberMap.get(dateWindow);
        if (seasonSeqNums == null) {
            seasonSeqNums = new BitSet();
            seasonSequenceNumberMap.put(dateWindow, seasonSeqNums);
        }

        seasonSeqNums.set(sequenceNumber);
    }
    
    public void newEpisodeLaunchDate(long launchDate) {
        episodeLaunchDate = launchDate;
    }
    
    public void newInWindowAvailabilityDate(long startDate) {
        if(episodeLaunchDate != -1L) {
            if(episodeLaunchDate > maxInWindowLaunchDate)
                maxInWindowLaunchDate = episodeLaunchDate;
        } else if(startDate > maxInWindowLaunchDate) {
            maxInWindowLaunchDate = startDate;
        }
    }
    
    public long getMaxInWindowLaunchDate() {
        return maxInWindowLaunchDate;
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
            boolean onHold = false;
            mergedSeqNums.clear();

            for (Map.Entry<DateWindow, BitSet> entry : seasonSequenceNumberMap.entrySet()) {
                if (entry.getKey().startDateTimestamp <= currentStartDate && entry.getKey().endDateTimestamp >= currentEndDate) {
                    mergedSeqNums.or(entry.getValue());
                    /// assumption: windows which are on hold are separated by large amounts of time, no overlapping onHold with not onHold windows.
                    if(entry.getKey().onHold)
                        onHold = true;
                }
            }

            DateWindow key = new DateWindow();
            key.startDateTimestamp = currentStartDate.longValue();
            key.endDateTimestamp = currentEndDate.longValue();
            key.onHold = onHold;

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
    
    public void foundLocalAudio() {
        showFoundLocalAudio = true;
        seasonFoundLocalAudio = true;
        viewableFoundLocalAudio = true;
    }
    
    public void foundLocalText() {
        showFoundLocalText = true;
        seasonFoundLocalText = true;
        viewableFoundLocalText = true;
    }

    public boolean isFoundLocalAudio() {
        if(doSeason())
            return seasonFoundLocalAudio;
        if(doShow())
            return showFoundLocalAudio;
        return viewableFoundLocalAudio;
    }

    public boolean isFoundLocalText() {
        if(doShow())
            return showFoundLocalText;
        if(doSeason())
            return seasonFoundLocalText;
        return viewableFoundLocalText;
    }

    public boolean isSeasonFoundLocalText() {
        return seasonFoundLocalText;
    }

    public boolean isEpisodeFoundLocalText() {
        return viewableFoundLocalText;
    }
    
    public void newEarliestScheduledPhaseDate(long date) {
        if(date < seasonEarliestSchedulePhaseDate)
            seasonEarliestSchedulePhaseDate = date;
        if(date < showEarliestSchedulePhaseDate)
            showEarliestSchedulePhaseDate = date;
    }
    
    public long getRolledUpEarliestScheduledPhaseDate() {
        if(doShow())
            return showEarliestSchedulePhaseDate;
        if(doSeason())
            return seasonEarliestSchedulePhaseDate;
        return Long.MAX_VALUE;
    }

}
