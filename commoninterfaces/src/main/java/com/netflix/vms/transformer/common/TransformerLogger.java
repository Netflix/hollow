package com.netflix.vms.transformer.common;

public interface TransformerLogger {

    public void info(String messageType, String message);
    public void warn(String messageType, String message);
    public void error(String messageType, String message);
    public void error(String messageType, String message, Throwable th);

}
