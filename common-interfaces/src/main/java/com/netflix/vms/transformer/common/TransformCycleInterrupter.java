package com.netflix.vms.transformer.common;

import com.netflix.vms.logging.TaggingLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TransformCycleInterrupter {
    void begin(long cycleId);

    void interruptCycle(String msg);

    boolean isCycleInterrupted();

    void pauseCycle(boolean isPaused);

    boolean isCyclePaused();

    String getCycleInterruptMsg();

    /**
     * Throw CycleInterruptException if Cycle Interrupt was requested
     *
     * @see #interruptCycle(String)
     * @see #isCycleInterrupted()
     */
    void triggerInterruptIfNeeded(long cycleId, TaggingLogger logger, String message) throws CycleInterruptException;

    Map<Long, CycleInterruptEntry> getHistory();

    String getHistoryAsString();

    void reset(long cycleId);

    public static class CycleInterruptEntry {
        private final Long cycleId;
        private List<String> messages = new ArrayList<>();
        private boolean isTriggered;
        private boolean isReset;

        public CycleInterruptEntry(Long cycleId, String message) {
            super();
            this.cycleId = cycleId;
            this.messages.add(message);
        }

        public Long getCycleId() {
            return cycleId;
        }

        public void appendMessage(String message) {
            this.messages.add(message);
        }

        public String getMessage() {
            if (messages.size() == 0) return messages.get(0);

            return messages.toString();
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
            builder.append(", isTriggered=");
            builder.append(isTriggered);
            builder.append(", isReset=");
            builder.append(isReset);
            builder.append(", message=");
            builder.append(messages);
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