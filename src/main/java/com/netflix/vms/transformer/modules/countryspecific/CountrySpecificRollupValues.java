package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.util.RollupValues;

import java.util.Set;

public class CountrySpecificRollupValues extends RollupValues {
    
    private boolean showEpisodeFound = false;
    private boolean seasonEpisodeFound = false;
    
    private int showBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
    private int seasonBundledAssetFromFirstAvailableEpisode = Integer.MIN_VALUE;
    private int showBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
    private int seasonBundledAssetFromFirstUnavailableEpisode = Integer.MIN_VALUE;
    
    private Set<String> aggregatedShowAssetCodes;
    private Set<String> aggregatedSeasonAssetCodes;
    
    @Override
    public void setDoShow(boolean doShow) {
        super.setDoShow(doShow);
        if(!doShow)
            showEpisodeFound = false;
    }

    @Override
    public void setDoSeason(boolean doSeason) {
        super.setDoSeason(doSeason);
        if(!doSeason)
            seasonEpisodeFound = false;
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

}
