package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.DefaultExtensionRecipeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.DefaultExtensionRecipe;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

public class DefaultExtensionRecipeModule extends AbstractTransformModule {

    public DefaultExtensionRecipeModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        mapper.addObject(new DefaultExtensionRecipe());

        for(DefaultExtensionRecipeHollow inExtRec : api.getAllDefaultExtensionRecipeHollow()) {
            DefaultExtensionRecipe outExtRec = new DefaultExtensionRecipe();
            outExtRec.extensionStr = inExtRec._getExtension()._getValue().toCharArray();
            outExtRec.recipeNameStr = inExtRec._getRecipeName()._getValue().toCharArray();
        }
    }
}
