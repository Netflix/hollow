package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieExtensionOverride extends HollowObject {

    public MovieExtensionOverride(MovieExtensionOverrideDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getEntityType() {
        return delegate().getEntityType(ordinal);
    }

    public boolean isEntityTypeEqual(String testValue) {
        return delegate().isEntityTypeEqual(ordinal, testValue);
    }

    public OverrideEntityType getEntityTypeHollowReference() {
        int refOrdinal = delegate().getEntityTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getOverrideEntityType(refOrdinal);
    }

    public String getEntityValue() {
        return delegate().getEntityValue(ordinal);
    }

    public boolean isEntityValueEqual(String testValue) {
        return delegate().isEntityValueEqual(ordinal, testValue);
    }

    public OverrideEntityValue getEntityValueHollowReference() {
        int refOrdinal = delegate().getEntityValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getOverrideEntityValue(refOrdinal);
    }

    public String getAttributeValue() {
        return delegate().getAttributeValue(ordinal);
    }

    public boolean isAttributeValueEqual(String testValue) {
        return delegate().isAttributeValueEqual(ordinal, testValue);
    }

    public AttributeValue getAttributeValueHollowReference() {
        int refOrdinal = delegate().getAttributeValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAttributeValue(refOrdinal);
    }

    public Long getDateCreatedBoxed() {
        return delegate().getDateCreatedBoxed(ordinal);
    }

    public long getDateCreated() {
        return delegate().getDateCreated(ordinal);
    }

    public Date getDateCreatedHollowReference() {
        int refOrdinal = delegate().getDateCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public long getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Date getLastUpdatedHollowReference() {
        int refOrdinal = delegate().getLastUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getCreatedBy() {
        return delegate().getCreatedBy(ordinal);
    }

    public boolean isCreatedByEqual(String testValue) {
        return delegate().isCreatedByEqual(ordinal, testValue);
    }

    public HString getCreatedByHollowReference() {
        int refOrdinal = delegate().getCreatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedBy() {
        return delegate().getUpdatedBy(ordinal);
    }

    public boolean isUpdatedByEqual(String testValue) {
        return delegate().isUpdatedByEqual(ordinal, testValue);
    }

    public HString getUpdatedByHollowReference() {
        int refOrdinal = delegate().getUpdatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public MovieExtensionOverrideTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieExtensionOverrideDelegate delegate() {
        return (MovieExtensionOverrideDelegate)delegate;
    }

}