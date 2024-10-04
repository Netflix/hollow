package com.netflix.hollow.api.error;

public class VersionMismatchException extends HollowException {
    private final long expectedVersion;

    private final long actualVersion;

    public VersionMismatchException(long expectedVersion, long actualVersion) {
        super("toVersion in blob did not match toVersion requested in transition; actualToVersion=" + actualVersion + ", expectedToVersion=" + expectedVersion);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public long getExpectedVersion() {
        return expectedVersion;
    }

    public long getActualVersion() {
        return actualVersion;
    }
}
