package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhaseElementsDelegate extends HollowObjectDelegate {

    public int getLocalized_metadataOrdinal(int ordinal);

    public int getCharactersOrdinal(int ordinal);

    public int getCastOrdinal(int ordinal);

    public int getArtwork_newOrdinal(int ordinal);

    public int getArtworkOrdinal(int ordinal);

    public int getTrailersOrdinal(int ordinal);

    public RolloutPhaseElementsTypeAPI getTypeAPI();

}