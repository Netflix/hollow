package com.netflix.vms.transformer.modules.countryspecific;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class VMSAvailabilityWindowModuleTest {
    private static final int VIDEO_ID = 1337;
    private static final String COUNTRY = "US";

    @Mock
    private TransformerContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getLogger()).thenReturn(mock(TaggingLogger.class));
    }

    @Test
    public void testGetMaxPackageContractInfo_returnsMaxPackageId() {
        WindowPackageContractInfo maxPackage = getPackageContractInfo(7, true);
        assertEquals(maxPackage, VMSAvailabilityWindowModule.getMaxPackageContractInfo(context,
                VIDEO_ID, COUNTRY, Arrays.asList(
                        getPackageContractInfo(4, true),
                        maxPackage,
                        getPackageContractInfo(6, true))));
    }

    @Test
    public void testGetMaxPackageContractInfo_ignoresNonDefaultPackages() {
        WindowPackageContractInfo maxPackage = getPackageContractInfo(7, true);
        assertEquals(maxPackage, VMSAvailabilityWindowModule.getMaxPackageContractInfo(context,
                VIDEO_ID, COUNTRY, Arrays.asList(
                        maxPackage,
                        getPackageContractInfo(8, false))));
    }

    @Test
    public void testGetMaxPackageContractInfo_getsMaxPackageIfNoDefault() {
        WindowPackageContractInfo maxPackage = getPackageContractInfo(2, false);
        assertEquals(maxPackage, VMSAvailabilityWindowModule.getMaxPackageContractInfo(context,
                VIDEO_ID, COUNTRY, Arrays.asList(
                        getPackageContractInfo(1, false),
                        maxPackage)));
    }

    @Test
    public void testGetMaxPackageContractInfo_getsSingleNonDefault() {
        WindowPackageContractInfo maxPackage = getPackageContractInfo(2, true);
        assertEquals(maxPackage, VMSAvailabilityWindowModule.getMaxPackageContractInfo(context,
                VIDEO_ID, COUNTRY, Collections.singletonList(maxPackage)));
    }

    private static WindowPackageContractInfo getPackageContractInfo(int packageId,
            boolean defaultPackage) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoPackageInfo = new VideoPackageInfo();
        info.videoPackageInfo.packageId = packageId;
        info.videoPackageInfo.isDefaultPackage = defaultPackage;
        return info;
    }
}
