package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface RolloutPhasesElementsDelegate extends HollowObjectDelegate {

    public int getLocalized_metadataOrdinal(int ordinal);

    public int getCharactersOrdinal(int ordinal);

    public int getCastOrdinal(int ordinal);

    public int getArtwork_newOrdinal(int ordinal);

    public int getArtworkOrdinal(int ordinal);

    public int getTrailersOrdinal(int ordinal);

    public RolloutPhasesElementsTypeAPI getTypeAPI();

}