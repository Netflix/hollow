package com.netflix.vms.transformer.util.slice;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.traversal.HollowIndexerValueTraverser;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.combine.HollowCombiner;
import com.netflix.hollow.tools.combine.HollowCombinerIncludeOrdinalsCopyDirector;
import com.netflix.vms.generated.notemplate.L10NResourcesHollow;
import com.netflix.vms.generated.notemplate.NamedCollectionHolderHollow;
import com.netflix.vms.generated.notemplate.SetOfVideoHollow;
import com.netflix.vms.generated.notemplate.StringsHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSlicerImpl implements DataSlicer {

    @Override
    public SliceTask getSliceTask(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        return new SliceTaskImpl(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    public SliceTask getSliceTask(Set<String> excludedTypes, boolean isIncludeNonVideoL10N, int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        SliceTaskImpl task = new SliceTaskImpl(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
        task.setIncludeNonVideoL10N(isIncludeNonVideoL10N);
        task.setExcludedTypes(excludedTypes);
        return task;
    }

    public static class SliceTaskImpl implements DataSlicer.SliceTask {
        private final int numberOfRandomTopNodesToInclude;
        private final int[] specificTopNodeIdsToInclude;
        private final Set<Integer> videoIdsToInclude;

        private Map<String, BitSet> ordinalsToInclude;
        private boolean isIncludeNonVideoL10N = true;
        private Set<String> excludedTypes = new HashSet<>();

        public SliceTaskImpl(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
            this.numberOfRandomTopNodesToInclude = numberOfRandomTopNodesToInclude;
            this.specificTopNodeIdsToInclude = specificTopNodeIdsToInclude;
            this.ordinalsToInclude = new HashMap<String, BitSet>();
            this.videoIdsToInclude = new HashSet<Integer>();
        }

        public void setIncludeNonVideoL10N(boolean value) {
            isIncludeNonVideoL10N = value;
        }

        public void setExcludedTypes(Set<String> excludedTypes) {
            this.excludedTypes = excludedTypes;
        }

        @Override
        public HollowWriteStateEngine sliceOutputBlob(HollowReadStateEngine stateEngine) {
            ordinalsToInclude.clear();

            VMSRawHollowAPI outputAPI = new VMSRawHollowAPI(stateEngine);

            if (videoIdsToInclude.isEmpty()) {
                RandomGlobalVideoBasedSelector random = new RandomGlobalVideoBasedSelector(stateEngine);
                this.videoIdsToInclude.addAll(random.findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));
            }

            findIncludedOrdinals(stateEngine, "CompleteVideo", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getCompleteVideoHollow(ordinal)._getId()._getValueBoxed();
                }
            });

            findIncludedOrdinals(stateEngine, "MulticatalogCountryData", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getMulticatalogCountryDataHollow(ordinal)._getVideoId()._getValueBoxed();
                }
            });

            BitSet packagesToInclude = findIncludedOrdinals(stateEngine, "PackageData", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getPackageDataHollow(ordinal)._getVideo()._getValueBoxed();
                }
            });

            findIncludedOrdinals(stateEngine, "VideoPackageData", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getVideoPackageDataHollow(ordinal)._getVideoId()._getValueBoxed();
                }

            });

            findIncludedOrdinals(stateEngine, "RolloutVideo", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getRolloutVideoHollow(ordinal)._getVideo()._getValueBoxed();
                }
            });

            findIncludedOrdinals(stateEngine, "GlobalVideo", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getGlobalVideoHollow(ordinal)._getCompleteVideo()._getId()._getValueBoxed();
                }
            });

            findIncludedOrdinals(stateEngine, "FallbackUSArtwork", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getFallbackUSArtworkHollow(ordinal)._getId()._getValueBoxed();
                }
            });

            findIncludedOrdinals(stateEngine, "LanguageRights", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return outputAPI.getLanguageRightsHollow(ordinal)._getVideoId()._getValueBoxed();
                }
            });

            includeAll(stateEngine, "EncodingProfile");
            includeAll(stateEngine, "OriginServer");
            includeAll(stateEngine, "DeploymentIntent");
            includeAll(stateEngine, "DrmSystem");
            includeAll(stateEngine, "EncodingProfileGroup");
            includeAll(stateEngine, "RolloutCharacter");
            includeAll(stateEngine, "ArtWorkImageFormatEntry");
            includeAll(stateEngine, "ArtWorkImageTypeEntry");
            includeAll(stateEngine, "ArtWorkImageRecipe");
            includeAll(stateEngine, "CharacterImages");
            includeAll(stateEngine, "PersonImages");
            includeAll(stateEngine, "TopNVideoData");
            includeAll(stateEngine, "GlobalPerson");

            ordinalsToInclude.put("L10NResources", findIncludedL10NOrdinals(stateEngine, videoIdsToInclude));

            joinIncludedOrdinals(stateEngine, packagesToInclude,
                    "FileEncodingData", "downloadableId.val",
                    "PackageData", "streams.element.downloadableId.val");

            joinIncludedOrdinals(stateEngine, packagesToInclude,
                    "DrmInfoData", "packageId",
                    "PackageData", "id");

            HollowWriteStateEngine writeStateEngine = populateFilteredBlob(stateEngine, ordinalsToInclude);
            addFilteredNamedLists(stateEngine, outputAPI, writeStateEngine, videoIdsToInclude);
            return writeStateEngine;
        }

        private void addFilteredNamedLists(HollowReadStateEngine readStateEngine, VMSRawHollowAPI api, HollowWriteStateEngine writeStateEngine, Set<Integer> includedVideoIds) {
            HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
            mapper.doNotUseDefaultHashKeys();

            for(NamedCollectionHolderHollow holder : api.getAllNamedCollectionHolderHollow()) {
                NamedCollectionHolder outputHolder = new NamedCollectionHolder();

                outputHolder.country = new ISOCountry(holder._getCountry()._getId());

                outputHolder.videoListMap = new HashMap<Strings, Set<Video>>();

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

                mapper.addObject(outputHolder);
            }
        }

        @Override
        public HollowWriteStateEngine sliceInputBlob(HollowReadStateEngine stateEngine) {
            ordinalsToInclude.clear();

            final VMSHollowInputAPI inputAPI = new VMSHollowInputAPI(stateEngine);

            if (videoIdsToInclude.isEmpty()) {
                RandomShowMovieHierarchyBasedSelector selector = new RandomShowMovieHierarchyBasedSelector(stateEngine);
                videoIdsToInclude.addAll(selector.findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));
            }

            findIncludedOrdinals(stateEngine, "ShowSeasonEpisode", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getShowSeasonEpisodeHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "Package", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int) inputAPI.getPackageHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "Status", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int) inputAPI.getStatusHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "PackageMovieDealCountryGroup", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getPackageMovieDealCountryGroupHollow(ordinal)._getMovieId()._getValue());
                }
            });
            findIncludedOrdinals(stateEngine, "Episodes", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getEpisodesHollow(ordinal)._getEpisodeId());
                }
            });
            findIncludedOrdinals(stateEngine, "LocalizedMetadata", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getLocalizedMetadataHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "MovieRatings", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getMovieRatingsHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "Movies", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getMoviesHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "Rollout", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getRolloutHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "StoriesSynopses", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getStoriesSynopsesHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "Supplementals", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getSupplementalsHollow(ordinal)._getMovieId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoAward", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getVideoAwardHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoDate", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getVideoDateHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoGeneral", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getVideoGeneralHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoRating", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getVideoRatingHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoType", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int)inputAPI.getVideoTypeHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "ShowCountryLabel", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int) inputAPI.getShowCountryLabelHollow(ordinal)._getVideoId());
                }
            });
            findIncludedOrdinals(stateEngine, "VideoArtworkSource", videoIdsToInclude, new VideoIdDeriver() {
                @Override
                public Integer deriveId(int ordinal) {
                    return Integer.valueOf((int) inputAPI.getVideoArtworkSourceHollow(ordinal)._getMovieId());
                }
            });

            includeAll(stateEngine, "DamMerchStills");
            includeAll(stateEngine, "TopN");
            includeAll(stateEngine, "AltGenres");
            includeAll(stateEngine, "ArtWorkImageType");
            includeAll(stateEngine, "ArtworkRecipe");
            includeAll(stateEngine, "AssetMetaDatas");
            includeAll(stateEngine, "Awards");
            includeAll(stateEngine, "CacheDeploymentIntent");
            includeAll(stateEngine, "Categories");
            includeAll(stateEngine, "CategoryGroups");
            includeAll(stateEngine, "Cdn");
            includeAll(stateEngine, "Certifications");
            includeAll(stateEngine, "CertificationSystem");
            includeAll(stateEngine, "Character");
            includeAll(stateEngine, "CharacterArtworkSource");
            includeAll(stateEngine, "Characters");
            includeAll(stateEngine, "ConsolidatedCertificationSystems");
            includeAll(stateEngine, "ConsolidatedVideoRatings");
            includeAll(stateEngine, "DrmSystemIdentifiers");
            includeAll(stateEngine, "Festivals");
            includeAll(stateEngine, "Languages");
            includeAll(stateEngine, "LocalizedCharacter");
            includeAll(stateEngine, "OriginServer");
            includeAll(stateEngine, "PersonAliases");
            includeAll(stateEngine, "PersonArtworkSource");
            includeAll(stateEngine, "Persons");
            includeAll(stateEngine, "ProtectionTypes");
            includeAll(stateEngine, "ShowMemberTypes");
            includeAll(stateEngine, "StorageGroups");
            includeAll(stateEngine, "StreamProfileGroups");
            includeAll(stateEngine, "StreamProfiles");
            includeAll(stateEngine, "TerritoryCountries");
            includeAll(stateEngine, "TurboCollections");
            includeAll(stateEngine, "PersonVideo");
            includeAll(stateEngine, "PersonBio");
            includeAll(stateEngine, "MovieCharacterPerson");
            includeAll(stateEngine, "VMSAward");
            includeAll(stateEngine, "IPLArtworkDerivativeSet");
            includeAll(stateEngine, "AbsoluteSchedule");
            includeAll(stateEngine, "MasterSchedule");
            includeAll(stateEngine, "OverrideSchedule");

            return populateFilteredBlob(stateEngine, ordinalsToInclude);
        }

        private void includeAll(HollowReadStateEngine stateEngine, String type) {
            if (excludedTypes.contains(type)) return;

            ordinalsToInclude.put(type, populatedOrdinals(stateEngine, type));
        }

        private BitSet findIncludedOrdinals(HollowReadStateEngine stateEngine, String type, Set<Integer> includedVideoIds, VideoIdDeriver idDeriver) {
            if (excludedTypes.contains(type) || stateEngine.getTypeState(type) == null) {
                return new BitSet();
            }

            int maxOrdinal = stateEngine.getTypeState(type).maxOrdinal();
            BitSet populatedOrdinals = new BitSet(maxOrdinal + 1);
            for(int i=0;i<maxOrdinal + 1;i++) {
                if(ordinalIsPopulated(stateEngine, type, i)) {
                    if(includedVideoIds.contains(idDeriver.deriveId(i))) {
                        populatedOrdinals.set(i);
                    }
                }
            }

            ordinalsToInclude.put(type, populatedOrdinals);

            return populatedOrdinals;
        }

        private BitSet findIncludedL10NOrdinals(HollowReadStateEngine stateEngine, Set<Integer> includedVideoIds) {
            VMSRawHollowAPI api = new VMSRawHollowAPI(stateEngine);

            BitSet l10nResourcesPopulatedOrdinals = populatedOrdinals(stateEngine, "L10NResources");
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
                    if (isIncludeNonVideoL10N) includedL10nResourcesOrdinals.set(l10nOrdinal);
                }

                l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(l10nOrdinal + 1);
            }

            return includedL10nResourcesOrdinals;
        }

        private void joinIncludedOrdinals(
                HollowReadStateEngine stateEngine,
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

        private boolean ordinalIsPopulated(HollowReadStateEngine stateEngine, String type, int ordinal) {
            return populatedOrdinals(stateEngine, type).get(ordinal);
        }

        private BitSet populatedOrdinals(HollowReadStateEngine stateEngine, String type) {
            if (stateEngine.getTypeState(type) == null) {
                return new BitSet();
            }

            return stateEngine.getTypeState(type).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        }

        private interface VideoIdDeriver {
            Integer deriveId(int ordinal);
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
}
