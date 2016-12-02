package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoImages implements Cloneable {

    public Map<Strings, List<Artwork>> artworks = null;
    public Map<ArtWorkImageTypeEntry, Set<ArtWorkImageFormatEntry>> artworkFormatsByType = null;
    public List<SchedulePhaseInfo> imageAvailabilityWindows = null;

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VideoImages other = (VideoImages) obj;
		if (artworkFormatsByType == null) {
			if (other.artworkFormatsByType != null)
				return false;
		} else if (!artworkFormatsByType.equals(other.artworkFormatsByType))
			return false;
		if (artworks == null) {
			if (other.artworks != null)
				return false;
		} else if (!artworks.equals(other.artworks))
			return false;
		if (imageAvailabilityWindows == null) {
			if (other.imageAvailabilityWindows != null)
				return false;
		} else if (!imageAvailabilityWindows.equals(other.imageAvailabilityWindows))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((artworkFormatsByType == null) ? 0 : artworkFormatsByType
						.hashCode());
		result = prime * result
				+ ((artworks == null) ? 0 : artworks.hashCode());
		result = prime * result
				+ ((imageAvailabilityWindows == null) ? 0 : imageAvailabilityWindows.hashCode());
		return result;
	}

    @Override
	public String toString() {
		return "VideoImages [artworks=" + artworks + ", artworkFormatsByType="
				+ artworkFormatsByType + ", artworksWithPhaseDates="
				+ "]";
	}

    public VideoImages clone() {
        try {
            VideoImages clone = (VideoImages)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}