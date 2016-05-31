package com.netflix.vms.transformer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class VersionMinter {
    
    private int versionCounter = 0;

    public long mintANewVersion() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = dateFormat.format(new Date());
        
        String versionStr = formattedDate + String.format("%03d", ++versionCounter % 1000);
        
        return Long.parseLong(versionStr);
    }

}
