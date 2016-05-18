package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.AltGenresAlternateNamesHollow;
import com.netflix.vms.transformer.hollowinput.AltGenresHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class AltGenresProcessor extends AbstractL10NProcessor {

    public AltGenresProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public int processResources() {
        for (AltGenresHollow item : api.getAllAltGenresHollow()) {
            final int itemId = (int) item._getAltGenreId();

            {
                final String resourceId = L10nResourceIdLookup.getAltGenreNameID(itemId);
                addL10NResources(resourceId, item._getDisplayName()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getAltGenreShortNameID(itemId);
                addL10NResources(resourceId, item._getShortName()._getTranslatedTexts());
            }

            for (AltGenresAlternateNamesHollow altName : item._getAlternateNames()) {
                final String resourceId = L10nResourceIdLookup.getAltGenreAlternateNameID(itemId, (int) altName._getTypeId());
                addL10NResources(resourceId, altName._getTranslatedTexts());
            }
        }

        return api.getAllAltGenresHollow().size();
    }
}