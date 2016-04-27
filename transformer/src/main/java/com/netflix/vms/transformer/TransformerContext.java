package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerLogger;

/**
 * Properties go here.
 *
 */
public class TransformerContext {

    private final TransformerLogger logger;
    private long now = System.currentTimeMillis();

    public TransformerContext() {
        this(new SysoutTransformerLogger());
    }

    public TransformerContext(TransformerLogger logger) {
        this.logger = logger;
    }

    public void setNowMillis(long now) {
        this.now = now;
    }

    public long getNowMillis() {
        return now;
    }

    public TransformerLogger getLogger() {
        return logger;
    }

}
