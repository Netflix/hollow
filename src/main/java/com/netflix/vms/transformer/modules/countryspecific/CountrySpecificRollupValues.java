package com.netflix.vms.transformer.modules.countryspecific;

import java.util.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netflix.vms.transformer.hollowoutput.VideoImage;
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

    private Map<Strings, List<VideoImage>> showFirstEpisodeVideoImagesMap = Collections.emptyMap();
    private Map<Strings, List<VideoImage>> seasonFirstEpisodeVideoImagesMap = Collections.emptyMap();
    private Map<Strings, List<VideoImage>> showLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
    private Map<Strings, List<VideoImage>> seasonLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();

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
        seasonFirstEpisodeVideoImagesMap = Collections.emptyMap();
        seasonLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
    }

    public void resetShow() {
        showEpisodeFound = false;
        aggregatedShowAssetCodes = new HashSet<Strings>();
        showVideoFormatDescriptors = new HashSet<VideoFormatDescriptor>();
        showPrePromoDays = 0;
        showCupTokensFromFirstStreamableEpisode = null;
        showFirstEpisodeVideoImagesMap = Collections.emptyMap();
        showLevelTaggedVideoImagesRollup = new HashMap<Strings, List<VideoImage>>();
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

    public void newEpisodeStillImagesByTypeMap(Map<Strings, List<VideoImage>> map) {
        if(showFirstEpisodeVideoImagesMap.isEmpty())
            showFirstEpisodeVideoImagesMap = map;
        if(seasonFirstEpisodeVideoImagesMap.isEmpty())
            seasonFirstEpisodeVideoImagesMap = map;

        for(Map.Entry<Strings, List<VideoImage>> entry : map.entrySet()) {
            for(VideoImage img : entry.getValue()) {
                for(Strings momentTag : img.videoMoment.momentTags) {
                    if(new String(momentTag.value).equals("show_level")) {
                        newTagBasedShowLevelVideoImage(entry.getKey(), img);
                        break;
                    }
                }
            }
        }
    }

    private void newTagBasedShowLevelVideoImage(Strings type, VideoImage img) {
        List<VideoImage> list = showLevelTaggedVideoImagesRollup.get(type);
        if(list == null) {
            list = new ArrayList<VideoImage>();
            showLevelTaggedVideoImagesRollup.put(type, list);
        }
        list.add(img);

        list = seasonLevelTaggedVideoImagesRollup.get(type);
        if(list == null) {
            list = new ArrayList<VideoImage>();
            seasonLevelTaggedVideoImagesRollup.put(type, list);
        }
        list.add(img);
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

    public Map<Strings, List<VideoImage>> getVideoImageMap() {
        if(doSeason()) {
            if(seasonLevelTaggedVideoImagesRollup.isEmpty())
                return seasonFirstEpisodeVideoImagesMap;
            return seasonLevelTaggedVideoImagesRollup;
        }

        if(showLevelTaggedVideoImagesRollup.isEmpty())
            return showFirstEpisodeVideoImagesMap;
        return showLevelTaggedVideoImagesRollup;

    }

}
