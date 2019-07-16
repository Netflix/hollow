package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface PhaseArtworkDelegate extends HollowObjectDelegate {

    public long getAssetId(int ordinal);

    public Long getAssetIdBoxed(int ordinal);

    public String getFileId(int ordinal);

    public boolean isFileIdEqual(int ordinal, String testValue);

    public boolean getIsSynthetic(int ordinal);

    public Boolean getIsSyntheticBoxed(int ordinal);

    public long getDateCreated(int ordinal);

    public Long getDateCreatedBoxed(int ordinal);

    public int getDateCreatedOrdinal(int ordinal);

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public String getCreatedBy(int ordinal);

    public boolean isCreatedByEqual(int ordinal, String testValue);

    public int getCreatedByOrdinal(int ordinal);

    public String getUpdatedBy(int ordinal);

    public boolean isUpdatedByEqual(int ordinal, String testValue);

    public int getUpdatedByOrdinal(int ordinal);

    public PhaseArtworkTypeAPI getTypeAPI();

}