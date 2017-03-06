package com.netflix.vms.transformer.modules.meta;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowinput.AbsoluteScheduleHollow;
import com.netflix.vms.transformer.hollowinput.MasterScheduleHollow;
import com.netflix.vms.transformer.hollowinput.OverrideScheduleHollow;
import com.netflix.vms.transformer.hollowinput.PhaseTagHollow;
import com.netflix.vms.transformer.hollowinput.PhaseTagListHollow;
import com.netflix.vms.transformer.hollowinput.PhaseTagListTypeAPI;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoArtworkHollow;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.ArtworkBasicPassthrough;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivatives;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourceString;
import com.netflix.vms.transformer.hollowoutput.SchedulePhaseInfo;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test for VideoImagesDataModule. Unit tests for asset schedules.
 */
public class VideoImagesDataModuleTest {

    private VMSHollowInputAPI api;

    private HollowHashIndex overrideIndex;
    private HollowHashIndex absoluteIndex;
    private HollowHashIndex masterIndex;

    private VideoArtworkHollow videoArtworkHollow;
    private PhaseTagListTypeAPI listTypeAPI;
    private PhaseTagListHollow listHollow;
    private Iterator<PhaseTagHollow> iterator;
    private HollowOrdinalIterator listIterator;

    private int videoId;
    private String scheduleId;
    private String promoTag;
    private String prePromoTag;

    private TransformerContext context;
    private TransformerConfig config;
    private TaggingLogger logger;

    private VideoImagesDataModule videoImagesDataModule;

    @Before
    public void setup() {
        // mock stuff
        api = mock(VMSHollowInputAPI.class);
        overrideIndex = mock(HollowHashIndex.class);
        absoluteIndex = mock(HollowHashIndex.class);
        masterIndex = mock(HollowHashIndex.class);

        videoId = 1;
        scheduleId = "testSchedule";
        prePromoTag = "PRE_PROMO";
        promoTag = "PROMO";

        context = mock(TransformerContext.class);
        config = mock(TransformerConfig.class);
        logger = mock(TaggingLogger.class);

        videoArtworkHollow = mock(VideoArtworkHollow.class);
        listHollow = mock(PhaseTagListHollow.class);
        listTypeAPI = mock(PhaseTagListTypeAPI.class);
        listIterator = mock(HollowOrdinalIterator.class);
        iterator = mock(Iterator.class);

        //mock calls
        when(context.getConfig()).thenReturn(config);
        when(context.getLogger()).thenReturn(logger);
        when(config.isRollupImagesForArtworkScheduling()).thenReturn(true);
        when(config.isFilterImagesForArtworkScheduling()).thenReturn(true);
        when(config.isUseSchedulePhasesInAvailabilityDateCalc()).thenReturn(true);

        when(listTypeAPI.getOrdinalIterator(1)).thenReturn(listIterator);
        when(listHollow.iterator()).thenReturn(iterator);

        //mock videoArtwork
        when(videoArtworkHollow._getPhaseTags()).thenReturn(listHollow);
        when(videoArtworkHollow._getIsSmoky()).thenReturn(false);
        StringHollow stringHollow = mock(StringHollow.class);
        when(videoArtworkHollow._getSourceFileId()).thenReturn(stringHollow);
        when(stringHollow._getValue()).thenReturn("testSourceFileId");

        // create instance to test
        this.videoImagesDataModule = new VideoImagesDataModule(context, overrideIndex, masterIndex, absoluteIndex,
                api, mock(HollowObjectMapper.class), mock(CycleConstants.class),
                mock(VMSTransformerIndexer.class));

    }

    private PhaseTagHollow getPhaseTag(String tag, String scheduleId) {
        PhaseTagHollow phaseTagHollow = mock(PhaseTagHollow.class);
        StringHollow tagStringHollow = mock(StringHollow.class);
        when(tagStringHollow._getValue()).thenReturn(tag);
        StringHollow scheduleStringHollow = mock(StringHollow.class);
        when(scheduleStringHollow._getValue()).thenReturn(scheduleId);
        when(phaseTagHollow._getPhaseTag()).thenReturn(tagStringHollow);
        when(phaseTagHollow._getScheduleId()).thenReturn(scheduleStringHollow);
        return phaseTagHollow;
    }

