package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;

import java.util.function.Supplier;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Validate the percentage of record change is within the {@link Threshold},
 * this only works for types that have a primary key definition
 */
public class RecordCountPercentChangeValidator implements ValidatorListener {
    private static final String NAME = RecordCountPercentChangeValidator.class.getName();
    private final Logger log = Logger.getLogger(RecordCountPercentChangeValidator.class.getName());

    private final String typeName;
    private final Threshold threshold;
    private AbstractHollowDataAccessor accessor;

    public RecordCountPercentChangeValidator(String typeName,
                                             Threshold threshold) {
        this.typeName = typeName;
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return NAME + "_" + typeName;
    }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState readState) {
        HollowReadStateEngine readStateEngine = requireNonNull(readState.getStateEngine(), "read state is null");
        HollowTypeReadState typeState = requireNonNull(readStateEngine.getTypeState(typeName),
                "type not loaded or does not exist in dataset; type=" + typeName);
        accessor =
            new AbstractHollowDataAccessor<Object>(readStateEngine, typeName) {
                @Override public Object getRecord(int ordinal) { return null; }
        };
        ValidationResult validationResult = validateChanges(typeState);
        log.info(validationResult.toString());
        return validationResult;
    }

    private ValidationResult validateChanges(HollowTypeReadState typeState) {
        if(typeState.getPreviousOrdinals().isEmpty()) {
            return ValidationResult.from(this).passed("Ignore the check if previous records are empty.");
        }
        int addRecordNumber = accessor.getAddedRecords().size();
        int removeRecordNumber = accessor.getRemovedRecords().size();
        int updatedRecordNumber = accessor.getUpdatedRecords().size();
        int previousRecordNumber = typeState.getPreviousOrdinals().cardinality();

        float addedPercent = (float) addRecordNumber / previousRecordNumber;
        float removedPercent = (float) removeRecordNumber / previousRecordNumber;
        float updatedPercent = (float) updatedRecordNumber / previousRecordNumber;


        float addedPercentageThreshold = threshold.addedPercentageThreshold.get();
        float removedPercentageThreshold = threshold.removedPercentageThreshold.get();
        float updatedPercentageThreshold = threshold.updatedPercentageThreshold.get();

        ValidationResult.ValidationResultBuilder builder = ValidationResult.from(this);
        builder.detail("addedRecordNumber", addRecordNumber);
        builder.detail("removedRecordNumber", removeRecordNumber);
        builder.detail("updatedRecordNumber", updatedRecordNumber);
        builder.detail("previousRecordNumber", previousRecordNumber);
        builder.detail("addedPercentageThreshold", addedPercentageThreshold);
        builder.detail("removedPercentageThreshold", removedPercentageThreshold);
        builder.detail("updatedPercentageThreshold", updatedPercentageThreshold);

        boolean pass =
                (addedPercentageThreshold < 0 || addedPercent < addedPercentageThreshold) &&
                        (removedPercentageThreshold < 0 || removedPercent < removedPercentageThreshold) &&
                        (updatedPercentageThreshold < 0 || updatedPercent < updatedPercentageThreshold);
        if (pass) {
            return builder.passed(String.format(
                    "%s added=%.2f%% (<%.2f%%), removed=%.2f%% (<%.2f%%), updated=%.2f%% (<%.2f%%)",
                    getName(), addedPercent * 100, addedPercentageThreshold * 100,  removedPercent * 100,
                    removedPercentageThreshold * 100, updatedPercent * 100, updatedPercentageThreshold * 100));
        }
        return builder.failed("record count change is more than threshold");
    }

    /**
     * Define the percentage of value change as supplier of float in this class,
     * for example 1% should be defined as 0.01.
     * Not all three threshold needs to be defined. removedPercentageThreshold and updatedPercentageThreshold
     * value range should be [0,1], addedPercentageThreshold should not be less than 0.
     */
    public static class Threshold {
        private final Supplier<Float> removedPercentageThreshold;
        private final Supplier<Float> addedPercentageThreshold;
        private final Supplier<Float> updatedPercentageThreshold;

        public Threshold(Supplier<Float> removedPercentageThreshold,
                         Supplier<Float> addedPercentageThreshold,
                         Supplier<Float> updatedPercentageThreshold) {
            this.removedPercentageThreshold = removedPercentageThreshold;
            this.addedPercentageThreshold = addedPercentageThreshold;
            this.updatedPercentageThreshold = updatedPercentageThreshold;
        }


        public static ThresholdBuilder builder() {
            return new ThresholdBuilder();
        }

        public static class ThresholdBuilder {
            private Supplier<Float> removedPercentageThreshold;
            private Supplier<Float> addedPercentageThreshold;
            private Supplier<Float> updatedPercentageThreshold;

            public ThresholdBuilder withRemovedPercentageThreshold(Supplier<Float> removedPercentageThreshold) {
                this.removedPercentageThreshold = removedPercentageThreshold;
                return this;
            }

            public ThresholdBuilder withAddedPercentageThreshold(Supplier<Float> addedPercentageThreshold) {
                this.addedPercentageThreshold = addedPercentageThreshold;
                return this;
            }

            public ThresholdBuilder withUpdatedPercentageThreshold(Supplier<Float> updatedPercentageThreshold) {
                this.updatedPercentageThreshold = updatedPercentageThreshold;
                return this;
            }

            public Threshold build() {
                if (removedPercentageThreshold != null && (removedPercentageThreshold.get() < 0 || removedPercentageThreshold.get() > 1)) {
                    throw new RuntimeException("removed percentage threshold must be between 0 and 1, value "
                            + removedPercentageThreshold.get() + " is invalid.");
                }
                if (updatedPercentageThreshold != null && (updatedPercentageThreshold.get() < 0 || updatedPercentageThreshold.get() > 1)) {
                    throw new RuntimeException("updated percentage threshold must be between 0 and 1, value "
                            + updatedPercentageThreshold.get() + " is invalid.");
                }
                if (addedPercentageThreshold != null && addedPercentageThreshold.get() < 0) {
                    throw new RuntimeException("added percentage threshold must be >= 0, value "
                            + addedPercentageThreshold.get() + " is invalid.");
                }
                if (removedPercentageThreshold == null) {
                    removedPercentageThreshold = () -> -1f;
                }
                if (updatedPercentageThreshold == null) {
                    updatedPercentageThreshold = () -> -1f;
                }
                if (addedPercentageThreshold == null) {
                    addedPercentageThreshold = () -> -1f;
                }
                return new Threshold(removedPercentageThreshold,
                        addedPercentageThreshold,
                        updatedPercentageThreshold);
            }
        }
    }
}
