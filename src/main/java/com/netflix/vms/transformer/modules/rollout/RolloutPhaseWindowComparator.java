package com.netflix.vms.transformer.modules.rollout;

import com.netflix.vms.transformer.hollowoutput.RolloutPhaseWindow;
import java.util.Comparator;

public class RolloutPhaseWindowComparator implements Comparator<RolloutPhaseWindow> {

    @Override
    public int compare(RolloutPhaseWindow o1, RolloutPhaseWindow o2) {
        if (o1.phaseWindow.startDate.val == o2.phaseWindow.startDate.val) {
            return new Long(o1.phaseWindow.endDate.val).compareTo(o2.phaseWindow.endDate.val);
        }
        return new Long(o1.phaseWindow.startDate.val).compareTo(o2.phaseWindow.startDate.val);
    }

}