    private void mockIterator(Iterator<PhaseTagHollow> iterator, int validResults, List<PhaseTagHollow> phaseTags) {
        mockIterator(iterator, validResults, phaseTags, false);
    }

    private void mockIterator(Iterator<PhaseTagHollow> iterator, int validResults, List<PhaseTagHollow> phaseTags, boolean emptyList) {
        when(iterator.next()).thenAnswer(new Answer<PhaseTagHollow>() {
            private int count = 0;

            @Override
            public PhaseTagHollow answer(InvocationOnMock invocation) throws Throwable {
                if (count < validResults) {
                    PhaseTagHollow phaseTagHollow = phaseTags.get(count);
                    count++;
                    return phaseTagHollow;
                }
                return null;
            }
        });
        when(iterator.hasNext()).thenAnswer(new Answer<Boolean>() {
            private int count = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                // hasNext is called once before the while loop to check it iterator has items to iterate
                if (emptyList) return false;
                if (count <= validResults) {
                    count++;
                    return true;
                }
                return false;
            }
        });
    }

    private HollowHashIndexResult getHollowHashIndexResult(int ordinal) {
        HollowHashIndexResult result = mock(HollowHashIndexResult.class);
        HollowOrdinalIterator resultIterator = mock(HollowOrdinalIterator.class);
        when(result.numResults()).thenReturn(1);
        when(resultIterator.next()).thenReturn(ordinal);
        when(result.iterator()).thenReturn(resultIterator);
        return result;
    }

    private MasterScheduleHollow getMasterScheduleHollow(long offset) {
        MasterScheduleHollow masterScheduleHollow = mock(MasterScheduleHollow.class);
        when(masterScheduleHollow._getAvailabilityOffset()).thenReturn(offset);
        return masterScheduleHollow;
    }

    private AbsoluteScheduleHollow getAbsoluteScheduleHollow(long startOffset, long endOffset) {
        AbsoluteScheduleHollow absoluteScheduleHollow = mock(AbsoluteScheduleHollow.class);
        when(absoluteScheduleHollow._getStartDate()).thenReturn(startOffset);
        when(absoluteScheduleHollow._getEndDate()).thenReturn(endOffset);
        return absoluteScheduleHollow;
    }

    private OverrideScheduleHollow getOverrideScheduleHollow(long offset) {
        OverrideScheduleHollow overrideScheduleHollow = mock(OverrideScheduleHollow.class);
        when(overrideScheduleHollow._getAvailabilityOffset()).thenReturn(offset);
        return overrideScheduleHollow;
    }

    private void verifySchedulePhaseInfo(SchedulePhaseInfo schedulePhaseInfo, long start, long end, boolean isAbsolute, boolean isAutomated) {
        Assert.assertNotNull(schedulePhaseInfo);
        Assert.assertEquals(start, schedulePhaseInfo.start);
        Assert.assertEquals(end, schedulePhaseInfo.end);
        Assert.assertEquals(isAbsolute, schedulePhaseInfo.isAbsolute);
        Assert.assertEquals(isAutomated, schedulePhaseInfo.isAutomatedImg);
        Assert.assertEquals(videoId, schedulePhaseInfo.sourceVideoId);
    }

    /**
     * This test checks, if the phaseTagList is null in videoArtwork, then schedulePhaseInfo is null
     */
    @Test
    public void testGetScheduleInfoNullPhaseTagList() {
        when(videoArtworkHollow._getPhaseTags()).thenReturn(null);
        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        SchedulePhaseInfo info = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);
        verifySchedulePhaseInfo(info, 0L, SchedulePhaseInfo.FAR_FUTURE_DATE, false, false);
        Assert.assertTrue(schedulePhaseInfoSet.size() == 1);
    }

    /**
     * This test checks, if the phaseTagList is an empty list, then default schedulePhaseInfo
     */
    @Test
    public void testGetScheduleInfoEmptyList() {
        mockIterator(iterator, 0, Collections.emptyList(), true);
        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);

        verifySchedulePhaseInfo(schedulePhaseInfo, 0L, SchedulePhaseInfo.FAR_FUTURE_DATE, false, false);
        Assert.assertTrue(schedulePhaseInfoSet.size() == 1);
    }

    /**
     * This test checks, if the tag is not present in any schedule then null value is returned for schedulePhaseInfo.
     */
    @Test
    public void testGetSchedulePhaseInfoNoSchedule() {

        // mock phaseTag returned from videoArtwork
        List<PhaseTagHollow> phaseTags = new ArrayList<>();
        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow phaseTagHollow1 = getPhaseTag(prePromoTag, scheduleId);
        phaseTags.add(phaseTagHollow);
        phaseTags.add(phaseTagHollow1);
        mockIterator(iterator, 2, phaseTags);

        // tag does not belong to any of the schedule
        when(masterIndex.findMatches(anyString(), anyString())).thenReturn(null);
        when(overrideIndex.findMatches(anyLong(), anyString())).thenReturn(null);
        when(absoluteIndex.findMatches(anyLong(), anyString())).thenReturn(null);

        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertNull(schedulePhaseInfoSet);
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);
        Assert.assertNull(schedulePhaseInfo);
    }

    /**
     * This test checks earliest schedulePhaseInfo from master schedule.
     */
    @Test
    public void testGetSchedulePhaseInfoMasterSchedule() {

        // mock phaseTag returned from videoArtwork
        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow phaseTagHollow1 = getPhaseTag(prePromoTag, scheduleId);
        List<PhaseTagHollow> phaseTags = new ArrayList<>();
        phaseTags.add(phaseTagHollow);
        phaseTags.add(phaseTagHollow1);
        mockIterator(iterator, 2, phaseTags);


        // mock override and absolute index
        when(absoluteIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(null);
        when(overrideIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(null);

        //mock master index
        HollowHashIndexResult result1 = getHollowHashIndexResult(1);
        HollowHashIndexResult result2 = getHollowHashIndexResult(2);
        when(masterIndex.findMatches(eq(promoTag), eq(scheduleId))).thenReturn(result1);
        when(masterIndex.findMatches(eq(prePromoTag), eq(scheduleId))).thenReturn(result2);

        //mock master schedule
        MasterScheduleHollow masterScheduleHollow1 = getMasterScheduleHollow(-100L);
        MasterScheduleHollow masterScheduleHollow2 = getMasterScheduleHollow(-200L);
        when(api.getMasterScheduleHollow(eq(1))).thenReturn(masterScheduleHollow1);
        when(api.getMasterScheduleHollow(eq(2))).thenReturn(masterScheduleHollow2);

        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertTrue(schedulePhaseInfoSet.size() == 2);// since we hve two phase tags
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);
        verifySchedulePhaseInfo(schedulePhaseInfo, -200L, SchedulePhaseInfo.FAR_FUTURE_DATE, false, false);
    }

    /**
     * This test checks, if absolute schedule is present then the schedulePhaseInfo is returned using that schedule.
     */
    @Test
    public void testGetSchedulePhaseInfoAbsoluteSchedule() {

        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        List<PhaseTagHollow> phaseTags = new ArrayList<>();
        phaseTags.add(phaseTagHollow);
        mockIterator(iterator, 1, phaseTags);

        // mock override and master index
        when(masterIndex.findMatches(eq(promoTag), eq(scheduleId))).thenReturn(null);
        when(overrideIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(null);

        //mock absolute index
        HollowHashIndexResult result = getHollowHashIndexResult(1);
        when(absoluteIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(result);

        //mock absolute schedule
        AbsoluteScheduleHollow absoluteScheduleHollow = getAbsoluteScheduleHollow(-10L, 200L);
        when(api.getAbsoluteScheduleHollow(eq(1))).thenReturn(absoluteScheduleHollow);

        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertTrue(schedulePhaseInfoSet.size() == 1);
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);

        verifySchedulePhaseInfo(schedulePhaseInfo, -10, 200L, true, false);
    }

    /**
     * This verifies the functionality where a master schedule and override schedule is present and earliest schedule is returned.
     */
    @Test
    public void testGetSchedulePhaseInfoMasterAndOverrideSchedule() {

        PhaseTagHollow promoPhaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow prePromoPhaseTagHollow = getPhaseTag(prePromoTag, scheduleId);
        List<PhaseTagHollow> phaseTags = new ArrayList<>();
        phaseTags.add(promoPhaseTagHollow);
        phaseTags.add(prePromoPhaseTagHollow);
        mockIterator(iterator, 2, phaseTags);

        // mock absolute index
        when(absoluteIndex.findMatches(anyLong(), anyString())).thenReturn(null);

        //mock masterIndex
        HollowHashIndexResult result1 = getHollowHashIndexResult(1);
        when(masterIndex.findMatches(eq(promoTag), eq(scheduleId))).thenReturn(result1);
        when(overrideIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(null);

        //mock absolute index
        HollowHashIndexResult result2 = getHollowHashIndexResult(2);
        when(overrideIndex.findMatches(eq((long) videoId), eq(prePromoTag))).thenReturn(result2);

        //mock masterSchedule
        MasterScheduleHollow masterScheduleHollow = getMasterScheduleHollow(-1L);
        when(api.getMasterScheduleHollow(eq(1))).thenReturn(masterScheduleHollow);

        //mock OverrideSchedule
        OverrideScheduleHollow overrideScheduleHollow = getOverrideScheduleHollow(-2L);
        when(api.getOverrideScheduleHollow(eq(2))).thenReturn(overrideScheduleHollow);

        Set<SchedulePhaseInfo> schedulePhaseInfoSet = videoImagesDataModule.getAllScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertTrue(schedulePhaseInfoSet.size() == 2);
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getEarliestScheduleInfo(schedulePhaseInfoSet, videoId);

        verifySchedulePhaseInfo(schedulePhaseInfo, -2L, SchedulePhaseInfo.FAR_FUTURE_DATE, false, false);
    }
    
    @Test
    public void testPickArtworkBasedOnRolloutInfo(){
    	String sourceFileId = "04f3e8c0-e009-11e6-9a23-0e2def47c5ca";
    	
        Artwork artworkWithRolloutTrue = new Artwork();
        artworkWithRolloutTrue.sourceVideoId = 70178217;
        artworkWithRolloutTrue.hasShowLevelTag = false;

        // Process list of derivatives
        artworkWithRolloutTrue.derivatives = new ArtworkDerivatives();
        artworkWithRolloutTrue.sourceFileId = new ArtworkSourceString(sourceFileId);
        artworkWithRolloutTrue.basic_passthrough = new ArtworkBasicPassthrough();
        artworkWithRolloutTrue.seqNum = 2;
        artworkWithRolloutTrue.ordinalPriority = 3;
        artworkWithRolloutTrue.schedulePhaseInfo = new SchedulePhaseInfo(false, 80151460);
        artworkWithRolloutTrue.isRolloutExclusive = true;
        
        Artwork artworkWithRolloutFalse = artworkWithRolloutTrue.clone();
        artworkWithRolloutFalse.isRolloutExclusive = false;
        
        Set<String> rolloutSourceFileIds = new HashSet<>();
        rolloutSourceFileIds.add("04f3e8c0-e009-11e6-9a23-0e2def47c5ca");
		
        // Input says rollout exclusive true and found a rollout: isRolloutExclusive should be true.
		Artwork result = videoImagesDataModule.pickArtworkBasedOnRolloutInfo(artworkWithRolloutTrue, artworkWithRolloutFalse, rolloutSourceFileIds, sourceFileId);
		Assert.assertTrue(result.isRolloutExclusive);

		// Input says rollout exclusive true and did not found a rollout: image is dropped to prevent leaks. Result null.		
		result = videoImagesDataModule.pickArtworkBasedOnRolloutInfo(artworkWithRolloutTrue, artworkWithRolloutFalse, Collections.emptySet(), sourceFileId);
		Assert.assertTrue(result == null);
		
        // Input says rollout exclusive  false and found a rollout: isRolloutExclusive should be true.
		result = videoImagesDataModule.pickArtworkBasedOnRolloutInfo(artworkWithRolloutFalse, artworkWithRolloutTrue, rolloutSourceFileIds, sourceFileId);
		Assert.assertTrue(result.isRolloutExclusive);
		
        // Input says rollout exclusive false and did not find a rollout: isRolloutExclusive should be false.
		result = videoImagesDataModule.pickArtworkBasedOnRolloutInfo(artworkWithRolloutFalse, artworkWithRolloutTrue,  Collections.emptySet(), sourceFileId);
		Assert.assertFalse(result.isRolloutExclusive);
    }
}
