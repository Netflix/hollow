package com.netflix.vms.transformer.util;

public class RollupValues {

    private boolean doShow = false;
    private boolean doSeason = false;
    private boolean doEpisode = false;
    
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
    
    public void setDoEpisode(boolean doEpisode) {
        this.doEpisode = doEpisode;
    }
    
    public boolean doEpisode() {
        return this.doEpisode;
    }

}
