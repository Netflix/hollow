package com.netflix.vms.transformer.publish.workflow;

import com.netflix.config.FastProperty;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.logging.TaggingLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishRegionProvider {

    private static final FastProperty.StringProperty PUBLISH_REGION_DELAYS = new FastProperty.StringProperty(
                                                                                        "com.netflix.vms.server.blobPublishRegionDelays",
                                                                                        "EU_WEST_1=0,US_WEST_2=0,US_EAST_1=0");
    private static final FastProperty.StringProperty PUBLISH_PRIMARY_REGION = new FastProperty.StringProperty(
                                                                                        "com.netflix.vms.server.blobPublishPrimaryRegion",
                                                                                        "US_EAST_1");

    public static List<RegionEnum> ALL_REGIONS = Arrays.asList(new RegionEnum[]{ RegionEnum.US_EAST_1, RegionEnum.US_WEST_2, RegionEnum.EU_WEST_1 });

    private final RegionEnum primaryRegion;
    private final Collection<RegionEnum> nonPrimaryRegions = new ArrayList<>();
    private final Map<RegionEnum, Long> publishRegionDelays = new HashMap<>();

    public PublishRegionProvider(TaggingLogger logger) {
        this(PUBLISH_REGION_DELAYS.get(), RegionEnum.valueOf(PUBLISH_PRIMARY_REGION.get()), logger);
    }

    public PublishRegionProvider(String regionPublishDelays, RegionEnum primaryRegion, TaggingLogger logger) {
        if(!ALL_REGIONS.contains(primaryRegion)) throw new IllegalArgumentException("Invalid primary region");
        this.primaryRegion = primaryRegion;
        for(RegionEnum region : ALL_REGIONS) {
            if(!region.equals(primaryRegion)) {
                nonPrimaryRegions.add(region);
            }
        }
        readConfiguredDelays(regionPublishDelays, publishRegionDelays);
    }

    public RegionEnum getPrimaryRegion() {
        return primaryRegion;
    }

    public Collection<RegionEnum> getNonPrimaryRegions() {
        return nonPrimaryRegions;
    }

    public long getPublishDelayInSeconds(RegionEnum region) {
        return publishRegionDelays.get(region);
    }

    void readConfiguredDelays(String regionDelays, Map<RegionEnum, Long> publishDelays) {
        for(RegionEnum region : ALL_REGIONS) {
            publishDelays.put(region, 0L);
        }
        if(regionDelays == null || regionDelays.isEmpty()) {
            return;
        }
        for(String regionDelay : regionDelays.split(",")) {
            String[] splits = regionDelay.split("=");
            long delay = 0;
            RegionEnum region = null;
            try {
                region = RegionEnum.valueOf(splits[0].trim());
                delay = Long.valueOf(splits[1].trim());
            }catch(Throwable e) {
                throw new IllegalArgumentException("Publish delay property is mis-configured: " + regionDelays, e);
            }
            publishDelays.put(region, delay);
        }
    }

}
