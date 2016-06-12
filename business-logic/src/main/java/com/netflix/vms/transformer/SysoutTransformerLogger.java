package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerLogger;
import java.util.Collection;

public class SysoutTransformerLogger implements TransformerLogger {

    @Override
    public void log(Severity severity, Collection<LogTag> tags, String message, Throwable th) {
        for(LogTag tag : tags) {
            System.out.format("%s: %s: %s\n", severity.toString(), tag.toString(), message);
        }

        if(th != null)
            th.printStackTrace();
    }

}
