package com.netflix.hollow.api.error;

/**
 * An exception thrown when the write state is unable to advance, revert, or otherwise fails.
 */
public class HollowWriteStateException extends HollowException {
    public HollowWriteStateException(String message) {
        super(message);
    }

    public HollowWriteStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public HollowWriteStateException(Throwable cause) {
        super(cause);
    }
}
