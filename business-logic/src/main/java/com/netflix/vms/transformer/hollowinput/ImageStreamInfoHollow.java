package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ImageStreamInfoHollow extends HollowObject {

    public ImageStreamInfoHollow(ImageStreamInfoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public int _getImageCount() {
        return delegate().getImageCount(ordinal);
    }

    public Integer _getImageCountBoxed() {
        return delegate().getImageCountBoxed(ordinal);
    }

    public StringHollow _getImageFormat() {
        int refOrdinal = delegate().getImageFormatOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getOffsetMillis() {
        return delegate().getOffsetMillis(ordinal);
    }

    public Long _getOffsetMillisBoxed() {
        return delegate().getOffsetMillisBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ImageStreamInfoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ImageStreamInfoDelegate delegate() {
        return (ImageStreamInfoDelegate)delegate;
    }

}