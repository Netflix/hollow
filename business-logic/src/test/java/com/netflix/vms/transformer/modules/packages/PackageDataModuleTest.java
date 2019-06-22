package com.netflix.vms.transformer.modules.packages;

import static com.netflix.vms.transformer.data.gen.gatekeeper2.FlagsTestData.FlagsField.alternateLanguage;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.FlagsTestData.FlagsField.goLive;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.FlagsTestData.FlagsField.liveOnSite;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsContractAssetTestData.RightsContractAsset;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsContractAssetTestData.RightsContractAssetField.assetType;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsContractAssetTestData.RightsContractAssetField.bcp47Code;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsTestData.RightsField.windows;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowContractTestData.RightsWindowContract;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowContractTestData.RightsWindowContractField.assets;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowContractTestData.RightsWindowContractField.dealId;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowContractTestData.RightsWindowContractField.download;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowContractTestData.RightsWindowContractField.packageId;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowTestData.RightsWindow;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowTestData.RightsWindowField.contractIdsExt;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowTestData.RightsWindowField.endDate;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.RightsWindowTestData.RightsWindowField.startDate;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.StatusTestData.StatusField.countryCode;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.StatusTestData.StatusField.flags;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.StatusTestData.StatusField.movieId;
import static com.netflix.vms.transformer.data.gen.gatekeeper2.StatusTestData.StatusField.rights;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.CONVERTER;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.GATEKEEPER2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.type.NFCountry;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.converterpojos.DealCountryGroup;
import com.netflix.vms.transformer.converterpojos.Package;
import com.netflix.vms.transformer.converterpojos.PackageMovieDealCountryGroup;
import com.netflix.vms.transformer.data.gen.gatekeeper2.Gk2StatusTestData;
import com.netflix.vms.transformer.helper.HollowReadStateEngineBuilder;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.StatusDelegate;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageDataModuleTest {
    private static final long PACKAGE_ID = 1337L;
    private static final int VIDEO_ID = 137;

    @Mock Gatekeeper2Dataset mockGk2Dataset;
    @Mock Gk2StatusAPI mockGk2StatusAPI;
    @Mock HollowReadStateEngine mockHollowReadStateEngine;
    @Mock StatusDelegate mockStatusDelegate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockGk2Dataset.getAPI()).thenReturn(mockGk2StatusAPI);
        when(mockGk2StatusAPI.getDataAccess()).thenReturn(mockHollowReadStateEngine);
    }
    @Test
    public void testConvertPackage_notInPackageMovieDealCountryGroup() {
        VMSHollowInputAPI api = createAPI(createPackage());
        PackageHollow packageHollow = api.getAllPackageHollow().iterator().next();
        PackageDataModule packageDataModule = getPackageDataModule(api);
        assertNull(packageDataModule.convertPackage(packageHollow, VIDEO_ID));
    }

    private HollowReadStateEngine createGatekeeper2ReadStateEngine() {
        Gk2StatusTestData data = new Gk2StatusTestData();

        data.Status(
                movieId(1L),
                countryCode("US"),
                flags(
                        goLive(true),
                        alternateLanguage("en"),
                        liveOnSite(true)
                ),
                rights(
                    windows(
                        RightsWindow(
                            startDate(Long.MIN_VALUE + 1),
                            endDate(Long.MAX_VALUE),
                            contractIdsExt(
                                    RightsWindowContract(
                                            dealId(1l),
                                            packageId(1L),
                                            download(true),
                                            assets(
                                                    RightsContractAsset(
                                                            bcp47Code("testCode"),
                                                            assetType("testType")
                                                    )
                                            )
                                    )
                            ))
                        )
                    )
                );


        return data.build();
    }

    @Test
    public void testConvertPackage_noDeployableCountries() {
        VMSHollowInputAPI api = createAPI(createPackage(),
                new PackageMovieDealCountryGroup((long) VIDEO_ID, PACKAGE_ID)
                        .setDealCountryGroups(new ArrayList<>()));
        PackageHollow packageHollow = api.getAllPackageHollow().iterator().next();
        PackageDataModule packageDataModule = getPackageDataModule(api);
        assertNull(packageDataModule.convertPackage(packageHollow, VIDEO_ID));
    }

    @Test
    public void testConvertPackage_hasDeployableCountries() {
        Map<String, Boolean> countryWindow = ImmutableMap.of(NFCountry.US.getId(), true);
        DealCountryGroup group = new DealCountryGroup(7L).setCountryWindow(countryWindow);
        VMSHollowInputAPI api = createAPI(createPackage(),
                new PackageMovieDealCountryGroup((long) VIDEO_ID, PACKAGE_ID)
                        .setDealCountryGroups(Collections.singletonList(group)));
        PackageHollow packageHollow = api.getAllPackageHollow().iterator().next();
        PackageDataModule packageDataModule = getPackageDataModule(api);
        PackageDataCollection data = packageDataModule.convertPackage(packageHollow, VIDEO_ID);
        assertEquals(Collections.singleton(new ISOCountry("US")),
                data.getPackageData().allDeployableCountries);
    }

    private static Package createPackage() {
        return new Package(PACKAGE_ID, VIDEO_ID).setDownloadables(new HashSet<>());
    }

        private static VMSHollowInputAPI createAPI(Object... objects) {
        HollowReadStateEngineBuilder builder = new HollowReadStateEngineBuilder();
        Arrays.stream(objects).forEach(builder::add);
        return new VMSHollowInputAPI(builder.build());
    }

    private PackageDataModule getPackageDataModule(VMSHollowInputAPI api) {
        HollowReadStateEngine readStateEngine = (HollowReadStateEngine)  api.getDataAccess();
        TransformerContext mockContext = mock(TransformerContext.class);
        VMSTransformerIndexer indexer = new VMSTransformerIndexer(readStateEngine, mockContext);

        Map<UpstreamDatasetHolder.Dataset, InputState> inputs = new HashMap<>();
        inputs.put(CONVERTER, new InputState(readStateEngine, 1l));
        inputs.put(GATEKEEPER2, new InputState(createGatekeeper2ReadStateEngine(), 1l));
        UpstreamDatasetHolder upstream = UpstreamDatasetHolder.getNewDatasetHolder(inputs);

        return new PackageDataModule(upstream, mockContext, mock(HollowObjectMapper.class),
                new CycleConstants(upstream),  indexer, null);
    }
}
