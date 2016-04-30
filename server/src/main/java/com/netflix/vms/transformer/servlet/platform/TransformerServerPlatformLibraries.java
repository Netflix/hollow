package com.netflix.vms.transformer.servlet.platform;

import com.netflix.hermes.publisher.FastPropertyPublisher;

import com.netflix.hermes.subscriber.SubscriptionManager;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.aws.file.FileStore;
import com.netflix.cassandra.NFAstyanaxManager;

public class TransformerServerPlatformLibraries implements TransformerPlatformLibraries {
    private final FileStore fileStore;
    private final NFAstyanaxManager astyanax;
    private final SubscriptionManager hermesSubscriber;
    private final FastPropertyPublisher hermesPublisher;

    public TransformerServerPlatformLibraries(FileStore fileStore,
     NFAstyanaxManager astyanax,
     SubscriptionManager hermesSubscriber,
     FastPropertyPublisher hermesPublisher) {
        this.fileStore = fileStore;
        this.astyanax = astyanax;
        this.hermesSubscriber = hermesSubscriber;
        this.hermesPublisher = hermesPublisher;
    }

    @Override
    public FileStore getFileStore() {
        return fileStore;
    }

    @Override
    public NFAstyanaxManager getAstyanax() {
        return astyanax;
    }

    @Override
    public SubscriptionManager getHermesSubscriber() {
        return hermesSubscriber;
    }

    @Override
    public FastPropertyPublisher getHermesPublisher() {
        return hermesPublisher;
    }
}
