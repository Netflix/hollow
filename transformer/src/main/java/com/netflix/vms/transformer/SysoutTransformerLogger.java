package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerLogger;

public class SysoutTransformerLogger implements TransformerLogger {

    @Override
    public void info(String messageType, String message) {
        System.out.format("INFO: %s: %s\n", messageType, message);
    }

    @Override
    public void warn(String messageType, String message) {
        System.out.format("WARN: %s: %s\n", messageType, message);
    }

    @Override
    public void error(String messageType, String message) {
        System.out.format("ERROR: %s: %s\n", messageType, message);
    }

    @Override
    public void error(String messageType, String message, Throwable th) {
        System.out.format("ERROR: %s: %s\n", messageType, message);
        th.printStackTrace();
    }


}
