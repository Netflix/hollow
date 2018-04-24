package com.netflix.vms.transformer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * Unit tests for cycle data aggregator.
 */
public class CycleDataAggregatorTest {

    @Mock
    private TransformerContext context;
    @Spy
    private TestLogger taggingLogger;
    private CycleDataAggregator cycleDataAggregator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.getLogger()).thenReturn(taggingLogger);
        cycleDataAggregator = new CycleDataAggregator(context);
    }

    @Test
    public void testCollect() {
        cycleDataAggregator.aggregateForLogTag(TransformerLogTag.Language_catalog_NoWindows, TaggingLogger.Severity.ERROR, "some message");
        cycleDataAggregator.collect("US", "en", 123, TransformerLogTag.Language_catalog_NoWindows);
        String expectedMessage = cycleDataAggregator.getJSON("US", "en", "some message", Arrays.asList(123));
        cycleDataAggregator.logAllAggregatedData();
        Mockito.verify(taggingLogger, atLeastOnce()).log(
                eq(TaggingLogger.Severity.ERROR),
                eq(Arrays.asList(TransformerLogTag.Language_catalog_NoWindows)),
                eq(expectedMessage)
        );
    }

    @Test
    public void testGetAsJSON() {
        String expectedString = "{\"country\":\"US\",\"language\":\"en\",\"count\":1,\"message\":\"No message\",\"videoIds\":[123]}";
        String actual = cycleDataAggregator.getJSON("US", "en", "No message", Arrays.asList(123));
        Assert.assertEquals(expectedString, actual);
    }

    private static class TestLogger implements TaggingLogger {

        @Override
        public void log(Severity severity, Collection<LogTag> tags, String message, Object... args) {

        }
    }
}
