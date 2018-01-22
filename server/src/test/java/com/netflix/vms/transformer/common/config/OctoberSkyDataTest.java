package com.netflix.vms.transformer.common.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.netflix.cup.CupModule;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.runtime.health.guice.HealthModule;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.cup.CupLibraryImpl;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator;
import com.netflix.vms.transformer.octobersky.OctoberSkyDataImpl;
import com.netflix.vms.transformer.startup.JerseyModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

/**
 * Test for reading multilingual countries
 */
@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({
        OctoberSkyDataTest.TestOctoberSkyModule.class,
})
public class OctoberSkyDataTest {

    @Inject
    public OctoberSkyData octoberSkyData;

    @Test
    public void testGetMultiLingualCountries() {
        Set<String> languages = octoberSkyData.getCatalogLanguages("BE");
        Assert.assertNotNull(languages);
    }

    public static class TestOctoberSkyModule extends AbstractModule {

        @Override
        public void configure() {
            install(new RuntimeCoreModule());
            install(new HealthModule() {
                @Override
                protected void configureHealth() {
                    bindAdditionalHealthIndicator().to(TransformerServerHealthIndicator.class);
                }
            });
            install(new JerseyModule());
            install(new CupModule());

            bind(OctoberSkyData.class).to(OctoberSkyDataImpl.class);
            bind(CupLibrary.class).to(CupLibraryImpl.class);
        }
    }
}
