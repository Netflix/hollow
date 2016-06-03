package com.netflix.vms.transformer.common.publish.workflow;

import com.netflix.config.NetflixConfiguration.RegionEnum;

public interface VipAnnouncer {
    boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion);

    boolean announce(String vip, RegionEnum region, boolean canary, long announceVersion, long priorVersion);

    long getPreviouslyAnnouncedCanaryVersion(String vip);
}
