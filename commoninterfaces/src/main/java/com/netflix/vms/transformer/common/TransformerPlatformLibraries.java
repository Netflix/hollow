package com.netflix.vms.transformer.common;

import com.netflix.aws.file.FileStore;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.hermes.publisher.FastPropertyPublisher;
import com.netflix.hermes.subscriber.SubscriptionManager;

public interface TransformerPlatformLibraries {
    FileStore getFileStore();

    NFAstyanaxManager getAstyanax();

    SubscriptionManager getHermesSubscriber();

    FastPropertyPublisher getHermesPublisher();
}
