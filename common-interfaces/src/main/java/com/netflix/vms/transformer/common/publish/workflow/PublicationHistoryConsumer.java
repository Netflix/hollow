package com.netflix.vms.transformer.common.publish.workflow;

import java.util.function.Consumer;

@FunctionalInterface
public interface PublicationHistoryConsumer extends Consumer<PublicationHistory> {}
