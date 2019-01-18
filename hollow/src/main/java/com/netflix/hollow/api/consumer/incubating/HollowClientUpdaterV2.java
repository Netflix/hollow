package com.netflix.hollow.api.consumer.incubating;

import static java.util.stream.Collectors.toList;

import com.netflix.hollow.api.client.HollowClientUpdater;
import com.netflix.hollow.api.custom.HollowAPI;
import java.util.stream.Collectors;

public class HollowClientUpdaterV2<A extends HollowAPI> extends HollowClientUpdater {
    HollowClientUpdaterV2(HollowConsumerV2.Builder<A> builder) {
        super(
                builder.blobRetriever.value,
                builder.refreshListenerList,
                builder.apiFactory,
                builder.doubleSnapshotConfig.value,
                builder.hashCodeFinder,
                builder.objectLongevityConfig.value,
                builder.objectLongevityDetector.value,
                builder.metrics,
                builder.metricsCollector.value
        );
        setFilter(builder.filterConfig.value);
    }

    @Override
    public A getAPI() {
        return (A)super.getAPI();
    }
}
