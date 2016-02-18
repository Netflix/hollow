package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface TextStreamInfoDelegate extends HollowObjectDelegate {

    public int getTextLanguageCodeOrdinal(int ordinal);

    public int getTimedTextTypeOrdinal(int ordinal);

    public long getImageTimedTextMasterIndexOffset(int ordinal);

    public Long getImageTimedTextMasterIndexOffsetBoxed(int ordinal);

    public long getImageTimedTextMasterIndexLength(int ordinal);

    public Long getImageTimedTextMasterIndexLengthBoxed(int ordinal);

    public TextStreamInfoTypeAPI getTypeAPI();

}