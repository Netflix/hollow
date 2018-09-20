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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The result of validation performed by a {@link ValidatorListener validator}.
 */
public final class ValidationResult {
    private final ValidationResultType type;
    private final String name;
    private final Throwable ex; // set for status == ERROR or FAILED
    private final String message;
    private final Map<String, String> details;

    ValidationResult(
            ValidationResultType type,
            String name,
            Throwable ex,
            String message,
            Map<String, String> details) {
        if (type == ValidationResultType.ERROR && ex == null) {
            throw new IllegalArgumentException();
        }
        // @@@ For the moment allow a throwable to be associated with FAILED state
        // This is for compatibility with HollowProducer.Validator.ValidationException
        if (type == ValidationResultType.PASSED && ex != null) {
            throw new IllegalArgumentException();
        }

        assert name != null; // builder checks
        this.name = name;
        this.type = type;
        assert type != null; // builder ensures
        this.ex = ex;
        this.message = message;
        this.details = Collections.unmodifiableMap(details);
    }

    /**
     * Returns the validation result type.
     *
     * @return the validation result type
     */
    public ValidationResultType getResultType() {
        return type;
    }

    /**
     * Returns the name of the validation performed.
     *
     * @return the name of the validation performed
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@code Throwable} associated with a validator that
     * failed with an unexpected {@link ValidationResultType#ERROR error}.
     *
     * @return the {@code Throwable} associated with an erroneous validator, otherwise
     * {@code null}
     */
    public Throwable getThrowable() {
        return ex;
    }

    /**
     * Returns a message associated with the validation.
     *
     * @return a message associated with the validation. may be {@code null}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns details associated with the validation.
     *
     * @return details associated with the validation. The details are unmodifiable and may be empty.
     */
    public Map<String, String> getDetails() {
        return details;
    }

    /**
     * Returns true if validation passed, otherwise {@code false}.
     *
     * @return true if validation passed, otherwise {@code false}
     */
    public boolean isPassed() {
        return type == ValidationResultType.PASSED;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ValidationResult[")
                .append("name=\"").append(name).append("\" ")
                .append("result=").append(type).append(" ");

        if (message == null) {
            sb.append("message=null ");
        } else {
            sb.append("message=\"").append(message).append("\" ");
        }

        sb.append("details=").append(details).append(" ")
                .append("throwable=").append(ex).append(" ")
                .append("]");

        return sb.toString();
    }

    /**
     * Initiates the building of a result from a validation listener.
     * The {@link ValidationResult#getName name} of the validation result with be
     * set to the {@link ValidatorListener#getName name} of the validator
     *
     * @param v the validation listener
     * @return the validation builder
     * @throws NullPointerException if {@code v} is {@code null}
     */
    public static ValidationResultBuilder from(ValidatorListener v) {
        return from(v.getName());
    }

    /**
     * Initiates the building of a result from a name.
     *
     * @param name the validation result
     * @return the validation builder
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public static ValidationResultBuilder from(String name) {
        return new ValidationResultBuilder(name);
    }

    /**
     * A builder of a {@link ValidationResult}.
     * <p>
     * The builder may be reused after it has built a validation result, but the details will be reset
     * to contain no entries.
     */
    static public class ValidationResultBuilder {
        private final String name;
        private Map<String, String> details;

        ValidationResultBuilder(String name) {
            this.name = Objects.requireNonNull(name);
            this.details = new HashMap<>();
        }

        /**
         * Sets a detail.
         *
         * @param name the detail name
         * @param value the detail value, which will be converted to a {@code String}
         * using {@link String#valueOf(Object)}
         * @return the validation builder
         * @throws NullPointerException if {@code name} or {@code value} are {@code null}
         */
        public ValidationResultBuilder detail(String name, Object value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            details.put(name, String.valueOf(value));
            return this;
        }

        /**
         * Builds a result that has {@link ValidationResultType#PASSED passed} with no message.
         *
         * @return a validation result that has passed.
         */
        public ValidationResult passed() {
            return new ValidationResult(
                    ValidationResultType.PASSED,
                    name,
                    null,
                    null,
                    details
            );
        }

        /**
         * Builds a result that has {@link ValidationResultType#PASSED passed} with a message.
         *
         * @param message the message, may be {@code null}
         * @return a validation result that has passed.
         */
        public ValidationResult passed(String message) {
            return build(
                    ValidationResultType.PASSED,
                    name,
                    null,
                    message,
                    details
            );
        }

        /**
         * Builds a result that has {@link ValidationResultType#FAILED failed} with a message.
         *
         * @param message the message
         * @return a validation result that has failed.
         * @throws NullPointerException if {@code message} is {@code null}
         */
        public ValidationResult failed(String message) {
            return build(
                    ValidationResultType.FAILED,
                    name,
                    null,
                    message,
                    details
            );
        }

        /**
         * Builds a result for a validator that produced an unexpected {@link ValidationResultType#ERROR error}
         * with a {@code Throwable}.
         *
         * @param t the {@code Throwable}
         * @return a validation result for a validator that produced an unexpected error
         * @throws NullPointerException if {@code t} is {@code null}
         */
        // @@@ This could be made package private as it is questionable if validators should use this,
        // however for testing purposes of status listeners it's useful.
        public ValidationResult error(Throwable t) {
            return build(
                    ValidationResultType.ERROR,
                    name,
                    t,
                    t.getMessage(),
                    details
            );
        }

        private ValidationResult build(
                ValidationResultType type,
                String name,
                Throwable ex,
                String message,
                Map<String, String> details) {
            reset();
            return new ValidationResult(type, name, ex, message, details);
        }

        private void reset() {
            this.details = new HashMap<>();
        }
    }
}
