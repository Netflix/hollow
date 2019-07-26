package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.contract.ContractAsset;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivative;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivatives;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfIntegerToListOfVideoEpisode;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VideoEpisode;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.hollowoutput.VideoNodeType;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.modules.countryspecific.MultilanguageCountryDialectOrdinalAssigner;
import com.netflix.vms.transformer.util.InputOrdinalResultCache;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    // @TODO: VideoFormat should exclude video resolution in the future
    public final VideoFormatDescriptor VIDEOFORMAT_UNKNOWN = videoFormatDescriptor(-1, "unknown", "unknown");
    @Deprecated public final VideoFormatDescriptor SD = videoFormatDescriptor(2, "SD", "Standard Definition");
    @Deprecated public final VideoFormatDescriptor HD = videoFormatDescriptor(1, "HD", "HiDefinition");
    @Deprecated public final VideoFormatDescriptor SUPER_HD = videoFormatDescriptor(3, "Super_HD", "Super HiDefinition");
    @Deprecated public final VideoFormatDescriptor ULTRA_HD = videoFormatDescriptor(4, "Ultra_HD", "Ultra HiDefinition");
    @Deprecated public final VideoFormatDescriptor FOUR_K = videoFormatDescriptor(5, "4K", "4K For Search");
    public final VideoFormatDescriptor HDR = videoFormatDescriptor(6, "HDR", "HDR For Search");
    public final VideoFormatDescriptor ATMOS = videoFormatDescriptor(7, "ATMOS", "Dolby Atmos For Search");
    @Deprecated public final int ULTRA_HD_MIN_HEIGHT = 1081;

    public final VideoImages EMPTY_VIDEO_IMAGES = emptyVideoImages();
    public final SortedMapOfDateWindowToListOfInteger EMPTY_DATE_WINDOW_SEASON_SEQ_MAP = new SortedMapOfDateWindowToListOfInteger(Collections.emptyMap());
    public final SortedMapOfIntegerToListOfVideoEpisode EMPTY_EPISODE_SEQUENCE_NUMBER_MAP = new SortedMapOfIntegerToListOfVideoEpisode(Collections.<com.netflix.vms.transformer.hollowoutput.Integer, List<VideoEpisode>>emptyMap());

    public final InputOrdinalResultCache<ArtworkDerivative> artworkDerivativeCache;
    public final InputOrdinalResultCache<ArtworkDerivatives> artworkDerivativesCache;
    public final InputOrdinalResultCache<String> artworkDerivativeOverlayTypes;
    public final InputOrdinalResultCache<Boolean> isNewEpisodeOverlayTypes;

    public final InputOrdinalResultCache<ContractAsset> gk2RightsContractAssetCache;
    public final MultilanguageCountryDialectOrdinalAssigner dialectOrdinalAssigner = new MultilanguageCountryDialectOrdinalAssigner();

    private final ConcurrentHashMap<String, ISOCountry> isoCountryMap = new ConcurrentHashMap<String, ISOCountry>();


    public CycleConstants(UpstreamDatasetHolder upstream) {

        HollowReadStateEngine converter = upstream.getDataset(CONVERTER).getInputState().getStateEngine();
        HollowReadStateEngine gk2 = upstream.getDataset(GATEKEEPER2).getInputState().getStateEngine();

        this.artworkDerivativeCache = new InputOrdinalResultCache<>(converter.getTypeState("IPLArtworkDerivative").maxOrdinal());
        this.artworkDerivativesCache = new InputOrdinalResultCache<>(converter.getTypeState("IPLDerivativeSet").maxOrdinal());
        this.gk2RightsContractAssetCache = new InputOrdinalResultCache<>(gk2.getTypeState("RightsContractAsset").maxOrdinal());
        this.artworkDerivativeOverlayTypes = new InputOrdinalResultCache<>(converter.getTypeState("ListOfDerivativeTag").maxOrdinal());
        this.isNewEpisodeOverlayTypes = new InputOrdinalResultCache<>(converter.getTypeState("ListOfDerivativeTag").maxOrdinal());
    }

    private CycleConstants() {
        this.artworkDerivativeCache = null;
        this.artworkDerivativesCache = null;
        this.gk2RightsContractAssetCache = null;
        this.artworkDerivativeOverlayTypes = null;
        this.isNewEpisodeOverlayTypes = null;
    }

    public static CycleConstants getInstanceForTesting() {
        return new CycleConstants();
    }

    public ISOCountry getISOCountry(String countryId) {
        ISOCountry country = isoCountryMap.get(countryId);
        if(country == null) {
            String uppercaseCountry = countryId.toUpperCase();

            if(countryId.length() != 2 || uppercaseCountry.charAt(0) < 'A' || uppercaseCountry.charAt(0) > 'Z' ||
                    uppercaseCountry.charAt(1) < 'A' || uppercaseCountry.charAt(1) > 'Z')
                throw new IllegalArgumentException("The country code \"" + countryId + "\" is invalid.");


            country = new ISOCountry(uppercaseCountry);
            ISOCountry existingCountry = isoCountryMap.putIfAbsent(countryId, country);
            if(existingCountry != null)
                country = existingCountry;
        }

        return country;
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
