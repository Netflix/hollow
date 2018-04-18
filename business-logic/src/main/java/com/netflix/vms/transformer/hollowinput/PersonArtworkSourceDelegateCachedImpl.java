package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PersonArtworkSourceDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonArtworkSourceDelegate {

    private final int sourceFileIdOrdinal;
    private final Long personId;
    private final Boolean isFallback;
    private final int fallbackSourceFileIdOrdinal;
    private final Integer seqNum;
    private final Integer ordinalPriority;
    private final int fileImageTypeOrdinal;
    private final int phaseTagsOrdinal;
    private final Boolean isSmoky;
    private final Boolean rolloutExclusive;
    private final int attributesOrdinal;
    private final int localesOrdinal;
    private PersonArtworkSourceTypeAPI typeAPI;

    public PersonArtworkSourceDelegateCachedImpl(PersonArtworkSourceTypeAPI typeAPI, int ordinal) {
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.isFallback = typeAPI.getIsFallbackBoxed(ordinal);
        this.fallbackSourceFileIdOrdinal = typeAPI.getFallbackSourceFileIdOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.fileImageTypeOrdinal = typeAPI.getFileImageTypeOrdinal(ordinal);
        this.phaseTagsOrdinal = typeAPI.getPhaseTagsOrdinal(ordinal);
        this.isSmoky = typeAPI.getIsSmokyBoxed(ordinal);
        this.rolloutExclusive = typeAPI.getRolloutExclusiveBoxed(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public long getPersonId(int ordinal) {
        if(personId == null)
            return Long.MIN_VALUE;
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public boolean getIsFallback(int ordinal) {
        if(isFallback == null)
            return false;
        return isFallback.booleanValue();
    }

    public Boolean getIsFallbackBoxed(int ordinal) {
        return isFallback;
    }

    public int getFallbackSourceFileIdOrdinal(int ordinal) {
        return fallbackSourceFileIdOrdinal;
    }

    public int getSeqNum(int ordinal) {
        if(seqNum == null)
            return Integer.MIN_VALUE;
        return seqNum.intValue();
    }

    public Integer getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public int getOrdinalPriority(int ordinal) {
        if(ordinalPriority == null)
            return Integer.MIN_VALUE;
        return ordinalPriority.intValue();
    }

    public Integer getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return fileImageTypeOrdinal;
    }

    public int getPhaseTagsOrdinal(int ordinal) {
        return phaseTagsOrdinal;
    }

    public boolean getIsSmoky(int ordinal) {
        if(isSmoky == null)
            return false;
        return isSmoky.booleanValue();
    }

    public Boolean getIsSmokyBoxed(int ordinal) {
        return isSmoky;
    }

    public boolean getRolloutExclusive(int ordinal) {
        if(rolloutExclusive == null)
            return false;
        return rolloutExclusive.booleanValue();
    }

    public Boolean getRolloutExclusiveBoxed(int ordinal) {
        return rolloutExclusive;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonArtworkSourceTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonArtworkSourceTypeAPI) typeAPI;
    }

}