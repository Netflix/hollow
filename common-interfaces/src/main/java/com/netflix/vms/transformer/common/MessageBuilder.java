package com.netflix.vms.transformer.common;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    private final Map<String, Object> keyValueMap = new HashMap<>();
    private final String message;

    public MessageBuilder() {
        this(null);
    }

    public MessageBuilder(String message) {
        this.message = message;
    }

    public void put(String k, Object v) {
        if (k == null) {
            return;
        }

        if (v == null) {
            keyValueMap.put(k, "");
        } else {
            keyValueMap.put(k, v);
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (message != null) {
            buffer.append(message).append(" ");
        }
        for (String k : keyValueMap.keySet()) {
            buffer.append(k).append("=").append(keyValueMap.get(k).toString()).append(" ");
        }
        return buffer.toString();
    }
}
