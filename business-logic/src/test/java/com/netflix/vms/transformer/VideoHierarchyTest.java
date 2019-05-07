package com.netflix.vms.transformer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.type.NFCountry;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.converterpojos.Episode;
import com.netflix.vms.transformer.converterpojos.Flags;
import com.netflix.vms.transformer.converterpojos.IndividualSupplemental;
import com.netflix.vms.transformer.converterpojos.Season;
import com.netflix.vms.transformer.converterpojos.ShowSeasonEpisode;
import com.netflix.vms.transformer.converterpojos.Status;
import com.netflix.vms.transformer.converterpojos.Supplementals;
import com.netflix.vms.transformer.converterpojos.VideoGeneral;
import com.netflix.vms.transformer.converterpojos.VideoType;
import com.netflix.vms.transformer.converterpojos.VideoTypeDescriptor;
import com.netflix.vms.transformer.gatekeeper2migration.GatekeeperStatusRetriever;
import com.netflix.vms.transformer.helper.HollowReadStateEngineBuilder;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class VideoHierarchyTest {
    private static int ID = 1;
    private static final int SHOW = ID++;
    private static final int S1 = ID++;
    private static final int S2 = ID++;
    private static final int S1E1 = ID++;
    private static final int S1E2 = ID++;
    private static final int S2E1 = ID++;
    private static final int S2E2 = ID++;
    private static final int S2E3 = ID++;
    private static final int SHOW_SUP = ID++;
    private static final int S1E1SUP1 = ID++;
    private static final int S1E1SUP2 = ID++;
    private static final int S2SUP = ID++;
    private static final List<Integer> ALL_VIDEOS = Arrays.asList(SHOW, S1, S2, S1E1, S1E2, S2E1, S2E2, S2E3,
            SHOW_SUP, S1E1SUP1, S1E1SUP2, S2SUP);

    private TransformerContext mockContext;

    @Before
    public void setUp() {
        mockContext = mock(TransformerContext.class);
        when(mockContext.getConfig()).thenReturn(mock(TransformerConfig.class));
    }

    @Test
    public void testExcludedFromVideoGeneral_episode() {
        VideoHierarchy hierarchy = getVideoHierarchy(createReadStateEngine(S1E1));
        assertArrayEquals("Should have all seasons", new int[]{S1, S2}, hierarchy.getSeasonIds());
        assertArrayEquals("Season 1 should have one episode", new int[]{S1E2},
                hierarchy.getEpisodeIds()[0]);
        assertArrayEquals("Season 2 should have three episodes", new int[]{S2E1, S2E2, S2E3},
                hierarchy.getEpisodeIds()[1]);
        assertArrayEquals("Supplementals should be missing S1E1 supplementals",
                new int[]{SHOW_SUP, S2SUP}, hierarchy.getSupplementalIds());
        assertEquals("Should have dropped S1E1 and supplementals",
                new HashSet<>(Arrays.asList(S1E1, S1E1SUP1, S1E1SUP2)), hierarchy.getDroppedIds());
    }
    @Test
    public void testExcludedFromVideoGeneral_season() {
        VideoHierarchy hierarchy = getVideoHierarchy(createReadStateEngine(S2));
        assertArrayEquals("Should have Season 1", new int[]{S1}, hierarchy.getSeasonIds());
        assertArrayEquals("Season 1 should have two episodes", new int[]{S1E1, S1E2},
                hierarchy.getEpisodeIds()[0]);
        assertArrayEquals("Supplementals should be missing S2SUP",
                new int[]{SHOW_SUP, S1E1SUP1, S1E1SUP2}, hierarchy.getSupplementalIds());
        assertEquals("Should have dropped S2 and supplementals",
                new HashSet<>(Arrays.asList(S2, S2E1, S2E2, S2E3, S2SUP)), hierarchy.getDroppedIds());
    }

    @Test
    public void testExcludedFromVideoGeneral_supplemental() {
        VideoHierarchy hierarchy = getVideoHierarchy(createReadStateEngine(S1E1SUP1));
        assertArrayEquals("Should have all seasons", new int[]{S1, S2}, hierarchy.getSeasonIds());
        assertArrayEquals("Season 1 should have two episodes", new int[]{S1E1, S1E2},
                hierarchy.getEpisodeIds()[0]);
        assertArrayEquals("Season 2 should have three episodes", new int[]{S2E1, S2E2, S2E3},
                hierarchy.getEpisodeIds()[1]);
        assertArrayEquals("Supplementals should be missing S1E1SUP1",
                new int[]{SHOW_SUP, S1E1SUP2, S2SUP}, hierarchy.getSupplementalIds());
    }

    private HollowReadStateEngine createReadStateEngine(int idExcludedFromVideoGeneral) {
        HollowReadStateEngineBuilder readStateEngineCreator = new HollowReadStateEngineBuilder();
        // add all VideoGenerals except idExcludedFromVideoGeneral
        ALL_VIDEOS.stream().forEach(id -> {
            if (!id.equals(idExcludedFromVideoGeneral))
                readStateEngineCreator.add(new VideoGeneral(id));
        });
        // add hierarchy
        readStateEngineCreator.add(new ShowSeasonEpisode(SHOW, -1).setMerchOrder("")
                .addToSeasons(new Season().setMovieId(S1).setMerchOrder("")
                        .addToEpisodes(new Episode().setMovieId(S1E1))
                        .addToEpisodes(new Episode().setMovieId(S1E2)))
                .addToSeasons(new Season().setMovieId(S2).setMerchOrder("")
                        .addToEpisodes(new Episode().setMovieId(S2E1))
                        .addToEpisodes(new Episode().setMovieId(S2E2))
                        .addToEpisodes(new Episode().setMovieId(S2E3))))
                // add all supplementals
                .add(new Supplementals(SHOW)
                        .addToSupplementals(new IndividualSupplemental().setMovieId(SHOW_SUP)))
                .add(new Supplementals(S1E1)
                        .addToSupplementals(new IndividualSupplemental().setMovieId(S1E1SUP1))
                        .addToSupplementals(new IndividualSupplemental().setMovieId(S1E1SUP2)))
                .add(new Supplementals(S2)
                        .addToSupplementals(new IndividualSupplemental().setMovieId(S2SUP)));
        // make each video valid in the US
        ALL_VIDEOS.forEach(videoId ->
                readStateEngineCreator.add(new VideoType(videoId)
                        .addToCountryInfos(new VideoTypeDescriptor().setCountryCode(NFCountry.US.getId())))
                        .add(new Status(videoId, NFCountry.US.getId())
                                .setFlags(new Flags().setGoLive(true))));
        return readStateEngineCreator.build();
    }

    private VideoHierarchy getVideoHierarchy(HollowReadStateEngine readStateEngine) {
        VMSHollowInputAPI api = new VMSHollowInputAPI(readStateEngine);
        VMSTransformerIndexer indexer = new VMSTransformerIndexer(readStateEngine, mockContext);
        GatekeeperStatusRetriever statusRetriever = new GatekeeperStatusRetriever(api, indexer);
        VideoHierarchyInitializer initializer = new VideoHierarchyInitializer(api, indexer, statusRetriever, mockContext);
        int ordinal = indexer.getHashIndex(IndexSpec.SHOW_SEASON_EPISODE).findMatches(
                Long.valueOf(SHOW)).iterator().next();
        ShowSeasonEpisodeHollow showSeasonEpisode = api.getShowSeasonEpisodeHollow(ordinal);
        return new VideoHierarchy(mockContext, SHOW, false,
                showSeasonEpisode, NFCountry.US.getId(), initializer);
    }
}
