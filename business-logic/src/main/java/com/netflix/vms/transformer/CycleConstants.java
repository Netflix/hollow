package com.netflix.vms.transformer;

import com.netflix.vms.transformer.contract.ContractAsset;

import com.netflix.vms.transformer.hollowoutput.ArtworkCdn;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivative;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivatives;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfIntegerToListOfVideoEpisode;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoNodeType;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.util.InputOrdinalResultCache;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class CycleConstants {

    public final VideoNodeType MOVIE = new VideoNodeType("MOVIE");
    public final VideoNodeType SHOW = new VideoNodeType("SHOW");
    public final VideoNodeType SEASON = new VideoNodeType("SEASON");
    public final VideoNodeType EPISODE = new VideoNodeType("EPISODE");
    public final VideoNodeType SUPPLEMENTAL = new VideoNodeType("SUPPLEMENTAL");

    public final VideoSetType PAST = new VideoSetType("Past");
    public final VideoSetType PRESENT = new VideoSetType("Present");
    public final VideoSetType FUTURE = new VideoSetType("Future");
    public final VideoSetType EXTENDED = new VideoSetType("Extended");

    public final Date HOLD_BACK_INDEFINITELY_DATE = null;
    public final Date EXEMPT_HOLD_BACK_DATE = new Date((new DateTime(1997, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)).getMillis());

    public final VideoFormatDescriptor VIDEOFORMAT_UNKNOWN = videoFormatDescriptor(-1, "unknown", "unknown");
    public final VideoFormatDescriptor SD = videoFormatDescriptor(2, "SD", "Standard Definition");
    public final VideoFormatDescriptor HD = videoFormatDescriptor(1, "HD", "HiDefinition");
    public final VideoFormatDescriptor SUPER_HD = videoFormatDescriptor(3, "Super_HD", "Super HiDefinition");
    public final VideoFormatDescriptor ULTRA_HD = videoFormatDescriptor(4, "Ultra_HD", "Ultra HiDefinition");
    public final int ULTRA_HD_MIN_HEIGHT = 1081;

    public final VideoImages EMPTY_VIDEO_IMAGES = emptyVideoImages();
    public final SortedMapOfDateWindowToListOfInteger EMPTY_DATE_WINDOW_SEASON_SEQ_MAP = new SortedMapOfDateWindowToListOfInteger(Collections.emptyMap());
    public final SortedMapOfIntegerToListOfVideoEpisode EMPTY_EPISODE_SEQUENCE_NUMBER_MAP = new SortedMapOfIntegerToListOfVideoEpisode(Collections.<com.netflix.vms.transformer.hollowoutput.Integer, List<VideoEpisode>>emptyMap());
    
    public final InputOrdinalResultCache<ArtworkDerivative> artworkDerivativeCache;
    public final InputOrdinalResultCache<ArtworkDerivatives> artworkDerivativesCache;
    public final InputOrdinalResultCache<List<ArtworkCdn>> cdnListCache;
    
    public final InputOrdinalResultCache<ContractAsset> rightsContractAssetCache;
    
    
    public CycleConstants(HollowReadStateEngine inputStateEngine) {
        this.artworkDerivativeCache = new InputOrdinalResultCache<ArtworkDerivative>(inputStateEngine.getTypeState("ArtworkDerivative").maxOrdinal());
        this.artworkDerivativesCache = new InputOrdinalResultCache<ArtworkDerivatives>(inputStateEngine.getTypeState("ArtworkDerivativeSet").maxOrdinal());
        this.cdnListCache = new InputOrdinalResultCache<List<ArtworkCdn>>(inputStateEngine.getTypeState("ArtworkDerivativeSet").maxOrdinal());
        this.rightsContractAssetCache = new InputOrdinalResultCache<ContractAsset>(inputStateEngine.getTypeState("RightsContractAsset").maxOrdinal());
    }

    private static VideoFormatDescriptor videoFormatDescriptor(int id, String name, String description) {
        VideoFormatDescriptor descriptor = new VideoFormatDescriptor();
        descriptor.id = id;
        descriptor.name = new Strings(name);
        descriptor.description = new Strings(description);
        return descriptor;
    }

    private static VideoImages emptyVideoImages() {
        VideoImages videoImages = new VideoImages();
        videoImages.artworks = Collections.emptyMap();
        videoImages.artworkFormatsByType = Collections.emptyMap();
        return videoImages;
    }
}
