package com.netflix.vmsserver;

import com.netflix.hollow.combine.HollowCombiner;
import com.netflix.hollow.combine.HollowCombinerIncludeOrdinalsCopyDirector;
import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.EpisodeHollow;
import com.netflix.vms.generated.notemplate.GlobalPersonHollow;
import com.netflix.vms.generated.notemplate.GlobalVideoHollow;
import com.netflix.vms.generated.notemplate.L10NResourcesHollow;
import com.netflix.vms.generated.notemplate.NamedCollectionHolderHollow;
import com.netflix.vms.generated.notemplate.PersonRoleHollow;
import com.netflix.vms.generated.notemplate.SetOfEpisodeHollow;
import com.netflix.vms.generated.notemplate.SetOfVPersonHollow;
import com.netflix.vms.generated.notemplate.SetOfVideoHollow;
import com.netflix.vms.generated.notemplate.StringsHollow;
import com.netflix.vms.generated.notemplate.SupplementalVideoHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VPersonHollow;
import com.netflix.vms.generated.notemplate.VideoCollectionsDataHollow;
import com.netflix.vms.generated.notemplate.VideoEpisodeHollow;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.generated.notemplate.VideoNodeTypeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFResourceID;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.io.LZ4VMSInputStream;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import net.jpountz.lz4.LZ4BlockInputStream;

/// NOTE:  This has a dependency on videometadata-common (for LZ4VMSInputStream)
/// NOTE:  This has a dependency on vms-hollow-generated-notemplate (for output blob API)

public class FilterToSmallDataSubset {

    private static final String WORKING_DIR = "/space/transformer-data";

    private static final int TARGET_NUMBER_OF_TOPNODES = 1000;
    private static final String ORIGINAL_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/berlin-snapshot";
    private static final String ORIGINAL_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-blobs/input-snapshot";

    private static final String FILTERED_OUTPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/control-output";
    private static final String FILTERED_INPUT_BLOB_LOCATION = WORKING_DIR + "/pinned-subsets/filtered-input";

    private HollowReadStateEngine stateEngine;
    private HollowPrimaryKeyIndex globalVideoIdx;
    private HollowPrimaryKeyIndex completeVideoPrimaryKeyIdx;
    private HollowHashIndex completeVideoHashIdx;
    private VMSRawHollowAPI outputAPI;

    private Map<String, BitSet> ordinalsToInclude;

    @Test
    public void doFilter() throws Exception {
        Set<Integer> includedVideoIds = filterOutputBlob();
        filterInputBlob(includedVideoIds);
    }

