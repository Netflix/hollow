package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.DefaultExtensionRecipeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.DefaultExtensionRecipe;

public class DefaultExtensionRecipeModule {
	private final VMSHollowVideoInputAPI api;
	private final HollowObjectMapper mapper;
	
	public DefaultExtensionRecipeModule(VMSHollowVideoInputAPI api,
			HollowObjectMapper mapper) {
		super();
		this.api = api;
		this.mapper = mapper;
	}
	
    public void transform() {
    	mapper.addObject(new DefaultExtensionRecipe());
    	
        for(DefaultExtensionRecipeHollow inExtRec : api.getAllDefaultExtensionRecipeHollow()) {
        	DefaultExtensionRecipe outExtRec = new DefaultExtensionRecipe();
        	outExtRec.extensionStr = inExtRec._getExtension()._getValue().toCharArray();
        	outExtRec.recipeNameStr = inExtRec._getRecipeName()._getValue().toCharArray();
        }
    }
}
