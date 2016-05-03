package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkRecipeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

public class ArtworkImageRecipeModule extends AbstractTransformModule {

    public ArtworkImageRecipeModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        for(ArtworkRecipeHollow inRecipe : api.getAllArtworkRecipeHollow()) {
            ArtWorkImageRecipe outRecipe = new ArtWorkImageRecipe();
            outRecipe.recipeNameStr = inRecipe._getRecipeName()._getValue().toCharArray();
            outRecipe.extensionStr = inRecipe._getExtension()._getValue().toCharArray();
            outRecipe.cdnFolderStr = inRecipe._getCdnFolder()._getValue().toCharArray();
            outRecipe.hostNameStr = null;
            mapper.addObject(outRecipe);
        }
    }
}