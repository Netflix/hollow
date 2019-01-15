package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.VERSION_LATEST;
import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;

import java.util.OptionalLong;

public final class Versions {

    private Versions() {}

    public static OptionalLong maybeVersion(long version) {
        return version == VERSION_NONE ? OptionalLong.empty() : OptionalLong.of(version);
    }

    public static String prettyVersion(long version) {
        if (version == VERSION_NONE) {
            return "none";
        } else if (version == VERSION_LATEST) {
            return "latest";
        } else {
            return String.valueOf(version);
        }
    }
}
