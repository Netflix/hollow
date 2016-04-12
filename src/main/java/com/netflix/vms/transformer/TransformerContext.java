package com.netflix.vms.transformer;

/**
 * Properties go here.
 *
 */
public class TransformerContext {

    private long now = System.currentTimeMillis();
    
    public void setNowMillis(long now) {
        this.now = now;
    }
    
    public long getNowMillis() {
        return now;
    }
    
}
