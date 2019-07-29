package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.CycleInterrupted;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformCycleInterrupter;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public class SimpleTransformCycleInterrupter implements TransformCycleInterrupter {
    public static TransformCycleInterrupter INSTANCE = new SimpleTransformCycleInterrupter();
    private static final int HISTORY_LIMIT = 10;

    private Long cycleId;
    private boolean isInterrupted;
    private boolean isCyclePaused;
    private String interruptMsg;

    private TreeMap<Long, CycleInterruptEntry> historyMap = new TreeMap<>();

    @Inject
    public SimpleTransformCycleInterrupter() {
    }

    @Override
    public void begin(long cycleId) {
        this.cycleId = cycleId;
    }

    @Override
    public void pauseCycle(boolean isPaused) {
        this.isCyclePaused = isPaused;
    }

    @Override
    public boolean isCyclePaused() {
        return isCyclePaused;
    }

    @Override
    public synchronized void interruptCycle(String msg) {
        if (cycleId == null) throw new RuntimeException("No cycle has started yet");

        isInterrupted = true;
        interruptMsg = msg;

        addEntry(cycleId, msg);
    }

    private synchronized CycleInterruptEntry addEntry(long cycleId, String msg) {
        CycleInterruptEntry entry = new CycleInterruptEntry(cycleId, msg);
        historyMap.put(cycleId, entry);
        if (historyMap.size() > HISTORY_LIMIT) {
            Long firstKey = historyMap.firstKey();
            historyMap.remove(firstKey);
        }
        return entry;
    }

    @Override
    public boolean isCycleInterrupted() {
        return isInterrupted;
    }

    @Override
    public String getCycleInterruptMsg() {
        return interruptMsg;
    }

    @Override
    public synchronized void reset(long cycleId) {
        isInterrupted = false;
        interruptMsg = "";

        CycleInterruptEntry cycleInterruptEntry = historyMap.get(cycleId);
        if (cycleInterruptEntry != null) cycleInterruptEntry.reset();
    }

    @Override
    public synchronized void triggerInterruptIfNeeded(long cycleId, TaggingLogger logger, String message) throws CycleInterruptException {
        if (!isCycleInterrupted()) return;

        CycleInterruptEntry cycleInterruptEntry = historyMap.get(cycleId);
        if (cycleInterruptEntry == null) {
            cycleInterruptEntry = addEntry(cycleId, message);
        } else {
            cycleInterruptEntry.appendMessage(message);
        }
        cycleInterruptEntry.triggered();

        logger.error(CycleInterrupted, message);
        throw new CycleInterruptException(cycleInterruptEntry.getMessage());
    }

    @Override
    public Map<Long, CycleInterruptEntry> getHistory() {
        return historyMap;
    }

    @Override
    public String getHistoryAsString() {
        if (historyMap.isEmpty()) return "";

        StringBuilder sb = new StringBuilder(String.format("HISTORY (last %s):", HISTORY_LIMIT));
        int i = 0;
        for (Map.Entry<Long, CycleInterruptEntry> item : historyMap.entrySet()) {
            sb.append(String.format("\n    %s) %s", ++i, item));
        }
        return sb.toString();
    }
}