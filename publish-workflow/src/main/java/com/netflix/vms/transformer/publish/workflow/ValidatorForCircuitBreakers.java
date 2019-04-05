package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.TransformCycleFailed;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.validation.ValidationResult;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.servo.monitor.DynamicCounter;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.CatalogSizeCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.CertificationSystemCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.DuplicateDetectionCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.HollowCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.SnapshotSizeCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.TopNViewShareAvailabilityCircuitBreaker;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.TypeCardinalityCircuitBreaker;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.commons.lang.StringUtils;

public class ValidatorForCircuitBreakers implements
        CycleListener,
        PublishListener,
        ValidationStatusListener,
        ValidatorListener {

    private final TransformerContext ctx;
    private final String vip;

    private boolean circuitBreakersDisabled;

    private HollowCircuitBreaker[] circuitBreakerRules;


    public ValidatorForCircuitBreakers(TransformerContext ctx, String vip) {
        this.ctx = ctx;
        this.vip = vip;
    }


    // CycleListener

    @Override public void onCycleSkip(CycleSkipReason reason) {
    }

    @Override public void onNewDeltaChain(long version) {
    }

    @Override
    public void onCycleStart(long version) {
        this.circuitBreakersDisabled = !ctx.getConfig().isCircuitBreakersEnabled();
    }

    @Override
    public void onCycleComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
    }


    // PublishListener

    @Override public void onNoDeltaAvailable(long version) {
    }

    @Override public void onPublishStart(long version) {
    }

    long snapshotSize;

    @Override
    public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
        if (status.getType() == Status.StatusType.SUCCESS &&
                blob.getType() == HollowProducer.Blob.Type.SNAPSHOT) {
            Path path = blob.getPath();
            if (path != null) {
                try {
                    snapshotSize = Files.size(path);
                } catch (IOException e) {
                    ctx.getLogger().error(CircuitBreaker, "Error obtaining snapshot blob size", e);
                }
            } else {
                ctx.getLogger().warn(CircuitBreaker, "Snapshot blob size cannot be obtained: path is null");
            }
        }
    }

    @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
        // There is an issue obtaining the snapshot size since it is published asynchronously
        // Note this is a problem in general with the listeners, it breaks the assumption of single threaded
        // operation
    }

    @Override public void onPublishComplete(Status status, long version, Duration elapsed) {

    }


    // ValidationStatusListener

    boolean failed;

    @Override
    public void onValidationStatusStart(long version) {
        failed = false;

        // @@@ Need snapshot size, if present
        if (circuitBreakersDisabled) {
            return;
        }

        circuitBreakerRules = createCircuitBreakerRules(ctx, vip, version, snapshotSize);
    }

    @Override
    public void onValidationStatusComplete(
            ValidationStatus status, long version, Duration elapsed) {
        circuitBreakerRules = null;

        if (status.failed()) {
            incrementAlertCounter();
        }

        logResult(status.passed(), version);
    }


    // ValidatorListener

    @Override
    public String getName() {
        return "CircuitBreakers";
    }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState readState) {
        if (circuitBreakersDisabled) {
            ctx.getLogger().warn(CircuitBreaker, "Hollow/Master Circuit Breaker is disabled!");
            return ValidationResult.from(this).passed("DISABLED");
        }

        SimultaneousExecutor executor = new SimultaneousExecutor();

        Map<HollowCircuitBreaker, Future<HollowCircuitBreaker.CircuitBreakerResults>> resultFutures = new HashMap<>();

        for (HollowCircuitBreaker rule : circuitBreakerRules) {
            Future<HollowCircuitBreaker.CircuitBreakerResults> job = executor.submit(
                    () -> rule.run(readState.getStateEngine()));
            resultFutures.put(rule, job);
        }

        try {
            executor.awaitSuccessfulCompletion();

            boolean isAllDataValid = true;

            ValidationResult.ValidationResultBuilder vresult = ValidationResult.from(this);
            for (Map.Entry<HollowCircuitBreaker, Future<HollowCircuitBreaker.CircuitBreakerResults>> e : resultFutures
                    .entrySet()) {
                HollowCircuitBreaker.CircuitBreakerResults results = e.getValue().get();

                for (HollowCircuitBreaker.CircuitBreakerResult result : results) {
                    if (!StringUtils.isEmpty(result.getMessage())) {
                        if (result.isPassed()) {
                            ctx.getLogger().info(CircuitBreaker, result.getMessage());
                        } else {
                            ctx.getLogger().error(CircuitBreaker, result.getMessage());
                        }

                        vresult.detail(e.getKey().getRuleName(), result.isPassed());
                    }

                    isAllDataValid = isAllDataValid && result.isPassed();
                }
            }

            if (isAllDataValid) {
                for (HollowCircuitBreaker rule : circuitBreakerRules) {
                    rule.saveSuccessSizesForCycle(readState.getVersion());
                }
                return vresult.passed();
            } else {
                failed = true;
                return vresult.failed("One or more circuitBreakers failed");
            }
        } catch (RuntimeException e) {
            failed = true;
            throw e;
        } catch (Exception e) {
            failed = true;
            throw new RuntimeException(e);
        }
    }

    static HollowCircuitBreaker[] createCircuitBreakerRules(
            TransformerContext ctx, String vip, long cycleVersion, long snapshotFileLength) {
        List<HollowCircuitBreaker> cbs = new ArrayList<>(Arrays.asList(
                new DuplicateDetectionCircuitBreaker(ctx, vip, cycleVersion),
                new CertificationSystemCircuitBreaker(ctx, vip, cycleVersion),
                new CertificationSystemCircuitBreaker(ctx, vip, cycleVersion, 100),
                new CertificationSystemCircuitBreaker(ctx, vip, cycleVersion, 75),
                new CertificationSystemCircuitBreaker(ctx, vip, cycleVersion, 50),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "NamedCollectionHolder"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "CompleteVideo"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "PackageData"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "StreamData"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "Artwork"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "OriginServer"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "DrmKey"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "WmDrmKey"),
                new TypeCardinalityCircuitBreaker(ctx, vip, cycleVersion, "GlobalPerson"),
                new TopNViewShareAvailabilityCircuitBreaker(ctx, vip, cycleVersion),
                new CatalogSizeCircuitBreaker(ctx, vip, cycleVersion, "CatalogSize")
        ));
        if (snapshotFileLength > 0) {
            cbs.add(new SnapshotSizeCircuitBreaker(ctx, vip, cycleVersion, snapshotFileLength));
        }

        return cbs.toArray(new HollowCircuitBreaker[0]);
    }

    private void logResult(boolean isAllDataValid, long cycleVersion) {
        String logMessage = "Hollow data validation completed for version " + cycleVersion + " vip: " + vip;

        if (!isAllDataValid) {
            ctx.getLogger().error(Arrays.asList(TransformCycleFailed, CircuitBreaker), "Circuit Breakers Failed: {}",
                    logMessage);
        } else {
            ctx.getLogger().info(CircuitBreaker, "Circuit Breakers Successful: {}", logMessage);
        }
    }

    private void incrementAlertCounter() {
        DynamicCounter.increment("vms.hollow.validation.failed");
    }
}
