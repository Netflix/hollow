package com.netflix.vms.transformer.modules.meta;

import com.netflix.vms.transformer.common.TransformerLogger.LogTag;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.LocaleTerritoryCodeHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TerritoryCountriesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoArtworkHollow;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.ArtworkCdn;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivative;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.artwork.ArtWorkModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoImagesDataModule extends ArtWorkModule {
    private final HollowHashIndex videoArtworkIndex;

    public VideoImagesDataModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super("Video", api, ctx, mapper, indexer);

        this.videoArtworkIndex = indexer.getHashIndex(IndexSpec.ARTWORK_BY_VIDEO_ID);
    }

    public Map<String, Map<Integer, VideoImages>> buildVideoImagesByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Set<Integer> ids = new HashSet<>();
        for (Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            ids.addAll(entry.getValue().getAllIds());
        }

        Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap = new HashMap<>();
        for (Integer videoId : ids) {
            HollowHashIndexResult matches = videoArtworkIndex.findMatches((long) videoId);
            if (matches != null) {
                HollowOrdinalIterator iter = matches.iterator();
                int videoArtworkOrdinal = iter.next();
                while (videoArtworkOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    VideoArtworkHollow artworkHollowInput = api.getVideoArtworkHollow(videoArtworkOrdinal);
                    processArtwork(artworkHollowInput, countryArtworkMap);
                    videoArtworkOrdinal = iter.next();
                }
            }
        }

        // Create VideoImages
        Map<String, Map<Integer, VideoImages>> countryImagesMap = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Set<Artwork>>> countryEntry : countryArtworkMap.entrySet()) {
            String countryCode = countryEntry.getKey();
            Map<Integer, Set<Artwork>> artMap = countryEntry.getValue();

            Map<Integer, VideoImages> imagesMap = new HashMap<>();
            countryImagesMap.put(countryCode, imagesMap);

            for (Map.Entry<Integer, Set<Artwork>> entry : artMap.entrySet()) {
                VideoImages images = new VideoImages();
                Integer id = entry.getKey();

                Set<Artwork> artworkSet = entry.getValue();
                images.artworks = createArtworkByTypeMap(artworkSet);
                images.artworkFormatsByType = createFormatByTypeMap(artworkSet);

                imagesMap.put(id, images);
            }
        }
        return countryImagesMap;
    }

    @Override
    public void transform() {
        throw new UnsupportedOperationException("Use buildVideoImagesByCountry");
    }

    private void processArtwork(VideoArtworkHollow artworkHollowInput, Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap) {
        ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
        int entityId = (int) artworkHollowInput._getMovieId();

        Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
        if (localeSet.isEmpty()) {
            ctx.getLogger().error(LogTag.MissingLocaleForArtwork, String.format("Missing artwork locale for %s with id=%d; data will be dropped.", entityType, entityId));
            return;
        }

        String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
        int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
        int seqNum = (int) artworkHollowInput._getSeqNum();
        ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
        ArtworkDerivativeListHollow derivatives = artworkHollowInput._getDerivatives();

        List<ArtworkDerivative> derivativeList = new ArrayList<ArtworkDerivative>();
        List<ArtworkCdn> cdnList = new ArrayList<ArtworkCdn>();
        processDerivatives(entityId, derivatives, derivativeList, cdnList);

        // Support Country based data
        for (ArtworkLocaleHollow localeHollow : localeSet) {
            for (String countryCode : getCountryCodes(localeHollow)) {
                Map<Integer, Set<Artwork>> artMap = getArtworkMap(countryCode, countryArtworkMap);
                Set<Artwork> artworkSet = getArtworkSet(entityId, artMap);
                createArtworkForLocale(localeHollow, sourceFileId, ordinalPriority, seqNum, attributes, derivativeList, cdnList, artworkSet);
            }
        }
    }

    protected Map<Integer, Set<Artwork>> getArtworkMap(String countryCode, Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap) {
        Map<Integer, Set<Artwork>> artMap = countryArtworkMap.get(countryCode);
        if (artMap == null) {
            artMap = new HashMap<>();
            countryArtworkMap.put(countryCode, artMap);
        }

        return artMap;
    }

    protected Set<String> getCountryCodes(ArtworkLocaleHollow artworkLocaleHollow) {
        Set<String> countrySet = new HashSet<>();
        for (LocaleTerritoryCodeHollow ltCodeHollow : artworkLocaleHollow._getTerritoryCodes()) {
            StringHollow codeHollow = ltCodeHollow._getValue();
            int ordinal = territoryIdx.getMatchingOrdinal(codeHollow._getValue());
            if (ordinal != -1) {
                TerritoryCountriesHollow territoryCountryHollow = api.getTerritoryCountriesHollow(ordinal);
                for (ISOCountryHollow countryHollow : territoryCountryHollow._getCountryCodes()) {
                    countrySet.add(countryHollow._getValue());
                }
            } else {
                ctx.getLogger().error(LogTag.InvalidImagesTerritoryCode, String.format("Invalid TerritoryCode=%s in entityType=%s", codeHollow._getValue(), entityType));
                continue;
            }
        }
        return countrySet;
    }
}
