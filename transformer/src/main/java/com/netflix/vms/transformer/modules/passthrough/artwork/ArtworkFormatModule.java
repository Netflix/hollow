package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtWorkImageFormatHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.Collection;

public class ArtworkFormatModule extends AbstractTransformModule {
    public ArtworkFormatModule (VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        Collection<ArtWorkImageFormatHollow> inputs = api.getAllArtWorkImageFormatHollow();
        for (ArtWorkImageFormatHollow input : inputs) {
            ArtWorkImageFormatEntry artworkFormatEntry = new ArtWorkImageFormatEntry();
            artworkFormatEntry.height = (int) input._getHeight();
            artworkFormatEntry.width = (int) input._getWidth();
            artworkFormatEntry.nameStr = input._getFormat()._getValue().toCharArray();
            mapper.addObject(artworkFormatEntry);
        }
    }
}