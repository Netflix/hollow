package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.common.VersionMinter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceVersionMinter implements VersionMinter {
    
    private AtomicInteger versionCounter = new AtomicInteger();

    public long mintANewVersion() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = dateFormat.format(new Date());
        
        String versionStr = formattedDate + String.format("%03d", versionCounter.incrementAndGet() % 1000);
        
        return Long.parseLong(versionStr);
    }

}
