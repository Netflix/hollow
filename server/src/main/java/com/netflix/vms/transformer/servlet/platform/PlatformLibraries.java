package com.netflix.vms.transformer.servlet.platform;

import com.netflix.hermes.publisher.FastPropertyPublisher;

import com.netflix.hermes.subscriber.SubscriptionManager;
import com.netflix.aws.file.FileStore;
import com.netflix.cassandra.NFAstyanaxManager;

public class PlatformLibraries {

    public static FileStore FILE_STORE;
    public static NFAstyanaxManager ASTYANAX;
    public static SubscriptionManager HERMES_SUBSCRIBER;
    public static FastPropertyPublisher HERMES_PUBLISHER;

}
