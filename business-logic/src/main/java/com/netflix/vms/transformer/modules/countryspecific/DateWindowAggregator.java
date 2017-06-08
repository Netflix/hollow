package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.DateWindow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DateWindowAggregator {

    private final Set<DateWindow> windows;
    private final List<DateWindow> mergedWindows;

    private boolean merged = false;

    public DateWindowAggregator() {
        this.windows = new HashSet<>();
        this.mergedWindows = new ArrayList<DateWindow>();
    }

    public void addDateWindow(long startTime, long endTime) {
        windows.add(window(startTime, endTime));
    }

    public void mergeDateWindows() {
        if (windows.size() == 0 || merged)
            return;

        List<DateWindow> sortedWindows = new ArrayList<>(windows);

        sortedWindows.sort((o1, o2) -> {
            int cmp = o1.startDateTimestamp < o2.startDateTimestamp ? -1 : (o1.startDateTimestamp == o2.startDateTimestamp ? 0 : 1);
            if (cmp == 0)
                cmp = o1.endDateTimestamp < o2.endDateTimestamp ? -1 : (o1.endDateTimestamp == o2.endDateTimestamp ? 0 : 1);
            return cmp;
        });

        long currentStartDate = sortedWindows.get(0).startDateTimestamp;
        long currentEndDate = sortedWindows.get(0).endDateTimestamp;

        for (int i = 1; i < sortedWindows.size(); i++) {
            if (sortedWindows.get(i).startDateTimestamp <= currentEndDate + 1) {
                if (sortedWindows.get(i).endDateTimestamp > currentEndDate)
                    currentEndDate = sortedWindows.get(i).endDateTimestamp;
            } else {
                mergedWindows.add(window(currentStartDate, currentEndDate));
                currentStartDate = sortedWindows.get(i).startDateTimestamp;
                currentEndDate = sortedWindows.get(i).endDateTimestamp;
            }
        }

        mergedWindows.add(window(currentStartDate, currentEndDate));

        merged = true;
    }

    public DateWindow matchDateWindowAgainstMergedDateWindows(long start, long end) {
        for (int i = 0; i < mergedWindows.size(); i++) {
            DateWindow window = mergedWindows.get(i);
            if (window.endDateTimestamp >= start) {
                if (window.startDateTimestamp >= start && window.endDateTimestamp <= end)
                    return window;
                start = Math.max(window.startDateTimestamp, start);
                end = Math.min(window.endDateTimestamp, end);
                return window(start, end);
            }
        }

        return null;
    }

    public void reset() {
        windows.clear();
        mergedWindows.clear();
        merged = false;
    }

    private DateWindow window(long start, long end) {
        DateWindow window = new DateWindow();
        window.startDateTimestamp = start;
        window.endDateTimestamp = end;
        return window;
    }

}
