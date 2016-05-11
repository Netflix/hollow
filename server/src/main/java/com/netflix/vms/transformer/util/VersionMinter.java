package com.netflix.vms.transformer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class VersionMinter {

    public long mintANewVersion() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Long.parseLong(dateFormat.format(new Date()));
    }

}
