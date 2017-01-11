package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.AssetMetaDatasHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;

public class AssetMetaDatasProcessor extends AbstractL10NMiscProcessor<AssetMetaDatasHollow> {

    public AssetMetaDatasProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<AssetMetaDatasHollow> getInputs() {
        return api.getAllAssetMetaDatasHollow();
    }

    @Override
    public void processInput(AssetMetaDatasHollow input) {
        final String itemId = input._getAssetId()._getValue();

        final String resourceId = L10nResourceIdLookup.getTrackLabelId(itemId);
        addL10NResources(resourceId, input._getTrackLabels());
    }

}