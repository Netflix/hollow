package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.EpisodesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class EpisodesProcessor extends AbstractL10NVideoProcessor<EpisodesHollow> {

    public EpisodesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper, indexer, IndexSpec.L10N_EPISODES);
    }

    @Override
    protected EpisodesHollow getDataForOrdinal(int ordinal) {
        return api.getEpisodesHollow(ordinal);
    }

    @Override
    public void processInput(EpisodesHollow input) {
        final int inputId = (int) input._getEpisodeId();

        final String resourceId = L10nResourceIdLookup.getEpisodeTitleID(inputId);
        addL10NResources(resourceId, input._getEpisodeName());
    }
}