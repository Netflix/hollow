package com.netflix.hollow.tools.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadContextHolder {
    private static final ThreadLocal<Map<String, String>> threadContext = ThreadLocal.withInitial(HashMap::new);
    public static Map<String, String> getThreadContext() {
        return threadContext.get();
    }

    public static void setThreadContext(Map<String, String> threadContext2) {
        threadContext.set(threadContext2);
    }

    public static void clearThreadContext() {
        threadContext.get().clear();
    }
}
