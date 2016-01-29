package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkDerivativesHollow extends HollowObject {

    public CharacterArtworkDerivativesHollow(CharacterArtworkDerivativesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRecipeName() {
        int refOrdinal = delegate().getRecipeNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getFileName() {
        int refOrdinal = delegate().getFileNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getImageId() {
        return delegate().getImageId(ordinal);
    }

    public Long _getImageIdBoxed() {
        return delegate().getImageIdBoxed(ordinal);
    }

    public StringHollow _getCdnOriginServerId() {
        int refOrdinal = delegate().getCdnOriginServerIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getWidth() {
        return delegate().getWidth(ordinal);
    }

    public Long _getWidthBoxed() {
        return delegate().getWidthBoxed(ordinal);
    }

    public StringHollow _getCdnDirectory() {
        int refOrdinal = delegate().getCdnDirectoryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getImageType() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCdnOriginServer() {
        int refOrdinal = delegate().getCdnOriginServerOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getHeight() {
        return delegate().getHeight(ordinal);
    }

    public Long _getHeightBoxed() {
        return delegate().getHeightBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkDerivativesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CharacterArtworkDerivativesDelegate delegate() {
        return (CharacterArtworkDerivativesDelegate)delegate;
    }

}