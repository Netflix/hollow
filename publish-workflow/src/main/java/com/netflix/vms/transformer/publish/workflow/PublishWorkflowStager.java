package com.netflix.vms.transformer.publish.workflow;

import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.input.CycleInputs;
import com.netflix.vms.transformer.publish.status.CycleStatusFuture;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface PublishWorkflowStager {
    
    CycleStatusFuture triggerPublish(CycleInputs cycleInputs, long previousCycleId, long currentCycleId);
    
    void notifyRestoredStateEngine(HollowReadStateEngine stateEngine, HollowReadStateEngine nostreamsRestoredState);
    
    HollowReadStateEngine getCurrentReadStateEngine();

    PublishWorkflowContext getContext();

    default void initProducer(
            Supplier<CycleInputs> cycleInputs,
            CinderProducerBuilder pb,
            String vip,
            LongSupplier previousVersion,
            LongSupplier noStreamsPreviousVersion, LongSupplier noStreamsVersion) {
    }

    default void initNoStreamsProducer(
            Supplier<CycleInputs> cycleInputs,
            CinderProducerBuilder pb,
            String vip,
            LongSupplier previousVersion) {
    }
}
