package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.EpisodesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class EpisodesProcessor extends AbstractL10NProcessor {

    public EpisodesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (EpisodesHollow item : api.getAllEpisodesHollow()) {
            final int itemId = (int) item._getEpisodeId();

            {
                final String resourceId = L10nResourceIdLookup.getEpisodeTitleID(itemId);
                addL10NResources(resourceId, item._getEpisodeName()._getTranslatedTexts());
            }
        }

        return api.getAllCategoriesHollow().size();
    }
}