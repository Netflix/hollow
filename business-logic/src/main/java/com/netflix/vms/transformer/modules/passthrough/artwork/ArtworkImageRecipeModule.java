package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.vms.transformer.CycleConstants;

import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkRecipeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

public class ArtworkImageRecipeModule extends AbstractTransformModule {

    public ArtworkImageRecipeModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper) {
        super(api, ctx, cycleConstants, mapper);
    }

    @Override
    public void transform() {
        for (ArtworkRecipeHollow inRecipe : api.getAllArtworkRecipeHollow()) {
            ArtWorkImageRecipe outRecipe = new ArtWorkImageRecipe();
            outRecipe.recipeNameStr = inRecipe._getRecipeName()._getValue().toCharArray();
            outRecipe.extensionStr = inRecipe._getExtension()._getValue().toCharArray();
            if (inRecipe._getCdnFolder() != null && inRecipe._getCdnFolder()._getValue() != null) {
                outRecipe.cdnFolderStr = inRecipe._getCdnFolder()._getValue().toCharArray();
            }

            if (inRecipe._getHostName() != null && inRecipe._getHostName()._getValue() != null) {
                outRecipe.hostNameStr = inRecipe._getHostName()._getValue().toCharArray();
            }
            mapper.addObject(outRecipe);
        }
    }
}