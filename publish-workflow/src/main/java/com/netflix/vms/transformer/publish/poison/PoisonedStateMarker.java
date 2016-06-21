package com.netflix.vms.transformer.publish.poison;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public interface PoisonedStateMarker {

    boolean isStatePoisoned(long version);

    /** mark the <code>version</code> as poisoned or clear the poison marker. Optional method. */
    default void markStatePoisoned(long version, boolean isPoisoned) throws ConnectionException {
        throw new UnsupportedOperationException();
    }

    public static PoisonedStateMarker DEFAULT_MARKER = new PoisonedStateMarker() {
        @Override
        public boolean isStatePoisoned(long version) {
            return false;
        }
    };
}
