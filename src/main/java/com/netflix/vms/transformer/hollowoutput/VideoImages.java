package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class VideoImages {

    public List<ArtWorkDescriptor> artWorkDescriptors;
    public ArtWorkIndex artWorkIndex;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoImages))
            return false;

        VideoImages o = (VideoImages) other;
        if(o.artWorkDescriptors == null) {
            if(artWorkDescriptors != null) return false;
        } else if(!o.artWorkDescriptors.equals(artWorkDescriptors)) return false;
        if(o.artWorkIndex == null) {
            if(artWorkIndex != null) return false;
        } else if(!o.artWorkIndex.equals(artWorkIndex)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}