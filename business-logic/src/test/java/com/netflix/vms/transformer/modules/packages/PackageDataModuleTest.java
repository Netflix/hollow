package com.netflix.vms.transformer.modules.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableMap;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.type.NFCountry;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.converterpojos.DealCountryGroup;
import com.netflix.vms.transformer.converterpojos.Package;
import com.netflix.vms.transformer.converterpojos.PackageMovieDealCountryGroup;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.helper.HollowReadStateEngineBuilder;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;

public class PackageDataModuleTest {
    private static final long PACKAGE_ID = 1337L;
    private static final int VIDEO_ID = 137;

    @Test
    public void testConvertPackage_notInPackageMovieDealCountryGroup() {
        VMSHollowInputAPI api = createAPI(createPackage());
        PackageHollow packageHollow = api.getAllPackageHollow().iterator().next();
        PackageDataModule packageDataModule = getPackageDataModule(api);
        assertNull(packageDataModule.convertPackage(packageHollow, VIDEO_ID));
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

    private static PackageDataModule getPackageDataModule(VMSHollowInputAPI api) {
        HollowReadStateEngine readStateEngine = (HollowReadStateEngine)  api.getDataAccess();
        TransformerContext mockContext = mock(TransformerContext.class);
        VMSTransformerIndexer indexer = new VMSTransformerIndexer(readStateEngine, mockContext);
        GatekeeperStatusRetriever statusRetriever = new GatekeeperStatusRetriever(api, indexer);
        return new PackageDataModule(api, mockContext, mock(HollowObjectMapper.class),
                new CycleConstants(readStateEngine, readStateEngine),  indexer, statusRetriever, null);
    }
}
