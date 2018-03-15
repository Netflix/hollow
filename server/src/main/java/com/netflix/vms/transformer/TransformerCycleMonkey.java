package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerCycleMonkey {
    private final TransformerContext ctx;
    private final Map<String, Integer> phaseMap = new HashMap<>();
    private final AtomicInteger phaseCounter = new AtomicInteger(0);
    private int chaosPhase = 1;
    private int cycleCount = 0;

    public TransformerCycleMonkey(TransformerContext ctx) {
        this.ctx = ctx;
    }

    public void cycleBegin() {
        cycleCount++;
    }

    private int getPhaseNum(String phaseName) {
        Integer phaseNum = phaseMap.get(phaseName);
        if (phaseNum == null) {
            phaseNum = phaseCounter.incrementAndGet();
            phaseMap.put(phaseName, phaseNum);
        }
        return phaseNum;
    }

    private boolean isGoChaosOnPhase(int phaseNum) {
        if (phaseNum == chaosPhase) {
            chaosPhase += 2; // Do every other fase
            if (chaosPhase > phaseMap.size()) chaosPhase = 1;
            return true;
        }
        return false;
    }

    public void doMonkeyBusiness(String phaseName) {
        if (cycleCount % 2 == 0) return; // Only trigger monkey business on odd cycles
        if (!ctx.getConfig().isTransformerCycleMonkeyEnabled()) return;

        synchronized (this) {
            int phaseNum = getPhaseNum(phaseName);
            if (isGoChaosOnPhase(phaseNum)) {
                ctx.getLogger().error(TransformerLogTag.TransformCycleMonkey, "TransformerCycleMonkey forcefully caused chaos to break cycle on cycle({}), phase({})", cycleCount, phaseName);
                throw new RuntimeException("TransformerCycleMonkey forcefully caused chaos to break cycle on phase:" + phaseName);
            }
        }

    }

}
