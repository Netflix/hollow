package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.LocalizedMetadataHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class LocalizedMetadataProcessor extends AbstractL10NVideoProcessor<LocalizedMetadataHollow> {

    public LocalizedMetadataProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper, indexer, IndexSpec.L10N_LOCALIZEDMETADATA);
    }

    @Override
    protected LocalizedMetadataHollow getDataForOrdinal(int ordinal) {
        return api.getLocalizedMetadataHollow(ordinal);
    }

    @Override
    public void processInput(LocalizedMetadataHollow input) {
        final int inputId = (int) input._getMovieId();
        final String name = input._getAttributeName()._getValue();
        final String label = input._getLabel()._getValue();

        final String resourceId = L10nResourceIdLookup.getRolloutAttribResourceId(inputId, name, label);
        addL10NResources(resourceId, input._getTranslatedTexts());
    }
}