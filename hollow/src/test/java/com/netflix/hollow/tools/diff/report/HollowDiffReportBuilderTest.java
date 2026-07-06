package com.netflix.hollow.tools.diff.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.diff.HollowDiff;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;

public class HollowDiffReportBuilderTest {

    @Test
    public void build_includesMetadataAndBlobHeaders() {
        HollowDiff diff = Mockito.mock(HollowDiff.class);
        Mockito.when(diff.getTypeDiffs()).thenReturn(Collections.emptyList());

        Map<String, String> fromTags = Collections.singletonMap("producer", "a");
        HollowReadStateEngine from = Mockito.mock(HollowReadStateEngine.class);
        Mockito.when(from.getHeaderTags()).thenReturn(fromTags);
        HollowReadStateEngine to = Mockito.mock(HollowReadStateEngine.class);
        Mockito.when(to.getHeaderTags()).thenReturn(Collections.singletonMap("producer", "b"));
        Mockito.when(diff.getFromStateEngine()).thenReturn(from);
        Mockito.when(diff.getToStateEngine()).thenReturn(to);

        HollowDiffReport report = HollowDiffReportBuilder.build(
                HollowDiffReportMetadata.of("nsA", 1L, "nsB", 2L),
                diff,
                HollowDiffReportOptions.defaults());

        assertEquals("nsA", report.getFromNamespace());
        assertEquals("nsB", report.getToNamespace());
        assertEquals(Long.valueOf(1L), report.getFromVersion());
        assertEquals(Long.valueOf(2L), report.getToVersion());
        assertEquals(1, report.getBlobHeaders().size());
        assertEquals("producer", report.getBlobHeaders().get(0).getHeaderName());
        assertEquals("a", report.getBlobHeaders().get(0).getFromValue());
        assertEquals("b", report.getBlobHeaders().get(0).getToValue());
        assertTrue(report.getTypeDiffs().isEmpty());
    }
}
