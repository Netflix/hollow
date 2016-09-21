package com.netflix.vms.transformer.modules.countryspecific;

import static com.netflix.vms.transformer.modules.countryspecific.VMSAvailabilityWindowModule.ONE_THOUSAND_YEARS;

import com.netflix.vms.transformer.hollowoutput.DateWindow;
import com.netflix.vms.transformer.hollowoutput.Integer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CountrySpecificRollupValuesTest {

    CountrySpecificRollupValues rollup;
    
    @Before
    public void setUp() {
        rollup = new CountrySpecificRollupValues();
    }
    
    @Test
    public void dateWindowRollupMergesOnHoldWindows() {
        rollup.newSeasonWindow(1, 100, false, 1);
        rollup.newSeasonWindow(1, 100, false, 2);
        rollup.newSeasonWindow(50, 100, false, 3);
        rollup.newSeasonWindow(75 + ONE_THOUSAND_YEARS, 100 + ONE_THOUSAND_YEARS, true, 4);
        rollup.newSeasonWindow(90 + ONE_THOUSAND_YEARS, 100 + ONE_THOUSAND_YEARS, true, 5);
        
        Map<DateWindow, List<Integer>> map = rollup.getDateWindowWiseSeasonSequenceNumbers();
        
        Assert.assertEquals(4, map.size());
        Assert.assertEquals(intList(1, 2), map.get(window(1, 50, false)));
        Assert.assertEquals(intList(1, 2, 3), map.get(window(50, 100, false)));
        Assert.assertEquals(intList(4), map.get(window(75+ONE_THOUSAND_YEARS, 90+ONE_THOUSAND_YEARS, true)));
        Assert.assertEquals(intList(4, 5), map.get(window(90+ONE_THOUSAND_YEARS, 100+ONE_THOUSAND_YEARS, true)));
    }
    
    private DateWindow window(long startDate, long endDate, boolean onHold) {
        DateWindow window = new DateWindow();
        window.startDateTimestamp = startDate;
        window.endDateTimestamp = endDate;
        window.onHold = onHold;
        return window;
    }
    
    private List<Integer> intList(int... values) {
        List<Integer> vals = new ArrayList<>();
        for(int value : values) {
            vals.add(new Integer(value));
        }
        return vals;
    }

}