    private Set<Integer> filterOutputBlob() throws IOException, FileNotFoundException {
        stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        reader.readSnapshot(new LZ4VMSInputStream(new FileInputStream(ORIGINAL_OUTPUT_BLOB_LOCATION)));

        outputAPI = new VMSRawHollowAPI(stateEngine);
        globalVideoIdx = new HollowPrimaryKeyIndex(stateEngine, "GlobalVideo", "completeVideo.id.value");
        completeVideoHashIdx = new HollowHashIndex(stateEngine, "CompleteVideo", "", "id.value");
        completeVideoPrimaryKeyIdx = new HollowPrimaryKeyIndex(stateEngine, "CompleteVideo", "id.value", "country.id");
        ordinalsToInclude = new HashMap<String, BitSet>();


        Set<Integer> includedVideoIds = findRandomVideoIds(stateEngine);

        findIncludedOrdinals("CompleteVideo", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getCompleteVideoHollow(ordinal)._getId()._getValueBoxed();
            }
        });

        BitSet packagesToInclude = findIncludedOrdinals("PackageData", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getPackageDataHollow(ordinal)._getVideo()._getValueBoxed();
            }
        });

        findIncludedOrdinals("RolloutVideo", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getRolloutVideoHollow(ordinal)._getVideo()._getValueBoxed();
            }
        });

        findIncludedOrdinals("GlobalVideo", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getGlobalVideoHollow(ordinal)._getCompleteVideo()._getId()._getValueBoxed();
            }
        });

        findIncludedOrdinals("VideoEpisode_CountryList", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getVideoEpisode_CountryListHollow(ordinal)._getItem()._getDeliverableVideo()._getValueBoxed();
            }
        });

        findIncludedOrdinals("FallbackUSArtwork", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getFallbackUSArtworkHollow(ordinal)._getId()._getValueBoxed();
            }
        });

        //includeAll("NamedCollectionHolder");   //// TODO: Filter this to included Video IDs only
        includeAll("EncodingProfile");
        includeAll("OriginServer");
        includeAll("DeploymentIntent");
        includeAll("DrmSystem");
        includeAll("EncodingProfileGroup");
        includeAll("RolloutCharacter");
        includeAll("ArtWorkImageFormatEntry");
        includeAll("ArtWorkImageTypeEntry");
        includeAll("ArtWorkImageRecipe");
        includeAll("DefaultExtensionRecipe");
        includeAll("CharacterImages");
        includeAll("PersonImages");
        includeAll("TopNVideoData");
        includeAll("GlobalPerson");
        includeAll("LanguageRights");

        ordinalsToInclude.put("L10NResources", findIncludedL10NOrdinals(includedVideoIds));

        joinIncludedOrdinals(packagesToInclude,
                "FileEncodingData", "downloadableId",
                "PackageData", "streams.element.downloadableId");

        joinIncludedOrdinals(packagesToInclude,
                "DrmInfoData", "packageId",
                "PackageData", "id");


        HollowWriteStateEngine writeStateEngine = populateFilteredBlob(stateEngine, ordinalsToInclude);
        addFilteredNamedLists(stateEngine, outputAPI, writeStateEngine, includedVideoIds);
        writeBlob(FILTERED_OUTPUT_BLOB_LOCATION, writeStateEngine);

        return includedVideoIds;
    }

    private void addFilteredNamedLists(HollowReadStateEngine readStateEngine, VMSRawHollowAPI api, HollowWriteStateEngine writeStateEngine, Set<Integer> includedVideoIds) {
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);

        HollowPrimaryKeyIndex globalPersonIdx = new HollowPrimaryKeyIndex(readStateEngine, "GlobalPerson", "id");

        for(NamedCollectionHolderHollow holder : api.getAllNamedCollectionHolderHollow()) {
            NamedCollectionHolder outputHolder = new NamedCollectionHolder();

            outputHolder.country = new ISOCountry(holder._getCountry()._getId());

            outputHolder.videoListMap = new HashMap<Strings, Set<Video>>();
            outputHolder.episodeListMap = new HashMap<Strings, Set<Episode>>();
            outputHolder.personListMap = new HashMap<Strings, Set<VPerson>>();
            outputHolder.resourceIdListMap = Collections.emptyMap();

            Set<Integer> validVideoIdsForCountry = new HashSet<Integer>();

            for(Map.Entry<StringsHollow, SetOfVideoHollow> entry : holder._getVideoListMap().entrySet()) {
                Set<Video> videoSet = new HashSet<Video>();

                for(VideoHollow video : entry.getValue()) {
                    Integer value = video._getValueBoxed();

                    if(includedVideoIds.contains(value)) {
                        videoSet.add(new Video(value.intValue()));
                    }
                }

                if("VALID_VIDEOS".equals(entry.getKey()._getValue())) {
                    for(Video video : videoSet)
                        validVideoIdsForCountry.add(video.value);
                }

                outputHolder.videoListMap.put(new Strings(entry.getKey()._getValue()), videoSet);
            }

            for(Map.Entry<StringsHollow, SetOfEpisodeHollow> entry : holder._getEpisodeListMap().entrySet()) {
                Set<Episode> episodeSet = new HashSet<Episode>();

                for(EpisodeHollow ep : entry.getValue()) {
                    Integer id = ep._getIdBoxed();

                    if(includedVideoIds.contains(id)) {
                        episodeSet.add(new Episode(id.intValue()));
                    }
                }

                outputHolder.episodeListMap.put(new Strings(entry.getKey()._getValue()), episodeSet);
            }

            for(Map.Entry<StringsHollow, SetOfVPersonHollow> entry : holder._getPersonListMap().entrySet()) {
                Set<VPerson> personSet = new HashSet<VPerson>();

                for(VPersonHollow person : entry.getValue()) {
                    Integer id = person._getIdBoxed();

                    int personOrdinal = globalPersonIdx.getMatchingOrdinal(id);

                    if(personOrdinal != -1) {
                        GlobalPersonHollow globalPerson = api.getGlobalPersonHollow(personOrdinal);

                        for(PersonRoleHollow personRole : globalPerson._getPersonRoles()) {
                            if(validVideoIdsForCountry.contains(personRole._getVideo()._getValueBoxed())) {
                                personSet.add(new VPerson(id.intValue()));
                                break;
                            }
                        }
                    }
                }

                outputHolder.personListMap.put(new Strings(entry.getKey()._getValue()), personSet);
            }


            mapper.addObject(outputHolder);
        }

        mapper.addObject(new NFResourceID("invalid"));
    }

    private void filterInputBlob(Set<Integer> includedVideoIds) throws IOException, FileNotFoundException {
        ordinalsToInclude.clear();
        stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        reader.readSnapshot(new LZ4BlockInputStream(new FileInputStream(ORIGINAL_INPUT_BLOB_LOCATION)));

        final VMSHollowInputAPI inputAPI = new VMSHollowInputAPI(stateEngine);

        findIncludedOrdinals("ShowSeasonEpisode", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getShowSeasonEpisodeHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("Package", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int) inputAPI.getPackageHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("VideoRights", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoRightsHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("CSMReview", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getCSMReviewHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("DeployablePackages", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getDeployablePackagesHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("Episodes", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getEpisodesHollow(ordinal)._getEpisodeId());
            }
        });
        findIncludedOrdinals("LocalizedMetadata", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getLocalizedMetadataHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("MovieRatings", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getMovieRatingsHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("Movies", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getMoviesHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("Rollout", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getRolloutHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("StoriesSynopses", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getStoriesSynopsesHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("Supplementals", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getSupplementalsHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("VideoArtwork", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoArtworkHollow(ordinal)._getMovieId());
            }
        });
        findIncludedOrdinals("VideoAward", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoAwardHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("VideoDate", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoDateHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("VideoGeneral", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoGeneralHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("VideoRating", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoRatingHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("VideoType", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int)inputAPI.getVideoTypeHollow(ordinal)._getVideoId());
            }
        });
        findIncludedOrdinals("ShowCountryLabel", includedVideoIds, new VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return Integer.valueOf((int) inputAPI.getShowCountryLabelHollow(ordinal)._getVideoId());
            }
        });

        includeAll("TopN");
        includeAll("AltGenres");
        includeAll("ArtWorkImageFormat");
        includeAll("ArtWorkImageType");
        includeAll("ArtworkRecipe");
        includeAll("AssetMetaDatas");
        includeAll("Awards");
        includeAll("CacheDeploymentIntent");
        includeAll("Categories");
        includeAll("CategoryGroups");
        includeAll("Cdn");
        includeAll("Certifications");
        includeAll("CertificationSystem");
        includeAll("Character");
        includeAll("CharacterArtwork");
        includeAll("Characters");
        includeAll("ConsolidatedCertificationSystems");
        includeAll("ConsolidatedVideoRatings");
        includeAll("DefaultExtensionRecipe");
        includeAll("DrmSystemIdentifiers");
        includeAll("Festivals");
        includeAll("Languages");
        includeAll("LocalizedCharacter");
        includeAll("OriginServer");
        includeAll("PersonAliases");
        includeAll("PersonArtwork");
        includeAll("Persons");
        includeAll("ProtectionTypes");
        includeAll("Ratings");
        includeAll("ShowMemberTypes");
        includeAll("StorageGroups");
        includeAll("StreamProfileGroups");
        includeAll("StreamProfiles");
        includeAll("TerritoryCountries");
        includeAll("TurboCollections");
        includeAll("PersonVideo");
        includeAll("PersonBio");
        includeAll("VMSAward");

        writeFilteredBlob(FILTERED_INPUT_BLOB_LOCATION, stateEngine, ordinalsToInclude);
    }

    private void includeAll(String type) {
        ordinalsToInclude.put(type, populatedOrdinals(type));
    }

    private BitSet findIncludedOrdinals(String type, Set<Integer> includedVideoIds, VideoIdDeriver idDeriver) {
        int maxOrdinal = stateEngine.getTypeState(type).maxOrdinal();
        BitSet populatedOrdinals = new BitSet(maxOrdinal + 1);
        for(int i=0;i<maxOrdinal + 1;i++) {
            if(ordinalIsPopulated(type, i)) {
                if(includedVideoIds.contains(idDeriver.deriveId(i))) {
                    populatedOrdinals.set(i);
                }
            }
        }

        ordinalsToInclude.put(type, populatedOrdinals);

        return populatedOrdinals;
    }

    private Set<Integer> findRandomVideoIds(HollowReadStateEngine stateEngine) {
        Random rand = new Random(1000);
        Set<Integer> topNodeVideoIds = new HashSet<Integer>();
        Set<Integer> allVideoIds = new HashSet<Integer>();

        int maxGlobalVideoOrdinal = stateEngine.getTypeState("GlobalVideo").maxOrdinal();

        while(topNodeVideoIds.size() < TARGET_NUMBER_OF_TOPNODES) {
            int randomOrdinal = rand.nextInt(maxGlobalVideoOrdinal + 1);

            if(ordinalIsPopulated("GlobalVideo", randomOrdinal)) {
                GlobalVideoHollow vid = outputAPI.getGlobalVideoHollow(randomOrdinal);

                addIdsBasedOnGlobalVideo(topNodeVideoIds, allVideoIds, vid);
            }
        }

        /// The following two topnodes are a strange case: the same episodes are included in two different
        /// show hierarchies for different countries.
        GlobalVideoHollow vid = outputAPI.getGlobalVideoHollow(globalVideoIdx.getMatchingOrdinal(80074321));
        addIdsBasedOnGlobalVideo(topNodeVideoIds, allVideoIds, vid);
        vid = outputAPI.getGlobalVideoHollow(globalVideoIdx.getMatchingOrdinal(80006146));
        addIdsBasedOnGlobalVideo(topNodeVideoIds, allVideoIds, vid);

        return allVideoIds;
    }

    private void addIdsBasedOnGlobalVideo(Set<Integer> topNodeVideoIds, Set<Integer> allVideoIds, GlobalVideoHollow vid) {
        HollowHashIndexResult completeVideos = completeVideoHashIdx.findMatches(vid._getCompleteVideo()._getId()._getValueBoxed());

        HollowOrdinalIterator completeVideoIterator = completeVideos.iterator();
        int completeVideoOrdinal = completeVideoIterator.next();

        while(completeVideoOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            CompleteVideoHollow completeVideo = outputAPI.getCompleteVideoHollow(completeVideoOrdinal);
            String countryCode = completeVideo._getCountry()._getId();
            VideoCollectionsDataHollow videoCollectionsData = completeVideo._getFacetData()._getVideoCollectionsData();
            VideoNodeTypeHollow nodeType = videoCollectionsData._getNodeType();
            if(nodeType._isValueEqual("SHOW") || nodeType._isValueEqual("MOVIE")) {
                Integer videoId = vid._getCompleteVideo()._getId()._getValueBoxed();
                topNodeVideoIds.add(videoId);
                allVideoIds.add(videoId);

                for(VideoEpisodeHollow episode : videoCollectionsData._getVideoEpisodes()) {
                    Integer episodeId = episode._getDeliverableVideo()._getValueBoxed();
                    allVideoIds.add(episodeId);
                    addAllSupplementalVideoIds(episodeId, countryCode, allVideoIds);
                }

                for(VideoHollow season : videoCollectionsData._getShowChildren()) {
                    Integer seasonId = season._getValueBoxed();
                    allVideoIds.add(seasonId);
                    addAllSupplementalVideoIds(seasonId, countryCode, allVideoIds);
                }

                addAllSupplementalVideoIds(videoId, countryCode, allVideoIds);
            }

            completeVideoOrdinal = completeVideoIterator.next();
        }
    }

    private void addAllSupplementalVideoIds(Integer videoId, String countryCode, Set<Integer> toSet) {
        int completeVideoOrdinal = completeVideoPrimaryKeyIdx.getMatchingOrdinal(videoId, countryCode);
        CompleteVideoHollow vid = outputAPI.getCompleteVideoHollow(completeVideoOrdinal);

        VideoCollectionsDataHollow videoCollectionsData = vid._getFacetData()._getVideoCollectionsData();

        for(SupplementalVideoHollow supplemental : videoCollectionsData._getSupplementalVideos()) {
            toSet.add(supplemental._getId()._getValueBoxed());
        }
    }

    private BitSet findIncludedL10NOrdinals(Set<Integer> includedVideoIds) {
        VMSRawHollowAPI api = new VMSRawHollowAPI(stateEngine);

        BitSet l10nResourcesPopulatedOrdinals = populatedOrdinals("L10NResources");
        BitSet includedL10nResourcesOrdinals = new BitSet(l10nResourcesPopulatedOrdinals.length());

        int l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(0);
        while(l10nOrdinal != -1) {
            L10NResourcesHollow l10nResources = api.getL10NResourcesHollow(l10nOrdinal);

            String idStr = l10nResources._getResourceIdStr();
            char c = idStr.charAt(0);
            if (c == 'm' || c == 'e' || (c == 'r' && idStr.startsWith("rv_"))) {
                // filter video related resourceIds
                for(int i=0;i<idStr.length();i++) {
                    if(isNumberCharacter(idStr.charAt(i))) {
                        int j=i+1;
                        while(j < idStr.length() && isNumberCharacter(idStr.charAt(j)))
                            j++;

                        int id = (int)Long.parseLong(idStr.substring(i, j));
                        if(includedVideoIds.contains(Integer.valueOf(id))) {
                            includedL10nResourcesOrdinals.set(l10nOrdinal);
                            break;
                        }

                        i = j;
                    }
                }
            } else {
                includedL10nResourcesOrdinals.set(l10nOrdinal);
            }

            l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(l10nOrdinal + 1);
        }

        return includedL10nResourcesOrdinals;
    }

    private void joinIncludedOrdinals(
            BitSet fromOrdinals,
            String toType, String toField,
            String fromType, String... fromFields) {

        HollowPrimaryKeyIndex pkIdx = new HollowPrimaryKeyIndex(stateEngine, toType, toField);

        BitSet includedOrdinals = new BitSet();

        for(String fromField : fromFields) {
            HollowIndexerValueTraverser traverser = new HollowIndexerValueTraverser(stateEngine, fromType, fromField);


            int ordinal = fromOrdinals.nextSetBit(0);
            while(ordinal != -1) {
                traverser.traverse(ordinal);
                for(int i=0;i<traverser.getNumMatches();i++) {
                    Object key = traverser.getMatchedValue(i, 0);
                    //System.out.println("MATCHED KEY: " + key);
                    int personImagesOrdinal = pkIdx.getMatchingOrdinal(key);
                    if(personImagesOrdinal != -1) {
                        //System.out.println("joined ordinal: " + personImagesOrdinal);
                        includedOrdinals.set(personImagesOrdinal);
                    }
                }

                ordinal = fromOrdinals.nextSetBit(ordinal + 1);
            }
        }

        ordinalsToInclude.put(toType, includedOrdinals);
    }

    private boolean isNumberCharacter(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean ordinalIsPopulated(String type, int ordinal) {
        return populatedOrdinals(type).get(ordinal);
    }

    private BitSet populatedOrdinals(String type) {
        return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
    }

    private interface VideoIdDeriver {
        Integer deriveId(int ordinal);
    }

    private static void writeFilteredBlob(String filename, HollowReadStateEngine inputStateEngine, Map<String, BitSet> ordinalsToInclude) throws FileNotFoundException, IOException {
        HollowWriteStateEngine outputStateEngine = populateFilteredBlob(inputStateEngine, ordinalsToInclude);

        writeBlob(filename, outputStateEngine);
    }

    private static void writeBlob(String filename, HollowWriteStateEngine outputStateEngine) throws FileNotFoundException, IOException {
        HollowBlobWriter writer = new HollowBlobWriter(outputStateEngine);
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
        writer.writeSnapshot(os);
        os.close();
    }

    private static HollowWriteStateEngine populateFilteredBlob(HollowReadStateEngine inputStateEngine, Map<String, BitSet> ordinalsToInclude) {
        Map<String, String> headerTags = inputStateEngine.getHeaderTags();
        HollowCombiner combiner = new HollowCombiner(new HollowCombinerIncludeOrdinalsCopyDirector(ordinalsToInclude), inputStateEngine);

        combiner.combine();
        HollowWriteStateEngine outputStateEngine = combiner.getCombinedStateEngine();
        outputStateEngine.addHeaderTags(headerTags);

        System.out.println(String.format("writeFilteredBlob headers:"));
        for (Map.Entry<String, String> entry : headerTags.entrySet()) {
            System.out.println(String.format("\t %s=%s", entry.getKey(), entry.getValue()));
        }
        return outputStateEngine;
    }
}
