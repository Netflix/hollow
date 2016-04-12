package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class TextStreamInfoDelegateLookupImpl extends HollowObjectAbstractDelegate implements TextStreamInfoDelegate {

    private final TextStreamInfoTypeAPI typeAPI;

    public TextStreamInfoDelegateLookupImpl(TextStreamInfoTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getTextLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getTextLanguageCodeOrdinal(ordinal);
    }

    public int getTimedTextTypeOrdinal(int ordinal) {
        return typeAPI.getTimedTextTypeOrdinal(ordinal);
    }

    public long getImageTimedTextMasterIndexOffset(int ordinal) {
        return typeAPI.getImageTimedTextMasterIndexOffset(ordinal);
    }

    public Long getImageTimedTextMasterIndexOffsetBoxed(int ordinal) {
        return typeAPI.getImageTimedTextMasterIndexOffsetBoxed(ordinal);
    }

    public long getImageTimedTextMasterIndexLength(int ordinal) {
        return typeAPI.getImageTimedTextMasterIndexLength(ordinal);
    }

    public Long getImageTimedTextMasterIndexLengthBoxed(int ordinal) {
        return typeAPI.getImageTimedTextMasterIndexLengthBoxed(ordinal);
    }

    public TextStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}