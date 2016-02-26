package com.netflix.vms.transformer.modules.passthrough.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.ArtWorkImageFormatHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import java.util.Collection;

public class ArtworkFormatModule {
    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    public ArtworkFormatModule (VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

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
