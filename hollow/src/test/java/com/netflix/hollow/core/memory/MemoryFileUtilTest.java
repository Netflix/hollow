package com.netflix.hollow.core.memory;

import org.junit.Assert;
import org.junit.Test;

public class MemoryFileUtilTest {

    @Test
    public void testFilenames() {

        Assert.assertTrue(MemoryFileUtil.varLengthDataFilename("TimecodeAnnotation", "STRING", 0)
                .startsWith("hollow-varLengthData-STRING_TimecodeAnnotation_0_"));

        Assert.assertTrue(MemoryFileUtil.fixedLengthDataFilename("MapOfISOCountryToSetOfContractRestriction", "mapPointerAndSizeData", 0)
                .startsWith("hollow-fixedLengthData-mapPointerAndSizeData_MapOfISOCountryToSetOfContractRestriction_0_"));

        Assert.assertTrue(MemoryFileUtil.fixedLengthDataFilename("GlobalVideo", null, 0)
                .startsWith("hollow-fixedLengthData-GlobalVideo_0_"));
    }
}
