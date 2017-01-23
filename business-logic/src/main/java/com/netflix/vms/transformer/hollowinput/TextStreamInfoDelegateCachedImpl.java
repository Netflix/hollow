package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class TextStreamInfoDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TextStreamInfoDelegate {

    private final int textLanguageCodeOrdinal;
    private final int timedTextTypeOrdinal;
    private final Long imageTimedTextMasterIndexOffset;
    private final Long imageTimedTextMasterIndexLength;
   private TextStreamInfoTypeAPI typeAPI;

    public TextStreamInfoDelegateCachedImpl(TextStreamInfoTypeAPI typeAPI, int ordinal) {
        this.textLanguageCodeOrdinal = typeAPI.getTextLanguageCodeOrdinal(ordinal);
        this.timedTextTypeOrdinal = typeAPI.getTimedTextTypeOrdinal(ordinal);
        this.imageTimedTextMasterIndexOffset = typeAPI.getImageTimedTextMasterIndexOffsetBoxed(ordinal);
        this.imageTimedTextMasterIndexLength = typeAPI.getImageTimedTextMasterIndexLengthBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getTextLanguageCodeOrdinal(int ordinal) {
        return textLanguageCodeOrdinal;
    }

    public int getTimedTextTypeOrdinal(int ordinal) {
        return timedTextTypeOrdinal;
    }

    public long getImageTimedTextMasterIndexOffset(int ordinal) {
        return imageTimedTextMasterIndexOffset.longValue();
    }

    public Long getImageTimedTextMasterIndexOffsetBoxed(int ordinal) {
        return imageTimedTextMasterIndexOffset;
    }

    public long getImageTimedTextMasterIndexLength(int ordinal) {
        return imageTimedTextMasterIndexLength.longValue();
    }

    public Long getImageTimedTextMasterIndexLengthBoxed(int ordinal) {
        return imageTimedTextMasterIndexLength;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TextStreamInfoTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TextStreamInfoTypeAPI) typeAPI;
    }

}