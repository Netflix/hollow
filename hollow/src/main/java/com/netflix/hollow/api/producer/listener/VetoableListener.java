package com.netflix.hollow.api.producer.listener;

/**
 * A marker interface to be implemented by a listener which indicates that any runtime exception
 * thrown by any of the listener's methods will be rethrown rather than, by default, caught and not
 * thrown.
 * <p>
 * This interface may be used by a listener to veto a cycle and cause the producer to fail.
 */
public interface VetoableListener {

    /**
     * A listener method may throw this exception which will be rethrown rather than caught and not
     * thrown.  This exception may be utilized when the listener does not implement {@link VetoableListener}
     * and requires finer-grain control over which of the listener's methods may veto a cycle and cause the
     * producer to fail.
     */
    class ListenerVetoException extends RuntimeException {

        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public ListenerVetoException() {
            super();
        }

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         * later retrieval by the {@link #getMessage()} method.
         */
        public ListenerVetoException(String message) {
            super(message);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.
         * <p>
         * Note that the detail message associated with {@code cause} is
         * <i>not</i> automatically incorporated in this runtime exception's detail
         * message.
         *
         * @param message the detail message (which is saved for later retrieval
         * by the {@link #getMessage()} method).
         * @param cause the cause (which is saved for later retrieval by the
         * {@link #getCause()} method).  (A {@code null} value is
         * permitted, and indicates that the cause is nonexistent or
         * unknown.)
         */
        public ListenerVetoException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of {@code (cause==null ? null : cause.toString())}
         * (which typically contains the class and detail message of
         * {@code cause<}).  This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the
         * {@link #getCause()} method).  (A {@code null} value is
         * permitted, and indicates that the cause is nonexistent or
         * unknown.)
         */
        public ListenerVetoException(Throwable cause) {
            super(cause);
        }
    }
}
