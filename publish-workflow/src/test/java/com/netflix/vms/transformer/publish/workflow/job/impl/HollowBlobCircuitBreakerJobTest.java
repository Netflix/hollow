package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.publish.workflow.circuitbreaker.HollowCircuitBreaker;
import org.junit.Assert;
import org.junit.Test;

public class HollowBlobCircuitBreakerJobTest {

	/*
	 * The following constraint is necessary for the circuit breaker UI.
	 * This test warns us if we add a circuit breaker which violates this assumption.
	 */
	@Test
	public void canRetrieveCircuitBreakerRulesWithNullParameters() {
		HollowCircuitBreaker[] circuitBreakerRules = HollowBlobCircuitBreakerJob.createCircuitBreakerRules(null, -1L, -1L);
		
		Assert.assertTrue(circuitBreakerRules.length > 0);
	}

}
