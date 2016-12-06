package com.netflix.vms.transformer.modules.meta;

public interface EDAvailabilityChecker {

    boolean isAvailableForED(int videoId, String countryCode);
}
