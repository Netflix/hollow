package com.netflix.vms.transformer.logmessage;

import com.netflix.vms.transformer.common.MessageBuilder;

public class ProgressMessage {
    private MessageBuilder msgBuilder = new MessageBuilder();

    public ProgressMessage(int processedCount, int progressDivisor) {
        msgBuilder.put("finished percent", (processedCount / progressDivisor));
        msgBuilder.put("processed count", processedCount);
    }

    public ProgressMessage(int processedCount) {
        msgBuilder.put("finished percent", 100);
        msgBuilder.put("processed count", processedCount);
    }

    @Override
    public String toString() {
        return msgBuilder.toString();
    }
}
