package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CategoriesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;
import java.util.Collection;

public class CategoriesProcessor extends AbstractL10NMiscProcessor<CategoriesHollow> {

    public CategoriesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }


    @Override
    public Collection<CategoriesHollow> getInputs() {
        return api.getAllCategoriesHollow();
    }

    @Override
    public void processInput(CategoriesHollow input) {
        final int inputId = (int) input._getCategoryId();

        final String resourceId = L10nResourceIdLookup.getCategoryNameID(inputId);
        addL10NResources(resourceId, input._getDisplayName());
    }
}