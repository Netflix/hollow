package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHollow;
import com.netflix.vms.transformer.hollowinput.StoriesSynopsesHookHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.HookType;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class StoriesSynopsesProcessor extends AbstractL10NProcessor {

    public StoriesSynopsesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (StoriesSynopsesHollow item : api.getAllStoriesSynopsesHollow()) {
            final int itemId = (int) item._getMovieId();

            { // narrativeText
                final String resourceId = L10nResourceIdLookup.getNarrativeTextId(itemId);
                addL10NResources(resourceId, item._getNarrativeText()._getTranslatedTexts());
            }

            { // hooks
                for (StoriesSynopsesHookHollow hook : item._getHooks()) {
                    String type = hook._getType()._getValue();
                    final String resourceId = L10nResourceIdLookup.getHookTextId(itemId, HookType.toHookType(type));
                    addL10NResources(resourceId, hook._getTranslatedTexts());
                }
            }
        }

        return api.getAllStoriesSynopsesHollow().size();
    }
}