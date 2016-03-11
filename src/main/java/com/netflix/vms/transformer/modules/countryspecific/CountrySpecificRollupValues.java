package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;

import com.netflix.memory.tool.HashSet;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.util.RollupValues;
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

    @Override
    public void setDoShow(boolean doShow) {
        super.setDoShow(doShow);
        if(!doShow) {
            showEpisodeFound = false;
            aggregatedShowAssetCodes = new HashSet<Strings>();
            showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        }
    }

    @Override
    public void setDoSeason(boolean doSeason) {
        super.setDoSeason(doSeason);
        if(!doSeason) {
            seasonEpisodeFound = false;
            aggregatedSeasonAssetCodes = new HashSet<Strings>();
            seasonVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        }
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

}
