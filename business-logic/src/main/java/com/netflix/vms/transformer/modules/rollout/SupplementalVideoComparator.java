package com.netflix.vms.transformer.modules.rollout;

import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import java.util.Comparator;

public class SupplementalVideoComparator implements Comparator<SupplementalVideo> {

    @Override
    public int compare(SupplementalVideo o1, SupplementalVideo o2) {
        return Integer.valueOf(o1.sequenceNumber).compareTo(o2.sequenceNumber);
    }

}
