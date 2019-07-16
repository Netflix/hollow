package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieExtensionOverrideDelegate extends HollowObjectDelegate {

    public String getEntityType(int ordinal);

    public boolean isEntityTypeEqual(int ordinal, String testValue);

    public int getEntityTypeOrdinal(int ordinal);

    public String getEntityValue(int ordinal);

    public boolean isEntityValueEqual(int ordinal, String testValue);

    public int getEntityValueOrdinal(int ordinal);

    public String getAttributeValue(int ordinal);

    public boolean isAttributeValueEqual(int ordinal, String testValue);

    public int getAttributeValueOrdinal(int ordinal);

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

    public MovieExtensionOverrideTypeAPI getTypeAPI();

}