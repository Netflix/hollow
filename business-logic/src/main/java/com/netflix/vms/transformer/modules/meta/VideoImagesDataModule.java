package com.netflix.vms.transformer.modules.meta;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.*;

import com.netflix.vms.transformer.util.NFLocaleUtil;

import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeSetHollow;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.LocaleTerritoryCodeHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TerritoryCountriesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoArtworkHollow;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.VideoImages;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.artwork.ArtWorkModule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VideoImagesDataModule extends ArtWorkModule {
    private final HollowHashIndex videoArtworkIndex;

    public VideoImagesDataModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        super("Video", api, ctx, mapper, cycleConstants, indexer);

        this.videoArtworkIndex = indexer.getHashIndex(IndexSpec.ARTWORK_BY_VIDEO_ID);
    }

    public Map<String, Map<Integer, VideoImages>> buildVideoImagesByCountry(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry) {
        Set<Integer> ids = new HashSet<>();
        for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
            for(VideoHierarchy hierarchy : entry.getValue()) {
                ids.addAll(hierarchy.getAllIds());
            }
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
            ctx.getLogger().error(MissingLocaleForArtwork, "Missing artwork locale for {} with id={}; data will be dropped.", entityType, entityId);
            return;
        }

        String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
        int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
        int seqNum = (int) artworkHollowInput._getSeqNum();
        ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
        ArtworkDerivativeSetHollow inputDerivatives = artworkHollowInput._getDerivatives();
        
        Artwork artwork = new Artwork();

        // Process list of derivatives
        processDerivativesAndCdnList(entityId, sourceFileId, inputDerivatives, artwork);

        artwork.sourceFileId = new Strings(sourceFileId);
        artwork.seqNum = seqNum;
        artwork.ordinalPriority = ordinalPriority;
        fillPassThroughData(artwork, attributes);

        // Support Country based data
        for (ArtworkLocaleHollow localeHollow : localeSet) {
            Artwork localeArtwork = artwork.clone();
            localeArtwork.locale = NFLocaleUtil.createNFLocale(localeHollow._getBcp47Code()._getValue());
            localeArtwork.effectiveDate = localeHollow._getEffectiveDate()._getValue();
            
            for (String countryCode : getCountryCodes(localeHollow)) {
                Map<Integer, Set<Artwork>> artMap = getArtworkMap(countryCode, countryArtworkMap);
                Set<Artwork> artworkSet = getArtworkSet(entityId, artMap);
                artworkSet.add(localeArtwork);
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
                ctx.getLogger().error(InvalidImagesTerritoryCode, "Invalid TerritoryCode={} in entityType={}", codeHollow._getValue(), entityType);
                continue;
            }
        }
        return countrySet;
    }
}
