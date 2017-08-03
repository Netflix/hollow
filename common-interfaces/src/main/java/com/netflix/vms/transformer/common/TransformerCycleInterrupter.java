package com.netflix.vms.transformer.common;

import java.util.Map;

public interface TransformerCycleInterrupter {
    void begin(long cycleId);

    void interruptCycle(String msg);

    boolean isCycleInterrupted();

    String getCycleInterruptMsg();

    void triggerInterrupt(long cycleId, String message) throws CycleInterruptException;

    Map<Long, CycleInterruptEntry> getHistory();

    String getHistoryAsString();

    void reset(long cycleId);

    public static class CycleInterruptEntry {
        private final Long cycleId;
        private String message;
        private boolean isTriggered;
        private boolean isReset;

        public CycleInterruptEntry(Long cycleId, String message) {
            super();
            this.cycleId = cycleId;
            this.message = message;
        }

        public Long getCycleId() {
            return cycleId;
        }

        public void updateMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void triggered() {
            isTriggered = true;
        }

        public boolean isTriggered() {
            return isTriggered;
        }

        public void reset() {
            isReset = true;
        }

        public boolean isReset() {
            return isReset;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("CycleInterrupt [cycleId=");
            builder.append(cycleId);
            builder.append(", message=");
            builder.append(message);
            builder.append(", isTriggered=");
            builder.append(isTriggered);
            builder.append(", isReset=");
            builder.append(isReset);
            builder.append("]");
            return builder.toString();
        }
    }

    public static class CycleInterruptException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public CycleInterruptException(String message) {
            super(message);
        }
    }
}