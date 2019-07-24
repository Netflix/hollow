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

    public final int numberOfRandomTopNodesToInclude;
    public final int[] specificTopNodeIdsToInclude;

    private boolean isIncludeNonVideoL10N = true;

    public TransformerOutputDataSlicer(int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        super();

        this.numberOfRandomTopNodesToInclude = numberOfRandomTopNodesToInclude;
        this.specificTopNodeIdsToInclude = specificTopNodeIdsToInclude;
    }

    public TransformerOutputDataSlicer(Set<String> excludedTypes, boolean isIncludeNonVideoL10N, int numberOfRandomTopNodesToInclude, int... specificTopNodeIdsToInclude) {
        this(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude);

        setExcludedTypes(excludedTypes);
        this.isIncludeNonVideoL10N = isIncludeNonVideoL10N;
    }

    @Override
    public HollowWriteStateEngine sliceOutputBlob(HollowReadStateEngine stateEngine) {

        clearOrdinalsToInclude();

        VMSRawHollowAPI outputAPI = new VMSRawHollowAPI(stateEngine);

        GlobalVideoBasedSelector videoSelector = new GlobalVideoBasedSelector(stateEngine);
        addVideoIdsToInclude(
                videoSelector.findVideosForTopNodes(numberOfRandomTopNodesToInclude, specificTopNodeIdsToInclude));

        findIncludedOrdinals(stateEngine, "CompleteVideo", ordinal ->
                outputAPI.getCompleteVideoHollow(ordinal)._getId()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "MulticatalogCountryData", ordinal ->
                outputAPI.getMulticatalogCountryDataHollow(ordinal)._getVideoId()._getValueBoxed());
        BitSet packagesToInclude = findIncludedOrdinals(stateEngine, "PackageData", ordinal ->
                outputAPI.getPackageDataHollow(ordinal)._getVideo()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "VideoPackageData", ordinal ->
                outputAPI.getVideoPackageDataHollow(ordinal)._getVideoId()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "RolloutVideo", ordinal ->
                outputAPI.getRolloutVideoHollow(ordinal)._getVideo()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "GlobalVideo", ordinal ->
                outputAPI.getGlobalVideoHollow(ordinal)._getCompleteVideo()._getId()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "FallbackUSArtwork", ordinal ->
                outputAPI.getFallbackUSArtworkHollow(ordinal)._getId()._getValueBoxed());
        findIncludedOrdinals(stateEngine, "LanguageRights", ordinal ->
                outputAPI.getLanguageRightsHollow(ordinal)._getVideoId()._getValueBoxed());

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

        findIncludedL10NOrdinals(stateEngine, "L10NResources", isIncludeNonVideoL10N);

        joinIncludedOrdinals(stateEngine, packagesToInclude,
                "FileEncodingData", "downloadableId.val",
                "PackageData", "streams.element.downloadableId.val");

        joinIncludedOrdinals(stateEngine, packagesToInclude,
                "DrmInfoData", "packageId",
                "PackageData", "id");

        HollowWriteStateEngine writeStateEngine = populateFilteredBlob(stateEngine);
        addFilteredNamedLists(outputAPI, writeStateEngine);
        return writeStateEngine;
    }
}
