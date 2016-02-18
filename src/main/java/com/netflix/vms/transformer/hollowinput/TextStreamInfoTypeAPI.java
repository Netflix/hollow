package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class TextStreamInfoTypeAPI extends HollowObjectTypeAPI {

    private final TextStreamInfoDelegateLookupImpl delegateLookupImpl;

    TextStreamInfoTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "textLanguageCode",
            "timedTextType",
            "imageTimedTextMasterIndexOffset",
            "imageTimedTextMasterIndexLength"
        });
        this.delegateLookupImpl = new TextStreamInfoDelegateLookupImpl(this);
    }

    public int getTextLanguageCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TextStreamInfo", ordinal, "textLanguageCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getTextLanguageCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTimedTextTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("TextStreamInfo", ordinal, "timedTextType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTimedTextTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getImageTimedTextMasterIndexOffset(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("TextStreamInfo", ordinal, "imageTimedTextMasterIndexOffset");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getImageTimedTextMasterIndexOffsetBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("TextStreamInfo", ordinal, "imageTimedTextMasterIndexOffset");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getImageTimedTextMasterIndexLength(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("TextStreamInfo", ordinal, "imageTimedTextMasterIndexLength");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getImageTimedTextMasterIndexLengthBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("TextStreamInfo", ordinal, "imageTimedTextMasterIndexLength");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public TextStreamInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}