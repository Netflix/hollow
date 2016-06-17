package com.netflix.vms.transformer.input;

import com.netflix.hollow.client.HollowClientUpdateDirector;
import com.netflix.vms.transformer.http.HttpHelper;

public class VMSInputDataProxyUpdateDirector extends HollowClientUpdateDirector {

    private final String proxyURL;
    private final VMSInputDataKeybaseBuilder keybaseBuilder;
    
    private long specifiedLatestVersion = Long.MAX_VALUE;

    public VMSInputDataProxyUpdateDirector(String proxyURL, String converterVip) {
        this.proxyURL = proxyURL;
        this.keybaseBuilder = new VMSInputDataKeybaseBuilder(converterVip);
    }

    @Override
    public long getLatestVersion() {
        if(specifiedLatestVersion != Long.MAX_VALUE)
            return specifiedLatestVersion;
        
        try {
            long snapshotVersion = Long.parseLong(HttpHelper.getStringResponse(proxyURL + "/filestore-version?keybase=" + keybaseBuilder.getSnapshotKeybase()));
            long deltaVersion = Long.parseLong(HttpHelper.getStringResponse(proxyURL + "/filestore-version?keybase=" + keybaseBuilder.getDeltaKeybase()));
            
            return Math.max(snapshotVersion, deltaVersion);
        } catch(Exception e){
            e.printStackTrace();
            return Long.MAX_VALUE;
        }
    }
    
    @Override
    public void setLatestVersion(long latestVersion) {
        this.specifiedLatestVersion = latestVersion;
    }

    @Override
    public void subscribeToEvents() { }

}
