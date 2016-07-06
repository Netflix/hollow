package com.netflix.vms.transformer;

import java.util.Collection;

import com.netflix.vms.io.TaggingLogger;

public class SysoutTransformerLogger implements TaggingLogger {
    @Override
    public void log(Severity severity, Collection<LogTag> tags, String message, Object... args) {
        Throwable cause = null;
        if (args.length > 0) {
            Object o = args[args.length-1];
            if (Throwable.class.isAssignableFrom(o.getClass())) cause = (Throwable)o;
        }
        for(LogTag tag : tags) {
            System.out.format("%s\t%s: %s\n", severity.toString(), tag.toString(), message);
        }

        if(cause != null) cause.printStackTrace();
    }
}
