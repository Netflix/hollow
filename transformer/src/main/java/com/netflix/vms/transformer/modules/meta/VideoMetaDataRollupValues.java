package com.netflix.vms.transformer.modules.meta;

import com.netflix.vms.transformer.util.RollupValues;


public class VideoMetaDataRollupValues extends RollupValues {

    private long earliestFirstDisplayDate = Long.MAX_VALUE;
    private long latestLiveFirstDisplayDate;

    private int showLatestYear;
    private int seasonLatestYear;

    public void newSeason() {
        seasonLatestYear = 0;
    }

    public void newLatestYear(int latestYear) {
        if(latestYear > seasonLatestYear)
            seasonLatestYear = latestYear;
        if(latestYear > showLatestYear)
            showLatestYear = latestYear;
    }

    public void newPotentiallyEarliestFirstDisplayDate(long firstDisplayDate) {
        if(firstDisplayDate < earliestFirstDisplayDate)
            earliestFirstDisplayDate = firstDisplayDate;
    }

    public void newPotentiallyLatestFirstDisplayDate(long firstDisplayDate) {
        if(firstDisplayDate > latestLiveFirstDisplayDate)
            latestLiveFirstDisplayDate = firstDisplayDate;
    }

    public long getEarliestFirstDisplayDate() {
        return earliestFirstDisplayDate;
    }

    public long getLatestLiveFirstDisplayDate() {
        return latestLiveFirstDisplayDate;
    }

    public int getSeasonLatestYear() {
        return seasonLatestYear;
    }

    public int getShowLatestYear() {
        return showLatestYear;
    }

}
