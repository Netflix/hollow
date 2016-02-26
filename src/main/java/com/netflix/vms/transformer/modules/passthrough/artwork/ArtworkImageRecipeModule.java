package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.ArtworkRecipeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;

public class ArtworkImageRecipeModule {

	private final VMSHollowVideoInputAPI api;
	private final HollowObjectMapper mapper;
	
	public ArtworkImageRecipeModule(VMSHollowVideoInputAPI api,
			HollowObjectMapper mapper) {
		super();
		this.api = api;
		this.mapper = mapper;
	}
	
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
