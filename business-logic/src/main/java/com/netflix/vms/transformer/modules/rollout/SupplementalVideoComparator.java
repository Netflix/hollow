package com.netflix.vms.transformer.modules.rollout;

import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;

import java.util.Comparator;

public class SupplementalVideoComparator implements Comparator<SupplementalVideo> {

    @Override
    public int compare(SupplementalVideo o1, SupplementalVideo o2) {
        int x = o1.seasonNumber;
        int y = o2.sequenceNumber;
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

}
