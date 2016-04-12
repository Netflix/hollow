package com.netflix.vms.transformer.util;

public class RollupValues {

    private boolean doShow = false;
    private boolean doSeason = false;
    
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
