package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PersonArtworkTypeAPI extends HollowObjectTypeAPI {

    private final PersonArtworkDelegateLookupImpl delegateLookupImpl;

    PersonArtworkTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "derivatives",
            "locales",
            "seqNum",
            "ordinalPriority",
            "sourceFileId",
            "attributes",
            "personId",
            "fileImageType"
        });
        this.delegateLookupImpl = new PersonArtworkDelegateLookupImpl(this);
    }

    public int getDerivativesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtwork", ordinal, "derivatives");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public ArtworkDerivativeListTypeAPI getDerivativesTypeAPI() {
        return getAPI().getArtworkDerivativeListTypeAPI();
    }

    public int getLocalesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtwork", ordinal, "locales");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ArtworkLocaleListTypeAPI getLocalesTypeAPI() {
        return getAPI().getArtworkLocaleListTypeAPI();
    }

    public long getSeqNum(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("PersonArtwork", ordinal, "seqNum");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSeqNumBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("PersonArtwork", ordinal, "seqNum");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getOrdinalPriority(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("PersonArtwork", ordinal, "ordinalPriority");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("PersonArtwork", ordinal, "ordinalPriority");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSourceFileIdOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtwork", ordinal, "sourceFileId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getSourceFileIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAttributesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtwork", ordinal, "attributes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public ArtworkAttributesTypeAPI getAttributesTypeAPI() {
        return getAPI().getArtworkAttributesTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("PersonArtwork", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("PersonArtwork", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getFileImageTypeOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("PersonArtwork", ordinal, "fileImageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getFileImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PersonArtworkDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}