package com.netflix.vms.transformer.util;

import java.util.function.Supplier;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class VersionMinter implements Supplier<Long> {

    @Override
    public Long get() {
        return mintANewVersion();
    }

    private long mintANewVersion() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Long.parseLong(dateFormat.format(new Date()));
    }

}
