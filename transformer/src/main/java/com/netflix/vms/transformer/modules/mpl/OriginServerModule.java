package com.netflix.vms.transformer.modules.mpl;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CdnsHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryListHollow;
import com.netflix.vms.transformer.hollowinput.OriginServersHollow;
import com.netflix.vms.transformer.hollowinput.StorageGroupsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.CdnData;
import com.netflix.vms.transformer.hollowoutput.OriginServer;
import com.netflix.vms.transformer.hollowoutput.StorageGroup;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.util.ISOCountryUtil;

public class OriginServerModule extends AbstractTransformModule {

    private final HollowPrimaryKeyIndex storageGroupsIndex;
    private final HollowPrimaryKeyIndex cdnsIndex;

    public OriginServerModule(VMSHollowVideoInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
        this.storageGroupsIndex = indexer.getPrimaryKeyIndex(IndexSpec.STORAGE_GROUPS);
        this.cdnsIndex = indexer.getPrimaryKeyIndex(IndexSpec.CDNS);
    }

    @Override
    public void transform() {
        for (OriginServersHollow input : api.getAllOriginServersHollow()) {
            OriginServer output = new OriginServer();
            output.nameStr = input._getName()._getValue().toCharArray();

            String storageGroupId = input._getStorageGroupId()._getValue();
            int storageGroupOrdinal = storageGroupsIndex.getMatchingOrdinal(storageGroupId);
            StorageGroupsHollow storageGroupInput = api.getStorageGroupsHollow(storageGroupOrdinal);
            output.storageGroup = createStorageGroup(storageGroupInput);

            long cdnId = storageGroupInput._getCdnId();
            int cdnOrdinal = cdnsIndex.getMatchingOrdinal(cdnId);
            CdnsHollow cdnInput = api.getCdnsHollow(cdnOrdinal);
            output.cdnData = createCdnData(cdnInput);

            mapper.addObject(output);
        }
    }

    private CdnData createCdnData(CdnsHollow input) {
        CdnData output = new CdnData();
        output.id = (int) input._getId();
        output.name = new Strings(input._getName()._getValue());

        return output;
    }

    private StorageGroup createStorageGroup(StorageGroupsHollow input) {
        StorageGroup output = new StorageGroup();
        output.idStr = input._getId()._getValue().toCharArray();

        ISOCountryListHollow countries = input._getCountries();
        output.countries = ISOCountryUtil.createList(countries);

        return output;
    }

}