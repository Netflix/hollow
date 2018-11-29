package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowoutput.DateWindow;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DateWindowAggregator {

    private final Set<DateWindow> windows;
    private final List<DateWindow> mergedWindows;

    private boolean merged = false;

    DateWindowAggregator() {
        this.windows = new HashSet<>();
        this.mergedWindows = new ArrayList<>();
    }

    void addDateWindow(long startTime, long endTime) {
        windows.add(window(startTime, endTime));
    }

    void mergeDateWindows() {
        if (windows.size() == 0 || merged)
            return;

        List<DateWindow> sortedWindows = new ArrayList<>(windows);

        Comparator<DateWindow> c = Comparator.comparingLong(o1 -> o1.startDateTimestamp);
        sortedWindows.sort(c.thenComparingLong(o1 -> o1.endDateTimestamp));

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

    /**
     * Find the first window that is within {@code start} and {@code end}. That is, we're looking
     * for the first window that starts after {@code start} and before {@code end}. We then bound
     * that by the tighter of start/the actual window start, and end/the actual window end.
     *
     * Note that mergedWindows is already sorted in ascending order of startDateTimestamp.
     *
     * @return a DateWindow matching the above criteria, or null if none is found
     */
    DateWindow matchDateWindowAgainstMergedDateWindows(long start, long end) {
        return mergedWindows.stream()
                // windows that end after our start and start before our end
                .filter(w -> start <= w.endDateTimestamp && end >= w.startDateTimestamp)
                .map(window -> {
                    boolean reuseExistingWindowObject =
                            window.startDateTimestamp >= start && window.endDateTimestamp <= end;
                    return reuseExistingWindowObject ? window
                            : window(Math.max(window.startDateTimestamp, start),
                                    Math.min(window.endDateTimestamp, end));
                }).findFirst().orElse(null);
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
