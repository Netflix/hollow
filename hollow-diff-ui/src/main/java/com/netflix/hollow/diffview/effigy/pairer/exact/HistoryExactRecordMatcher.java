package com.netflix.hollow.diffview.effigy.pairer.exact;

import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

public class HistoryExactRecordMatcher implements ExactRecordMatcher {

    public static HistoryExactRecordMatcher INSTANCE = new HistoryExactRecordMatcher();
    
    private HistoryExactRecordMatcher() { }
    
    @Override
    public boolean isExactMatch(HollowTypeDataAccess fromType, int fromOrdinal, HollowTypeDataAccess toType, int toOrdinal) {
        return fromType != null && fromType == toType && fromOrdinal == toOrdinal;
    }

}
