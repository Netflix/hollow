package com.netflix.vms.transformer.publish;

public interface PoisonedStateMarker {

    public boolean isStatePoisoned(long version);

    public static PoisonedStateMarker DEFAULT_MARKER = new PoisonedStateMarker() {
        @Override
        public boolean isStatePoisoned(long version) {
            return false;
        }
    };
}
