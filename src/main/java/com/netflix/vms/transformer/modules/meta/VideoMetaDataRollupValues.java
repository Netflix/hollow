package com.netflix.vms.transformer.modules.meta;


public class VideoMetaDataRollupValues {

    private boolean doShow = false;
    private boolean doSeason = false;

    private long earliestFirstDisplayDate = Long.MAX_VALUE;
    private long latestLiveFirstDisplayDate;

    private int showLatestYear;
    private int seasonLatestYear;

    public void newSeason() {
        this.doSeason = false;
        seasonLatestYear = 0;
    }

    public void newShow() {
        this.doShow = false;
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

    public void setDoShow(boolean doShow) {
        this.doShow = doShow;
    }

    public boolean doShow() {
        return this.doShow;
    }

    public void setDoSeason(boolean doSeason) {
        this.doSeason = doSeason;
    }

    public boolean doSeason() {
        return this.doSeason;
    }

}
