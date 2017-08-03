package com.netflix.vms.transformer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.vms.transformer.common.TransformerCycleInterrupter;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public class SimpleTransformerCycleInterrupter implements TransformerCycleInterrupter {
    public static TransformerCycleInterrupter INSTANCE = new SimpleTransformerCycleInterrupter();
    private static final int HISTORY_LIMIT = 10;

    private Long cycleId;
    private boolean isInterrupted;
    private String interruptMsg;

    private TreeMap<Long, CycleInterruptEntry> historyMap = new TreeMap<>();

    @Inject
    public SimpleTransformerCycleInterrupter() {
    }

    @Override
    public void begin(long cycleId) {
        this.cycleId = cycleId;
    }

    @Override
    public synchronized void interruptCycle(String msg) {
        if (cycleId == null) throw new RuntimeException("No cycle has started yet");

        isInterrupted = true;
        interruptMsg = msg;

        historyMap.put(cycleId, new CycleInterruptEntry(cycleId, msg));
        if (historyMap.size() > HISTORY_LIMIT) {
            Long firstKey = historyMap.firstKey();
            historyMap.remove(firstKey);
        }
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
    public synchronized void triggerInterrupt(long cycleId, String message) throws CycleInterruptException {
        CycleInterruptEntry cycleInterruptEntry = historyMap.get(cycleId);
        if (cycleInterruptEntry == null) {
            cycleInterruptEntry = new CycleInterruptEntry(cycleId, message);
            historyMap.put(cycleId, cycleInterruptEntry);
        } else {
            cycleInterruptEntry.updateMessage(message + " : " + interruptMsg);
        }
        cycleInterruptEntry.triggered();

        throw new CycleInterruptException(message + " : " + interruptMsg);
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