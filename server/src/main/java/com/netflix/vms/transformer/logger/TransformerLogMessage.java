package com.netflix.vms.transformer.logger;

import com.netflix.vms.logging.TaggingLogger.LogTag;
import com.netflix.vms.logging.TaggingLogger.Severity;

class TransformerLogMessage {
    private final EventInfo eventInfo;
    private final String message;

    public TransformerLogMessage(Severity logLevel, LogTag tag, String message, String currentCycle, long timestamp, String instanceId) {
        this.eventInfo = new EventInfo(logLevel, tag, currentCycle, timestamp, instanceId);
        this.message = message;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public String getMessage() {
        return message;
    }

    static class EventInfo {
        private final Severity logLevel;
        private final LogTag tag;
        private final String currentCycle;
        private final long timestamp;
        private final String instanceId;

        public EventInfo(Severity logLevel, LogTag tag, String currentCycle, long timestamp, String instanceId) {
            this.logLevel = logLevel;
            this.tag = tag;
            this.currentCycle = currentCycle;
            this.timestamp = timestamp;
            this.instanceId = instanceId;
        }

        public Severity getLogLevel() {
            return logLevel;
        }

        public LogTag getTag() {
            return tag;
        }

        public String getCurrentCycle() {
            return currentCycle;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getInstanceId() {
            return instanceId;
        }
    }
}
