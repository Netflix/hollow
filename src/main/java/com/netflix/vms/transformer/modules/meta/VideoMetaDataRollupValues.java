package com.netflix.vms.transformer.modules.meta;

public class VideoMetaDataRollupValues {

    private boolean doShow = false;
    private boolean doSeason = false;

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
