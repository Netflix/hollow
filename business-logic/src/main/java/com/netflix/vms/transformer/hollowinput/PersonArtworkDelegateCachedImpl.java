package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class PersonArtworkDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonArtworkDelegate {

    private final int derivativesOrdinal;
    private final int localesOrdinal;
    private final Long seqNum;
    private final Long ordinalPriority;
    private final int sourceFileIdOrdinal;
    private final int attributesOrdinal;
    private final Long personId;
    private final int fileImageTypeOrdinal;
   private PersonArtworkTypeAPI typeAPI;

    public PersonArtworkDelegateCachedImpl(PersonArtworkTypeAPI typeAPI, int ordinal) {
        this.derivativesOrdinal = typeAPI.getDerivativesOrdinal(ordinal);
        this.localesOrdinal = typeAPI.getLocalesOrdinal(ordinal);
        this.seqNum = typeAPI.getSeqNumBoxed(ordinal);
        this.ordinalPriority = typeAPI.getOrdinalPriorityBoxed(ordinal);
        this.sourceFileIdOrdinal = typeAPI.getSourceFileIdOrdinal(ordinal);
        this.attributesOrdinal = typeAPI.getAttributesOrdinal(ordinal);
        this.personId = typeAPI.getPersonIdBoxed(ordinal);
        this.fileImageTypeOrdinal = typeAPI.getFileImageTypeOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return derivativesOrdinal;
    }

    public int getLocalesOrdinal(int ordinal) {
        return localesOrdinal;
    }

    public long getSeqNum(int ordinal) {
        return seqNum.longValue();
    }

    public Long getSeqNumBoxed(int ordinal) {
        return seqNum;
    }

    public long getOrdinalPriority(int ordinal) {
        return ordinalPriority.longValue();
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return ordinalPriority;
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return sourceFileIdOrdinal;
    }

    public int getAttributesOrdinal(int ordinal) {
        return attributesOrdinal;
    }

    public long getPersonId(int ordinal) {
        return personId.longValue();
    }

    public Long getPersonIdBoxed(int ordinal) {
        return personId;
    }

    public int getFileImageTypeOrdinal(int ordinal) {
        return fileImageTypeOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonArtworkTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonArtworkTypeAPI) typeAPI;
    }

}