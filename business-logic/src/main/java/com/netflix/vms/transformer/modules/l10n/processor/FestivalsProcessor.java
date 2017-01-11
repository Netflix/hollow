package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.FestivalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;

public class FestivalsProcessor extends AbstractL10NMiscProcessor<FestivalsHollow> {

    public FestivalsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<FestivalsHollow> getInputs() {
        return api.getAllFestivalsHollow();
    }

    @Override
    public void processInput(FestivalsHollow input) {
        final int inputId = (int) input._getFestivalId();

        {
            final String resourceId = L10nResourceIdLookup.getFestivalCopyrightID(inputId);
            addL10NResources(resourceId, input._getCopyright());
        }

        {
            final String resourceId = L10nResourceIdLookup.getFestivalNameID(inputId);
            addL10NResources(resourceId, input._getFestivalName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getFestivalDescriptionID(inputId);
            addL10NResources(resourceId, input._getDescription());
        }

        //            {
        //                final String resourceId = L10nResourceIdLookup.getFestivalLocationID(inputId);
        //                addL10NResources(resourceId, input._???);
        //            }

        {
            final String resourceId = L10nResourceIdLookup.getFestivalShortNameID(inputId);
            addL10NResources(resourceId, input._getShortName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getFestivalSingularNameID(inputId);
            addL10NResources(resourceId, input._getSingularName());
        }
    }
}