/*
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The overall status of a sequence of validation results.
 * <p>
 * A status accumulated from the results of validators will be passed to registered
 * {@link ValidationStatusListener validation status} listeners after validation status has completed.
 */
public final class ValidationStatus {
    private final List<ValidationResult> results;
    private final boolean passed;

    /**
     * Creates a new validation status from a list of validation results.
     *
     * @param results the validation results
     * @throws NullPointerException if {@code results} is {@code null}
     */
    public ValidationStatus(List<ValidationResult> results) {
        this.results = Collections.unmodifiableList(new ArrayList<>(results));
        this.passed = this.results.stream().allMatch(ValidationResult::isPassed);
    }

    /**
     * Returns true if all validation results have passed, otherwise false if one or more results
     * failed or a validator was erroneous.
     *
     * @return true if all validation results have passed, otherwise false
     */
    public boolean passed() {
        return passed;
    }

    /**
     * Returns true if one or more validation results failed or was erroneous, otherwise false if all results
     * passed.
     *
     * @return true if one or more validation results failed or was erroneous, otherwise false
     */
    public boolean failed() {
        return !passed;
    }

    /**
     * Returns the validation results.
     *
     * @return the validation results. The results are unmodifiable.
     */
    public List<ValidationResult> getResults() {
        return results;
    }
}
