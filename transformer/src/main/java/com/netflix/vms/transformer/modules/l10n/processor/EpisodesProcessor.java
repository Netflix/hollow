package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.EpisodesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class EpisodesProcessor extends AbstractL10NProcessor<EpisodesHollow> {

    public EpisodesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<EpisodesHollow> getInputs() {
        return api.getAllEpisodesHollow();
    }

    @Override
    public void processInput(EpisodesHollow input) {
        final int inputId = (int) input._getEpisodeId();

        final String resourceId = L10nResourceIdLookup.getEpisodeTitleID(inputId);
        addL10NResources(resourceId, input._getEpisodeName());
    }
}