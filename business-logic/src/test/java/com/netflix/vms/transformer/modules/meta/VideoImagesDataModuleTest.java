package com.netflix.vms.transformer.modules.meta;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
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
import com.netflix.vms.transformer.hollowoutput.SchedulePhaseInfo;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 */
public class VideoImagesDataModuleTest {

    private VMSHollowInputAPI api;

    private HollowHashIndex overrideIndex;
    private HollowHashIndex absoluteIndex;
    private HollowHashIndex masterIndex;

    private VideoArtworkHollow videoArtworkHollow;
    private PhaseTagListTypeAPI listTypeAPI;
    private PhaseTagListHollow listHollow;
    private HollowOrdinalIterator listIterator;

    private int videoId;
    private String scheduleId;
    private String promoTag;
    private String prePromoTag;
    private String sourceFileId;

    private TransformerContext context;
    private TransformerConfig config;
    private TaggingLogger logger;

    private VideoImagesDataModule videoImagesDataModule;

    @Before
    public void setup() {
        api = mock(VMSHollowInputAPI.class);
        this.overrideIndex = mock(HollowHashIndex.class);
        this.absoluteIndex = mock(HollowHashIndex.class);
        this.masterIndex = mock(HollowHashIndex.class);

        this.videoArtworkHollow = mock(VideoArtworkHollow.class);

        this.videoId = 1;
        this.scheduleId = "testSchedule";
        this.prePromoTag = "PRE_PROMO";
        this.promoTag = "PROMO";
        this.sourceFileId = "testSourceFileId";

        this.context = mock(TransformerContext.class);
        this.config = mock(TransformerConfig.class);
        this.logger = mock(TaggingLogger.class);

        when(context.getConfig()).thenReturn(config);
        when(context.getLogger()).thenReturn(logger);

        this.videoImagesDataModule = new VideoImagesDataModule(context, overrideIndex, masterIndex, absoluteIndex,
                api, mock(HollowObjectMapper.class), mock(CycleConstants.class),
                mock(VMSTransformerIndexer.class));

        //mock phaseTagList from VideoArtwork
        HollowListDelegate hollowListDelegate = mock(HollowListDelegate.class);
        listHollow = new PhaseTagListHollow(hollowListDelegate, 1);
        listTypeAPI = mock(PhaseTagListTypeAPI.class);
        listIterator = mock(HollowOrdinalIterator.class);

        when(listTypeAPI.getOrdinalIterator(1)).thenReturn(listIterator);
        when(hollowListDelegate.getTypeAPI()).thenReturn(listTypeAPI);
        when(listTypeAPI.getAPI()).thenReturn(api);

        //mock videoArtwork
        when(videoArtworkHollow._getPhaseTags()).thenReturn(listHollow);
        when(videoArtworkHollow._getIsSmoky()).thenReturn(false);
        StringHollow stringHollow = mock(StringHollow.class);
        when(videoArtworkHollow._getSourceFileId()).thenReturn(stringHollow);
        when(stringHollow._getValue()).thenReturn("testSourceFileId");

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

    private void mockListIterator(HollowOrdinalIterator listIterator, int validResults) {
        when(listIterator.next()).thenAnswer(new Answer<Integer>() {
            private int count = 0;

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                if (count < validResults) {
                    count++;
                    return count;
                }
                return HollowOrdinalIterator.NO_MORE_ORDINALS;
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
    }

    /**
     * This test checks, if the phaseTagList is null in videoArtwork, then schedulePhaseInfo is null
     */
    @Test
    public void testGetScheduleInfoNullPhaseTagList() {
        when(videoArtworkHollow._getPhaseTags()).thenReturn(null);
        SchedulePhaseInfo info = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertNull(info);
    }

    /**
     * This test checks, if the phaseTagList is an empty list, then default schedulePhaseInfo
     */
    @Test
    public void testGetScheduleInfoEmptyList() {
        mockListIterator(listIterator, 0);
        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        verifySchedulePhaseInfo(schedulePhaseInfo, 0L, Long.MIN_VALUE, false, false);
    }

    /**
     * This test checks, if the tag is not present in any schedule then null value is returned for schedulePhaseInfo.
     */
    @Test
    public void testGetSchedulePhaseInfoNoSchedule() {
        mockListIterator(listIterator, 2);

        // mock phaseTag returned from videoArtwork
        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow phaseTagHollow1 = getPhaseTag(prePromoTag, scheduleId);
        when(api.getPhaseTagHollow(eq(1))).thenReturn(phaseTagHollow);
        when(api.getPhaseTagHollow(eq(2))).thenReturn(phaseTagHollow1);

        // tag does not belong to any of the schedule
        when(masterIndex.findMatches(anyString(), anyString())).thenReturn(null);
        when(overrideIndex.findMatches(anyLong(), anyString())).thenReturn(null);
        when(absoluteIndex.findMatches(anyLong(), anyString())).thenReturn(null);

        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        Assert.assertNull(schedulePhaseInfo);
    }

    /**
     * This test checks earliest schedulePhaseInfo from master schedule.
     */
    @Test
    public void testGetSchedulePhaseInfoMasterSchedule() {

        mockListIterator(listIterator, 2);

        // mock phaseTag returned from videoArtwork
        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow phaseTagHollow1 = getPhaseTag(prePromoTag, scheduleId);
        when(api.getPhaseTagHollow(eq(1))).thenReturn(phaseTagHollow);
        when(api.getPhaseTagHollow(eq(2))).thenReturn(phaseTagHollow1);

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

        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        verifySchedulePhaseInfo(schedulePhaseInfo, -200L, Long.MIN_VALUE, false, false);
    }

    /**
     * This test checks, if absolute schedule is present then the schedulePhaseInfo is returned using that schedule.
     */
    @Test
    public void testGetSchedulePhaseInfoAbsoluteSchedule() {

        mockListIterator(listIterator, 2);

        PhaseTagHollow phaseTagHollow = getPhaseTag(promoTag, scheduleId);
        when(api.getPhaseTagHollow(1)).thenReturn(phaseTagHollow);

        // mock override and master index
        when(masterIndex.findMatches(eq(promoTag), eq(scheduleId))).thenReturn(null);
        when(overrideIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(null);

        //mock absolute index
        HollowHashIndexResult result = getHollowHashIndexResult(1);
        when(absoluteIndex.findMatches(eq((long) videoId), eq(promoTag))).thenReturn(result);

        //mock absolute schedule
        AbsoluteScheduleHollow absoluteScheduleHollow = getAbsoluteScheduleHollow(-10L, 200L);
        when(api.getAbsoluteScheduleHollow(eq(1))).thenReturn(absoluteScheduleHollow);

        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        verifySchedulePhaseInfo(schedulePhaseInfo, -10, 200L, true, false);
    }

    /**
     * This verifies the functionality where a master schedule and override schedule is present and earliest schedule is returned.
     */
    @Test
    public void testGetSchedulePhaseInfoMasterAndOverrideSchedule() {

        mockListIterator(listIterator, 2);

        PhaseTagHollow promoPhaseTagHollow = getPhaseTag(promoTag, scheduleId);
        PhaseTagHollow prePromoPhaseTagHollow = getPhaseTag(prePromoTag, scheduleId);
        when(api.getPhaseTagHollow(eq(1))).thenReturn(promoPhaseTagHollow);
        when(api.getPhaseTagHollow(eq(2))).thenReturn(prePromoPhaseTagHollow);

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

        SchedulePhaseInfo schedulePhaseInfo = videoImagesDataModule.getScheduleInfo(videoArtworkHollow, videoId);
        verifySchedulePhaseInfo(schedulePhaseInfo, -2L, Long.MIN_VALUE, false, false);
    }
}
