package com.netflix.vms.transformer;

import com.netflix.hollow.HollowBlobHeader;
import com.netflix.hollow.HollowSchema;
import com.netflix.hollow.filter.HollowFilterConfig;
import com.netflix.hollow.read.engine.HollowBlobHeaderReader;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.memory.WastefulRecycler;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.videometadata.compression.LZ4VMSInputStream;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import net.jpountz.lz4.LZ4BlockInputStream;

public class ShowMeTheProgress {

    private static final boolean isUseFastLane = false;

    private static final String ROOT_DATA_DIR = "/space/transformer-data/pinned-" + (isUseFastLane ? "blobs" : "subsets");
    private static final String CONTROL_FN = isUseFastLane ? "berlin-snapshot" : "control-output";
    private static final String INPUT_FN = isUseFastLane ? "input-snapshot" : "filtered-input";

    private static final String PUBLISH_CYCLE_DATATS_HEADER = "publishCycleDataTS";

    @Test
    public void start() throws Throwable {
        HollowReadStateEngine inputStateEngine = loadStateEngine(INPUT_FN);
        VMSHollowInputAPI api = new VMSHollowInputAPI(inputStateEngine);

        // header control blob header
        Map<String, String> headerTags = getHeaderTagsFromStateEngine(CONTROL_FN);
        String value = headerTags.get(PUBLISH_CYCLE_DATATS_HEADER);
        long publishCycleDataTS = value != null ? Long.parseLong(value) : System.currentTimeMillis();

        // setup output header
        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        outputStateEngine.addHeaderTags(inputStateEngine.getHeaderTags());
        outputStateEngine.addHeaderTag(PUBLISH_CYCLE_DATATS_HEADER, String.valueOf(publishCycleDataTS));

        // perform transformation
        SimpleTransformerContext context = new SimpleTransformerContext();
        if (isUseFastLane) {
            context.setFastlaneIds(new HashSet<>(Arrays.asList(80115503, 70143860)));
        }
        SimpleTransformer transformer = new SimpleTransformer(api, outputStateEngine, context);
        transformer.setPublishCycleDataTS(publishCycleDataTS);
        transformer.transform();
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        HollowReadStateEngine expectedOutputStateEngine = loadStateEngine(CONTROL_FN, getDiffFilter(actualOutputReadStateEngine.getSchemas()));

        // diff
        ShowMeTheProgressDiffTool.startTheDiff(expectedOutputStateEngine, actualOutputReadStateEngine);
    }


    public static HollowFilterConfig getDiffFilter(Collection<HollowSchema> outputSchemas) {
        HollowFilterConfig filter = new HollowFilterConfig();
        filter.addFieldRecursive("CompleteVideo", "id", outputSchemas);
        filter.addFieldRecursive("CompleteVideo", "country", outputSchemas);
        filter.addField("CompleteVideo", "facetData");
        filter.addFieldRecursive("CompleteVideoFacetData", "videoCollectionsData", outputSchemas);
        filter.addFieldRecursive("CompleteVideoFacetData", "videoMetaData", outputSchemas);
        filter.addFieldRecursive("CompleteVideoFacetData", "videoMediaData", outputSchemas);
        filter.addFieldRecursive("CompleteVideoFacetData", "videoMiscData", outputSchemas);
        filter.addFieldRecursive("CompleteVideoFacetData", "videoImages", outputSchemas);
        filter.addFieldRecursive("CompleteVideo", "countrySpecificData", outputSchemas);

        filter.addTypeRecursive("PersonImages", outputSchemas);
        filter.addTypeRecursive("CharacterImages", outputSchemas);
        filter.addTypeRecursive("FallbackUSArtwork", outputSchemas);

        filter.addTypeRecursive("PackageData", outputSchemas);
        filter.addTypeRecursive("GlobalVideo", outputSchemas);
        filter.addTypeRecursive("NamedCollectionHolder", outputSchemas);
        filter.addTypeRecursive("LanguageRights", outputSchemas);
        filter.addTypeRecursive("L10NResources", outputSchemas);

        filter.addTypeRecursive("DrmSystem", outputSchemas);
        filter.addTypeRecursive("OriginServer", outputSchemas);
        filter.addTypeRecursive("ArtWorkImageFormatEntry", outputSchemas);
        filter.addTypeRecursive("EncodingProfile", outputSchemas);
        filter.addTypeRecursive("ArtWorkImageFormatEntry", outputSchemas);
        filter.addTypeRecursive("ArtWorkImageTypeEntry", outputSchemas);
        filter.addTypeRecursive("ArtWorkImageRecipe", outputSchemas);
        filter.addTypeRecursive("DefaultExtensionRecipe", outputSchemas);
        filter.addTypeRecursive("DeploymentIntent", outputSchemas);
        filter.addTypeRecursive("TopNVideoData", outputSchemas);
        filter.addTypeRecursive("RolloutCharacter", outputSchemas);
        filter.addTypeRecursive("RolloutVideo", outputSchemas);
        filter.addTypeRecursive("EncodingProfileGroup", outputSchemas);
        filter.addTypeRecursive("GlobalPerson", outputSchemas);
        filter.addTypeRecursive("VideoEpisode_CountryList", outputSchemas);
        filter.addTypeRecursive("DrmInfoData", outputSchemas);
        filter.addTypeRecursive("FileEncodingData", outputSchemas);
        filter.addTypeRecursive("LanguageRights", outputSchemas);
        return filter;
    }


    private HollowReadStateEngine loadStateEngine(String resourceFilename) throws IOException {
        return loadStateEngine(resourceFilename, new HollowFilterConfig(true));
    }

    private HollowReadStateEngine loadStateEngine(String resourceFilename, HollowFilterConfig filter) throws IOException {
        FileInputStream fio = new FileInputStream(new File(ROOT_DATA_DIR, resourceFilename));
        InputStream is = isUseFastLane ? new LZ4BlockInputStream(fio) : new BufferedInputStream(fio);
        return loadStateEngine(is, filter);
    }

    private Map<String, String> getHeaderTagsFromStateEngine(String resourceFilename) throws IOException {
        FileInputStream fio = new FileInputStream(new File(ROOT_DATA_DIR, resourceFilename));
        InputStream is = isUseFastLane ? new LZ4VMSInputStream(fio) : new BufferedInputStream(fio);

        HollowBlobHeaderReader reader = new HollowBlobHeaderReader();
        HollowBlobHeader header = reader.readHeader(is);
        return header.getHeaderTags();
    }

    private HollowReadStateEngine loadStateEngine(InputStream is, HollowFilterConfig filter) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(is, filter);

        return stateEngine;
    }

    private HollowReadStateEngine roundTripOutputStateEngine(HollowWriteStateEngine stateEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        HollowReadStateEngine actualOutputStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(actualOutputStateEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));
        return actualOutputStateEngine;
    }

}
