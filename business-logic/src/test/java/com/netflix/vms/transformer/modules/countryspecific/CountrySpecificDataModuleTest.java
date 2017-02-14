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
	private static final int videoWithEarliestWindowFromAnotherSourceVideo = 6333333;
	
	private static final long Minus60DaysInMilliS = -60*24*60*60*1000l;
	private static final long Minus90DaysInMilliS = -90*24*60*60*1000l;

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

    }

	private Set<SchedulePhaseInfo> getSchedulePhaseInfo(int videoId, long ...start ){
		Set<SchedulePhaseInfo> result = new HashSet<>();
		for(long offset: start){
			SchedulePhaseInfo phaseInfo = new SchedulePhaseInfo(videoId);
			phaseInfo.start = offset;
			result.add(phaseInfo);
		}
		return result;
	}
    
    @Test
	public void testGetEarliestSchedulePhaseOffset(){
		Long earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseOffset(videoWithMinus60EarliestWindow, videoImagesByVideoMap);
		Assert.assertEquals((Long)Minus60DaysInMilliS, earliestSchedulePhaseOffset);
		
		earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseOffset(videoWithNoImages, videoImagesByVideoMap);
		Assert.assertNull(earliestSchedulePhaseOffset);
		
		earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseOffset(videoWithLaunchEarliestWindow, videoImagesByVideoMap);
		Assert.assertEquals((Long)0l, earliestSchedulePhaseOffset);
		
		earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseOffset(videoWith2WindowsBeforeLaunch, videoImagesByVideoMap);
		Assert.assertEquals((Long)Minus90DaysInMilliS, earliestSchedulePhaseOffset);
		
		// Windows with other source video are ignored. So -90 days is earliest window but with different source video id. This will be ignored
		// -60 days window will be returned instead.
		earliestSchedulePhaseOffset = dataModule.getEarliestSchedulePhaseOffset(videoWithEarliestWindowFromAnotherSourceVideo, videoImagesByVideoMap);
		Assert.assertEquals((Long)Minus60DaysInMilliS, earliestSchedulePhaseOffset);
	}
}
