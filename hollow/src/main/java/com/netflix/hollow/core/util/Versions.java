package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.VERSION_LATEST;
import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;

public final class Versions {

    // visible for testing
    static final String PRETTY_VERSION_NONE = "none";
    static final String PRETTY_VERSION_LATEST = "latest";

    private Versions() {}

    public static String prettyVersion(long version) {
        if (version == VERSION_NONE) {
            return PRETTY_VERSION_NONE;
        } else if (version == VERSION_LATEST) {
            return PRETTY_VERSION_LATEST;
        } else {
            return String.valueOf(version);
        }
    }
}
