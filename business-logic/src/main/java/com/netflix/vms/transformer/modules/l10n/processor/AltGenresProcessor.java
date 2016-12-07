package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.AltGenresAlternateNamesHollow;
import com.netflix.vms.transformer.hollowinput.AltGenresHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;

public class AltGenresProcessor extends AbstractL10NMiscProcessor<AltGenresHollow> {

    public AltGenresProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<AltGenresHollow> getInputs() {
        return api.getAllAltGenresHollow();
    }

    @Override
    public void processInput(AltGenresHollow input) {
        final int inputId = (int) input._getAltGenreId();

        {
            final String resourceId = L10nResourceIdLookup.getAltGenreNameID(inputId);
            addL10NResources(resourceId, input._getDisplayName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getAltGenreShortNameID(inputId);
            addL10NResources(resourceId, input._getShortName());
        }

        for (AltGenresAlternateNamesHollow altName : input._getAlternateNames()) {
            final String resourceId = L10nResourceIdLookup.getAltGenreAlternateNameID(inputId, (int) altName._getTypeId());
            addL10NResources(resourceId, altName._getTranslatedTexts());
        }
    }
}