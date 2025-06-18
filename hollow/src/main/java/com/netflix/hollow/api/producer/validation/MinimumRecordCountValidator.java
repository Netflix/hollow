package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import java.util.function.Supplier;

public class MinimumRecordCountValidator implements ValidatorListener {

    private static final String INVALID_THRESHOLD =
            "Minimum record count validation for type %s has failed due to invalid threshold: %s";
    private static final String FAILED_MIN_RECORD_COUNT_VALIDATION =
            "Minimum record count validation for type %s has failed since the record count %s is less than the "
                    + "configured threshold of %s";

    private static final String DATA_TYPE_NAME = "Typename";
    private static final String ALLOWABLE_MIN_RECORD_COUNT_NAME = "AllowableMinRecordCount";
    private static final String RECORD_COUNT_NAME = "RecordCount";

    private final String typeName;
    private final Supplier<Integer> minRecordCountSupplier;
    private final Supplier<Boolean> isEnabledSupplier;

    public MinimumRecordCountValidator(String typeName, Supplier<Integer> minRecordCountSupplier) {
        this(typeName, () -> true, minRecordCountSupplier);
    }

    public MinimumRecordCountValidator(String typeName, Supplier<Boolean> isEnabledSupplier, Supplier<Integer> minRecordCountSupplier) {
        this.typeName = typeName;
        this.isEnabledSupplier = isEnabledSupplier;
        this.minRecordCountSupplier = minRecordCountSupplier;
    }

    @Override
    public String getName() {
        return MinimumRecordCountValidator.class.getName() + "_" + typeName;
    }

    @Override
    public Supplier<Boolean> isEnabled() {
        return isEnabledSupplier;
    }

    @Override
    public ValidationResult onValidate(HollowProducer.ReadState readState) {
        ValidationResult.ValidationResultBuilder vrb = ValidationResult.from(this);

        Integer minRecordCount = minRecordCountSupplier.get();
        // 1<<29 is the max no. of records supported in a Hollow type
        if (minRecordCount == null || minRecordCount < 0 || minRecordCount > (1<<29)) {
            String message = String.format(INVALID_THRESHOLD, typeName, minRecordCount);
            return vrb.failed(message);
        }

        vrb.detail(DATA_TYPE_NAME, typeName)
                .detail(ALLOWABLE_MIN_RECORD_COUNT_NAME, minRecordCount);

        HollowTypeReadState typeState = readState.getStateEngine().getTypeState(typeName);
        int recordCount = typeState.getPopulatedOrdinals().cardinality();

        vrb.detail(RECORD_COUNT_NAME, recordCount);

        if (recordCount < minRecordCount) {
            String message = String.format(FAILED_MIN_RECORD_COUNT_VALIDATION, typeName, recordCount, minRecordCount);
            return vrb.failed(message);
        }

        return vrb.passed();
    }

}
