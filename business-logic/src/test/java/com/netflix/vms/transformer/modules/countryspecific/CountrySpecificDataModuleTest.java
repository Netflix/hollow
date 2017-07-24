package com.netflix.vms.transformer.modules.countryspecific;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowoutput.SchedulePhaseInfo;
import com.netflix.vms.transformer.hollowoutput.VideoImages;

public class CountrySpecificDataModuleTest {
	
    private static final int videoWithNoImages = 701734242;
	private static final int videoWithLaunchEarliestWindow = 70178888;
	private static final int videoWithMinus60EarliestWindow = 70178217;
	private static final int videoWith2WindowsBeforeLaunch = 62178888;
	private static final int videoWithAbsoluteTagAndOffsets = 84574444;
	private static final int videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest = 76574684;
	private static final int videoWithEarliestWindowFromAnotherSourceVideo = 6333333;
	
	private static final long Minus60DaysInMilliS = -60*24*60*60*1000l;
	private static final long Minus90DaysInMilliS = -90*24*60*60*1000l;
	private static final Long availabilityDate = new Long(1487189563099l); //02/15/2017
	private static final Long october15th2016 = new Long(1476514800000l); //10/15/2016
	private static final Long january5th2017 = new Long(1483603200000l); //1/5/2017

	private CountrySpecificDataModule dataModule;
	private HashMap<Integer, VideoImages> videoImagesByVideoMap = new HashMap<Integer, VideoImages>();

    @Before
    public void setUp() {
        TransformerContext ctx = Mockito.mock(TransformerContext.class);
        TransformerConfig mockConfig = Mockito.mock(TransformerConfig.class);
        Mockito.when(ctx.getConfig()).thenReturn(mockConfig);
        Mockito.when(mockConfig.isUseSchedulePhasesInAvailabilityDateCalc()).thenReturn(true);
        dataModule = new CountrySpecificDataModule(ctx);

        VideoImages imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWithMinus60EarliestWindow, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWithMinus60EarliestWindow, 0l, Minus60DaysInMilliS);

        imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWithLaunchEarliestWindow, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWithLaunchEarliestWindow, 0l);

        imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWith2WindowsBeforeLaunch, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWith2WindowsBeforeLaunch, Minus60DaysInMilliS,
                Minus90DaysInMilliS);

        imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWithEarliestWindowFromAnotherSourceVideo, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWithEarliestWindowFromAnotherSourceVideo, Minus60DaysInMilliS);
        imagesForvideo.imageAvailabilityWindows.addAll(getSchedulePhaseInfo(70178217, Minus90DaysInMilliS));
        
        imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWithAbsoluteTagAndOffsets, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWithAbsoluteTagAndOffsets, 0l, Minus60DaysInMilliS);
        imagesForvideo.imageAvailabilityWindows.addAll(getSchedulePhaseInfo(videoWithAbsoluteTagAndOffsets, true, october15th2016));
        
        imagesForvideo = new VideoImages();
        videoImagesByVideoMap.put(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest, imagesForvideo);
        imagesForvideo.imageAvailabilityWindows = getSchedulePhaseInfo(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest, 0l, Minus90DaysInMilliS);
        imagesForvideo.imageAvailabilityWindows.addAll(getSchedulePhaseInfo(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest, true, january5th2017));

    }

    private Set<SchedulePhaseInfo> getSchedulePhaseInfo(int videoId, boolean isAbsolute, long ...start ){
        Set<SchedulePhaseInfo> result = new HashSet<>();
        for(long offset: start){
                SchedulePhaseInfo phaseInfo = new SchedulePhaseInfo(videoId);
                phaseInfo.start = offset;
                phaseInfo.isAbsolute = isAbsolute;
                result.add(phaseInfo);
        }
        return result;
    }
	private Set<SchedulePhaseInfo> getSchedulePhaseInfo(int videoId, long ...start ){
	    return getSchedulePhaseInfo(videoId, false, start);
	}
    
    @Test
    public void testGetEarliestSchedulePhaseOffsetWithOffsetsOnly() {
	    VideoImages images = videoImagesByVideoMap.get(videoWithMinus60EarliestWindow);
        Long earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithMinus60EarliestWindow, images,
                availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals((Long)(availabilityDate+Minus60DaysInMilliS), earliestSchedulePhaseOffset);

        images = videoImagesByVideoMap.get(videoWithNoImages);
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithNoImages, images, availabilityDate, new CountrySpecificRollupValues());
        Assert.assertNull(earliestSchedulePhaseOffset);

        images = videoImagesByVideoMap.get(videoWithLaunchEarliestWindow);
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithLaunchEarliestWindow, images,
                availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals((Long)(availabilityDate+0l), earliestSchedulePhaseOffset);

        images = videoImagesByVideoMap.get(videoWith2WindowsBeforeLaunch);
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWith2WindowsBeforeLaunch, images,
                availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals((Long)(availabilityDate+Minus90DaysInMilliS), earliestSchedulePhaseOffset);

        // Windows with other source video are ignored. So -90 days is earliest window but with different source video id. This will be ignored
        // -60 days window will be returned instead.
        images = videoImagesByVideoMap.get(videoWithEarliestWindowFromAnotherSourceVideo);
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithEarliestWindowFromAnotherSourceVideo,
                images, availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals((Long)(availabilityDate+Minus60DaysInMilliS), earliestSchedulePhaseOffset);
    }
    
    @Test
    public void testGetEarliestSchedulePhaseOffsetWithFixedDatesAndOffsets() {
        // PR Date is earliest (15th Oct 2016) than offsets. PR Date wins
        VideoImages images = videoImagesByVideoMap.get(videoWithAbsoluteTagAndOffsets);
        Long earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithAbsoluteTagAndOffsets, images,
                availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals(october15th2016, earliestSchedulePhaseOffset);

        images = videoImagesByVideoMap.get(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest);
        // Earliest offset is earlier than PR Date (5th Jan 2017) and hence offset wins.
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest, images,
                availabilityDate, new CountrySpecificRollupValues());
        Assert.assertEquals((Long)(availabilityDate+Minus90DaysInMilliS), earliestSchedulePhaseOffset);

        images = videoImagesByVideoMap.get(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest);
        // Null availability date, phase offsets will be ignored. Hence PR Date (5th Jan 2016) wins
        earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseDate(videoWithAbsoluteTagAndOffsetsWherOffsetIsEarliest, images,
                null, new CountrySpecificRollupValues());
        Assert.assertEquals(january5th2017, earliestSchedulePhaseOffset);
    }
    
}
