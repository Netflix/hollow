package com.netflix.vms.transformer;

import com.netflix.vms.transformer.common.CycleMonkey;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TransformerCycleMonkey implements CycleMonkey {
    public static final CycleMonkey SIMPLE_CYCLE_MONKEY = new CycleMonkey() {
        @Override public void cycleBegin() { }
        @Override public void doMonkeyBusiness(String phaseName) { }
    };

    private final TransformerContext ctx;
    private final Map<String, Integer> phaseMap = new LinkedHashMap<>();
    private final Set<String> triggeredPhases = new HashSet<>();

    private int cycleCount = 0;
    private int phaseCounter = 1; // phase number starts at 1
    private int chaosPhase = 1;

    private boolean isEnabled = false;
    private boolean isAutoChaosEnabled = false;

    public TransformerCycleMonkey(TransformerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void cycleBegin() {
        cycleCount++;

        isEnabled = ctx.getConfig().isTransformerCycleMonkeyEnabled();
        isAutoChaosEnabled = ctx.getConfig().isTransformerCycleMonkeyAutoChaosEnabled();
    }

    private synchronized int getPhaseNum(String phaseName) {
        Integer phaseNum = phaseMap.get(phaseName);
        if (phaseNum == null) {
            phaseNum = phaseCounter++;
            phaseMap.put(phaseName, phaseNum);
        }
        return phaseNum;
    }

    private boolean isGoChaosOnPhase(String phaseName) {
        int phaseNum = getPhaseNum(phaseName);
        if (phaseNum == chaosPhase) {
            chaosPhase++;

            // Do not trigger phases that was already triggered
            if (!triggeredPhases.contains(phaseName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doMonkeyBusiness(String phaseName) {
        if (isEnabled && cycleCount % 2 == 0)
            return; // Only trigger monkey business on odd cycles

        boolean isGoChaos = false;
        TransformerConfig config = ctx.getConfig();
        if (isAutoChaosEnabled) {
            if (config.isTransformerCycleMonkeyAutoPhaseChaosEnabled(phaseName)) {
                isGoChaos = isGoChaosOnPhase(phaseName);
            }
        } else {
            // Check for explicit configured phase chaos
            isGoChaos = config.isTransformerCycleMonkeyPhaseChaosEnabled(phaseName);
        }

        if (isGoChaos) {
            triggeredPhases.add(phaseName);
            ctx.getLogger().error(TransformerLogTag.TransformCycleMonkey, "TransformerCycleMonkey forcefully caused chaos to break cycle on cycleCount({}), phase({})", cycleCount, phaseName);
            throw new RuntimeException("TransformerCycleMonkey forcefully caused chaos to break cycle on phase:" + phaseName);
        }
    }
}