package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.DateWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateWindowAggregatorTest {

    DateWindowAggregator aggregator;
    
    @Before
    public void setUp() {
        aggregator = new DateWindowAggregator();
        aggregator.addDateWindow(10, 20);
        aggregator.addDateWindow(15, 30);
        aggregator.addDateWindow(31, 40);
        
        aggregator.addDateWindow(150, 300);
        aggregator.addDateWindow(100, 200);
        aggregator.addDateWindow(90, 290);
        
        aggregator.addDateWindow(1000, 2000);
        aggregator.addDateWindow(1500, 3000);
        aggregator.addDateWindow(2900, 3100);
        
        
        aggregator.addDateWindow(10000, 20000);
        aggregator.addDateWindow(11000, 20000);
        aggregator.addDateWindow(12000, 20000);
        aggregator.addDateWindow(13000, 20000);
        aggregator.addDateWindow(14000, 20000);
        aggregator.addDateWindow(15000, 20000);
        
        aggregator.mergeDateWindows();
    }
    
    @Test
    public void exactWindowsAreMatched() {
        DateWindow window = aggregator.matchDateWindowAgainstMergedDateWindows(10, 40);
        assertDateWindow(window, 10, 40);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(90, 300);
        assertDateWindow(window, 90, 300);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(1000, 3100);
        assertDateWindow(window, 1000, 3100);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(10000, 20000);
        assertDateWindow(window, 10000, 20000);
        
    }
    
    @Test
    public void windowsAreShrunkToActualAvailability() {
        DateWindow window = aggregator.matchDateWindowAgainstMergedDateWindows(0, 50);
        assertDateWindow(window, 10, 40);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(50, 350);
        assertDateWindow(window, 90, 300);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(900, 3200);
        assertDateWindow(window, 1000, 3100);
        
        window = aggregator.matchDateWindowAgainstMergedDateWindows(9000, 25000);
        assertDateWindow(window, 10000, 20000);
    }
    
    @Test
    public void overlapTest() {
        DateWindow window = aggregator.matchDateWindowAgainstMergedDateWindows(25, 50);
        assertDateWindow(window, 25, 40);

        window = aggregator.matchDateWindowAgainstMergedDateWindows(200, 500);
        assertDateWindow(window, 200, 300);

        window = aggregator.matchDateWindowAgainstMergedDateWindows(900, 1200);
        assertDateWindow(window, 1000, 1200);
    }
    
    
    private void assertDateWindow(DateWindow window, long startDate, long endDate) {
        Assert.assertEquals(startDate, window.startDateTimestamp);
        Assert.assertEquals(endDate, window.endDateTimestamp);
    }

}
