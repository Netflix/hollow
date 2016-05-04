package com.netflix.vms.transformer.publish.workflow;

import java.util.Collections;
import java.util.Set;

public class PublishWorkflowConfig {

    public boolean areCircuitBreakersEnabled() {
        return false;
    }

    public boolean isCircuitBreakerEnabled(String ruleName) {
        return true;
    }

    public boolean isCircuitBreakerEnabled(String ruleName, String country) {
        return true;
    }

    public double getCircuitBreakerThreshold(String ruleName) {
        return 0.05d;
    }

    public double getCircuitBreakerThreshold(String ruleName, String country) {
        return 0.05d;
    }

    public boolean isPlaybackMonkeyEnabled() {
        return false;
    }

    public Set<String> getSupportedCountrySet() {
        return Collections.emptySet();
    }

    public Set<String> getPlaybackMonkeyTestForCountries() {
        return Collections.emptySet();
    }

    public int getPlaybackMonkeyMaxTestVideosSize() {
        return 0;
    }

    public boolean shouldFailCycleOnPlaybackMonkeyFailure() {
        return false;
    }

    public int getPlaybackMonkeyMaxRetriesPerTest() {
        return 0;
    }

    public float getPlaybackMonkeyNoiseTolerance() {
        return 0;
    }


}
