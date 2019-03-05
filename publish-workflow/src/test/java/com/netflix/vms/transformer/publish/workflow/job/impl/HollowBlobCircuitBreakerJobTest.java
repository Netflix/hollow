package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.circuitbreaker.HollowCircuitBreaker;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class HollowBlobCircuitBreakerJobTest {

	/*
	 * The following constraint is necessary for the circuit breaker UI.
	 * This test warns us if we add a circuit breaker which violates this assumption.
	 */
	@Test
	public void canRetrieveCircuitBreakerRulesWithNullParameters() {
		TransformerContext ctx = Mockito.mock(TransformerContext.class);
		PublishWorkflowContext pctx = Mockito.mock(PublishWorkflowContext.class);

		Mockito.when(pctx.getTransformerContext()).thenReturn(ctx);
		Mockito.when(pctx.getVip()).thenReturn("vip");

		HollowCircuitBreaker[] circuitBreakerRules = HollowBlobCircuitBreakerJob.createCircuitBreakerRules(pctx, -1L, -1L);
		
		Assert.assertTrue(circuitBreakerRules.length > 0);
	}

}
