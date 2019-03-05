package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OctoberSkyData;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test to run the circuit breaker.
 */
public class CatalogSizeCircuitBreakerTest {


    @Test
    @Ignore
    public void testCatalogSizeCircuitBreaker() throws Exception {
        File localFile = new File("/Users/ksatiya/Downloads/20180424171608397");
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        InputStream is = new LZ4BlockInputStream(new FileInputStream(localFile));
        reader.readSnapshot(is);

        TransformerContext ctx = Mockito.mock(TransformerContext.class);
        OctoberSkyData octoberSkyData = Mockito.mock(OctoberSkyData.class);
        TaggingLogger logger = Mockito.mock(TaggingLogger.class);

        Mockito.when(ctx.getOctoberSkyData()).thenReturn(octoberSkyData);
        Mockito.when(ctx.getLogger()).thenReturn(logger);
        Mockito.when(ctx.getNowMillis()).thenReturn(System.currentTimeMillis());

        Mockito.when(octoberSkyData.getMultiLanguageCatalogCountries()).thenReturn(Collections.singleton("BE"));
        Set<String> languages = new HashSet<>();
        languages.add("en");
        languages.add("fr");
        languages.add("nl");
        Mockito.when(octoberSkyData.getCatalogLanguages(Mockito.eq("BE"))).thenReturn(languages);


        CatalogSizeCircuitBreaker catalogSizeCircuitBreaker = new CatalogSizeCircuitBreaker(ctx, "vip", 1L, "catalogSizeTest");
        catalogSizeCircuitBreaker.runCircuitBreaker(readEngine);
    }
}
