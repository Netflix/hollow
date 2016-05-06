package com.netflix.vms.transformer;

import com.netflix.hollow.HollowSchema;
import com.netflix.hollow.filter.HollowFilterConfig;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.util.memory.WastefulRecycler;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.junit.Test;

public class ShowMeTheProgress {

    @Test
    public void start() throws Exception {
        VMSHollowInputAPI api = new VMSHollowInputAPI(loadStateEngine("/filtered-input.hollow"));

        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();

        SimpleTransformer transformer = new SimpleTransformer(api, outputStateEngine);

        transformer.transform();
        HollowReadStateEngine actualOutputReadStateEngine = roundTripOutputStateEngine(outputStateEngine);
        HollowReadStateEngine expectedOutputStateEngine = loadStateEngine("/expected-output.hollow", getDiffFilter(actualOutputReadStateEngine.getSchemas()));

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
        filter.addTypeRecursive("PersonImages", outputSchemas);
        filter.addTypeRecursive("CharacterImages", outputSchemas);
        filter.addTypeRecursive("DrmInfoData", outputSchemas);
        filter.addTypeRecursive("FileEncodingData", outputSchemas);
        return filter;
    }


    private HollowReadStateEngine loadStateEngine(String resourceFilename) throws IOException {
        return loadStateEngine(resourceFilename, new HollowFilterConfig(true));
    }

    private HollowReadStateEngine loadStateEngine(String resourceFilename, HollowFilterConfig filter) throws IOException {
        return loadStateEngine(new BufferedInputStream(this.getClass().getResourceAsStream(resourceFilename)), filter);
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
