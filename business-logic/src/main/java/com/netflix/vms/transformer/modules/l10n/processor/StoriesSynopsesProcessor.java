package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHookHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.l10n.HookType;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class StoriesSynopsesProcessor extends AbstractL10NVideoProcessor<StoriesSynopsesHollow> {

    public StoriesSynopsesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper, indexer, IndexSpec.L10N_STORIES_SYNOPSES);
    }

    @Override
    protected StoriesSynopsesHollow getDataForOrdinal(int ordinal) {
        return api.getStoriesSynopsesHollow(ordinal);
    }

    @Override
    public void processInput(StoriesSynopsesHollow input) {
        final int inputId = (int) input._getMovieId();

        { // narrativeText
            final String resourceId = L10nResourceIdLookup.getNarrativeTextId(inputId);
            addL10NResources(resourceId, input._getNarrativeText());
        }

        { // hooks
            for (StoriesSynopsesHookHollow hook : input._getHooks()) {
                String type = hook._getType()._getValue();
                final String resourceId = L10nResourceIdLookup.getHookTextId(inputId, HookType.toHookType(type));
                addL10NResources(resourceId, hook._getTranslatedTexts());
            }
        }
    }
}