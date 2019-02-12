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

import java.util.Objects;

/**
 * A validation status exception holding a validation status.
 */
public final class ValidationStatusException extends RuntimeException {
    private final ValidationStatus status;

    /**
     * Creates a validation status exception.
     *
     * @param status the status
     * @param message the message
     * @throws IllegalArgumentException if {@code status} contains results that all passed
     * @throws NullPointerException if {@code status} is {@code null}
     */
    public ValidationStatusException(ValidationStatus status, String message) {
        super(message);

        if (status.passed()) {
            throw new IllegalArgumentException("A validation status exception was created "
                    + "with a status containing results that all passed");
        }

        this.status = Objects.requireNonNull(status);
    }

    /**
     * Returns the validation status.
     *
     * @return the validation status.
     */
    public ValidationStatus getValidationStatus() {
        return status;
    }
}
