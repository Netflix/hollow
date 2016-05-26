package com.netflix.vms.transformer.publish.workflow.circuitbreaker;


import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.CircuitBreaker;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.type.ISOCountry;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.HashSet;
import java.util.Set;

public abstract class HollowCountrySpecificCircuitBreaker extends HollowCircuitBreaker {

    private Set<String> comparedCountries;

    public HollowCountrySpecificCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
        super(ctx, versionId);
        this.comparedCountries = new HashSet<String>();
    }

    @Override
    public CircuitBreakerResults run(HollowReadStateEngine stateEngine) {
        if(!ctx.getConfig().isCircuitBreakerEnabled(getRuleName())) {
            ctx.getLogger().warn(CircuitBreaker, "Circuit breaker rule: " + getRuleName() + " is disabled!");
            return PASSED;
        }

        CircuitBreakerResults results = runCircuitBreaker(stateEngine);

        for(String country : ctx.getOctoberSkyData().getSupportedCountries()) {
        	if(!isEnabled(country))
        		continue;
        		
            if(!comparedCountries.contains(country)
                    && ctx.getConfig().isCircuitBreakerEnabled(getRuleName(), country)
                    && countryWasPreviouslyCompared(country)) {
                results.addResult(false, "Rule " + getRuleName() + ": country " + country + " was previously compared, but no value was found this cycle.");
            }
        }

        return results;
    }

    protected CircuitBreakerResults compareMetric(ISOCountry country, long value) {
        return compareMetric(country.getId(), value);
    }

    protected CircuitBreakerResults compareMetric(String country, long value) {
    	if(!isEnabled(country))
    		return new CircuitBreakerResults(true, "Rule " + getRuleName() + ": disabled for country " + country);
    	
        comparedCountries.add(country);
        return compareMetric(metricName(country), value, getThreshold(country));
    }

    private boolean countryWasPreviouslyCompared(String country) {
        return metricExists(metricName(country));
    }

    private String metricName(String country) {
        return getRuleName() + "_" + country;
    }
    
    private double getThreshold(String country) {
    	Double threshold = ctx.getConfig().getCircuitBreakerThreshold(getRuleName(), country);
    	if(threshold != null)
    		return threshold;
    	return ctx.getConfig().getCircuitBreakerThreshold(getRuleName());
    }
    
    private boolean isEnabled(String country) {
    	Boolean enabled = ctx.getConfig().isCircuitBreakerEnabled(getRuleName(), country);
    	if(enabled != null)
    		return enabled;
    	return ctx.getConfig().isCircuitBreakerEnabled(getRuleName());
    }

}
