package com.netflix.vms.transformer.input.datasets.slicers;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.generated.notemplate.L10NResourcesHollow;
import com.netflix.vms.generated.notemplate.NamedCollectionHolderHollow;
import com.netflix.vms.generated.notemplate.SetOfVideoHollow;
import com.netflix.vms.generated.notemplate.StringsHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.common.slice.OutputDataSlicer;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NamedCollectionHolder;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransformerOutputDataSlicer extends DataSlicer implements OutputDataSlicer {

    private boolean isIncludeNonVideoL10N = true;

    public TransformerOutputDataSlicer(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        super(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);
    }

    public TransformerOutputDataSlicer(Set<String> excludedTypes, boolean isIncludeNonVideoL10N, int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        super(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);

        setExcludedTypes(excludedTypes);
        this.isIncludeNonVideoL10N = isIncludeNonVideoL10N;
    }

    @Override
    public HollowWriteStateEngine sliceOutputBlob(HollowReadStateEngine stateEngine) {

        ordinalsToInclude.clear();

        VMSRawHollowAPI outputAPI = new VMSRawHollowAPI(stateEngine);

        if (videoIdsToInclude.isEmpty()) {
            RandomGlobalVideoBasedSelector random = new RandomGlobalVideoBasedSelector(stateEngine);
            videoIdsToInclude.addAll(random.findRandomVideoIds(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));
        }

        findIncludedOrdinals(stateEngine, "CompleteVideo", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getCompleteVideoHollow(ordinal)._getId()._getValueBoxed();
            }
        });

        findIncludedOrdinals(stateEngine, "MulticatalogCountryData", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getMulticatalogCountryDataHollow(ordinal)._getVideoId()._getValueBoxed();
            }
        });

        BitSet packagesToInclude = findIncludedOrdinals(stateEngine, "PackageData", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getPackageDataHollow(ordinal)._getVideo()._getValueBoxed();
            }
        });

        findIncludedOrdinals(stateEngine, "VideoPackageData", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getVideoPackageDataHollow(ordinal)._getVideoId()._getValueBoxed();
            }

        });

        findIncludedOrdinals(stateEngine, "RolloutVideo", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getRolloutVideoHollow(ordinal)._getVideo()._getValueBoxed();
            }
        });

        findIncludedOrdinals(stateEngine, "GlobalVideo", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getGlobalVideoHollow(ordinal)._getCompleteVideo()._getId()._getValueBoxed();
            }
        });

        findIncludedOrdinals(stateEngine, "FallbackUSArtwork", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
            @Override
            public Integer deriveId(int ordinal) {
                return outputAPI.getFallbackUSArtworkHollow(ordinal)._getId()._getValueBoxed();
            }
        });

        findIncludedOrdinals(stateEngine, "LanguageRights", videoIdsToInclude, new DataSlicer.VideoIdDeriver() {
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
                if (this.isIncludeNonVideoL10N) includedL10nResourcesOrdinals.set(l10nOrdinal);
            }

            l10nOrdinal = l10nResourcesPopulatedOrdinals.nextSetBit(l10nOrdinal + 1);
        }

        return includedL10nResourcesOrdinals;
    }
}
