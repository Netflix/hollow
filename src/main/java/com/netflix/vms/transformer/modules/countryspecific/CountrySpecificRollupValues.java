package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.util.RollupValues;

import java.util.HashSet;
import java.util.Set;

public class CountrySpecificRollupValues extends RollupValues {

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
    
    int showPrePromoDays = 0;
    int seasonPrePromoDays = 0;

    public void reset() {
        resetSeason();
        resetShow();
    }
    
    public void resetSeason() {
        seasonEpisodeFound = false;
        aggregatedSeasonAssetCodes = new HashSet<Strings>();
        seasonVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        seasonPrePromoDays = 0;
        seasonCupTokensFromFirstStreamableEpisode = null;
    }
    
    public void resetShow() {
        showEpisodeFound = false;
        aggregatedShowAssetCodes = new HashSet<Strings>();
        showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        showPrePromoDays = 0;
        showCupTokensFromFirstStreamableEpisode = null;
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

    public void newAssetBcp47Codes(Set<Strings> assetCodes) {
        aggregatedShowAssetCodes.addAll(assetCodes);
        aggregatedSeasonAssetCodes.addAll(assetCodes);
    }

    public void newVideoFormatDescriptors(Set<VideoFormatDescriptor> videoFormatDescriptors) {
        showVideoFormatDescriptors.addAll(videoFormatDescriptors);
        seasonVideoFormatDescriptors.addAll(videoFormatDescriptors);
    }

    public void newEpisodeData(boolean isGoLive, int bundledAssetId) {
        if(isGoLive) {
            if(showBundledAssetFromFirstAvailableEpisode == Integer.MIN_VALUE)
                showBundledAssetFromFirstAvailableEpisode = bundledAssetId;
            if(seasonBundledAssetFromFirstAvailableEpisode == Integer.MIN_VALUE)
                seasonBundledAssetFromFirstAvailableEpisode = bundledAssetId;
        } else {
            if(showBundledAssetFromFirstUnavailableEpisode == Integer.MIN_VALUE)
                showBundledAssetFromFirstUnavailableEpisode = bundledAssetId;
            if(seasonBundledAssetFromFirstUnavailableEpisode == Integer.MIN_VALUE)
                seasonBundledAssetFromFirstUnavailableEpisode = bundledAssetId;
        }
    }
    
    public void newPrePromoDays(int prePromoDays) {
        if(showPrePromoDays == 0 || prePromoDays < showPrePromoDays)
            showPrePromoDays = prePromoDays;
        if(seasonPrePromoDays == 0 || prePromoDays < seasonPrePromoDays)
            seasonPrePromoDays = prePromoDays;
    }
    
    public void newCupTokens(LinkedHashSetOfStrings cupTokens) {
        if(showCupTokensFromFirstStreamableEpisode == null)
            showCupTokensFromFirstStreamableEpisode = cupTokens;
        if(seasonCupTokensFromFirstStreamableEpisode == null)
            seasonCupTokensFromFirstStreamableEpisode = cupTokens;
    }

    public int getFirstEpisodeBundledAssetId() {
        if(doSeason()) {
            if(seasonBundledAssetFromFirstAvailableEpisode != Integer.MIN_VALUE)
                return seasonBundledAssetFromFirstAvailableEpisode;
            return seasonBundledAssetFromFirstUnavailableEpisode;
        }

        if(showBundledAssetFromFirstAvailableEpisode != Integer.MIN_VALUE)
            return showBundledAssetFromFirstAvailableEpisode;
        return showBundledAssetFromFirstUnavailableEpisode;
    }

    public Set<Strings> getAssetBcp47Codes() {
        if(doSeason())
            return aggregatedSeasonAssetCodes;
        return aggregatedShowAssetCodes;
    }

    public Set<VideoFormatDescriptor> getVideoFormatDescriptors() {
        if(doSeason())
            return seasonVideoFormatDescriptors;
        return showVideoFormatDescriptors;
    }
    
    public int getPrePromoDays() {
        if(doSeason())
            return seasonPrePromoDays;
        return showPrePromoDays;
    }
    
    public LinkedHashSetOfStrings getCupTokens() {
        if(doSeason())
            return seasonCupTokensFromFirstStreamableEpisode;
        return showCupTokensFromFirstStreamableEpisode;
    }

}
