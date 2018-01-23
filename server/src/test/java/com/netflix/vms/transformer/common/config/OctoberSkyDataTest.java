package com.netflix.vms.transformer.common.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.netflix.cup.CupModule;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.runtime.health.guice.HealthModule;
import com.netflix.runtime.lifecycle.RuntimeCoreModule;
import com.netflix.vms.transformer.common.cup.CupLibrary;
import com.netflix.vms.transformer.cup.CupLibraryImpl;
import com.netflix.vms.transformer.health.TransformerServerHealthIndicator;
import com.netflix.vms.transformer.octobersky.OctoberSkyDataImpl;
import com.netflix.vms.transformer.startup.JerseyModule;
import com.netflix.vms.transformer.startup.TransformerModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

/**
 * Test for reading multilingual countries
 */
public class OctoberSkyDataTest {

    @Test
    public void testGetMultiLingualCountries() {
        Injector injector = InjectorBuilder.fromModules(new TransformerModule()).createInjector();
        OctoberSkyData octoberSkyData = injector.getInstance(OctoberSkyDataImpl.class);

        Set<String> languages = octoberSkyData.getCatalogLanguages("BE");
        Assert.assertNotNull(languages);
    }
}
