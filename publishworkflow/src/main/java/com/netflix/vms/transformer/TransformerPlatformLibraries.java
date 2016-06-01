package com.netflix.vms.transformer;

import com.netflix.aws.file.FileStore;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.hermes.publisher.FastPropertyPublisher;
import com.netflix.hermes.subscriber.SubscriptionManager;

// TODO: these are great candidates for standard Guice injection; they already
// have providers configured in the server
public interface TransformerPlatformLibraries {
    FileStore getFileStore();

    NFAstyanaxManager getAstyanax();

    SubscriptionManager getHermesSubscriber();

    FastPropertyPublisher getHermesPublisher();
}
