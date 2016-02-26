package com.netflix.vms.transformer.modules.passthrough.artwork;


import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.ArtWorkImageTypeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;

public class ArtworkTypeModule {
	private final String UNAVAILABLE_STRING = "unavailable";
	private final boolean TRUE = true;
	
	private final VMSHollowVideoInputAPI api;
	private final HollowObjectMapper mapper;
	
	public ArtworkTypeModule(VMSHollowVideoInputAPI api,
			HollowObjectMapper mapper) {
		super();
		this.api = api;
		this.mapper = mapper;
	}
	
    public void transform() {
        for(ArtWorkImageTypeHollow inputImageType : api.getAllArtWorkImageTypeHollow()) {
        	ArtWorkImageTypeEntry outputImageType = new ArtWorkImageTypeEntry();
        	outputImageType.nameStr = inputImageType._getImageType()._getValue().toCharArray();
        	outputImageType.recipeNameStr = inputImageType._getRecipe()._getValue().toCharArray();
        	outputImageType.unavailableFileNameStr = UNAVAILABLE_STRING.toCharArray();
        	outputImageType.allowMultiples = TRUE;
        	mapper.addObject(outputImageType);
        }
    }
	
}
