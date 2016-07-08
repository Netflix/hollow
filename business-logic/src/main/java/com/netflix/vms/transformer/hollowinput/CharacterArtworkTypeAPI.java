package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class CharacterArtworkTypeAPI extends HollowObjectTypeAPI {

    private final CharacterArtworkDelegateLookupImpl delegateLookupImpl;

    CharacterArtworkTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "characterId",
            "sourceFileId",
            "seqNum",
            "derivatives",
            "locales",
            "attributes",
            "ordinalPriority",
            "fileImageType"
        });
        this.delegateLookupImpl = new CharacterArtworkDelegateLookupImpl(this);
    }

    public long getCharacterId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CharacterArtwork", ordinal, "characterId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getCharacterIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CharacterArtwork", ordinal, "characterId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSourceFileIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtwork", ordinal, "sourceFileId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getSourceFileIdTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getSeqNum(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("CharacterArtwork", ordinal, "seqNum");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSeqNumBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("CharacterArtwork", ordinal, "seqNum");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDerivativesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtwork", ordinal, "derivatives");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public ArtworkDerivativeListTypeAPI getDerivativesTypeAPI() {
        return getAPI().getArtworkDerivativeListTypeAPI();
    }

    public int getLocalesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtwork", ordinal, "locales");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public ArtworkLocaleListTypeAPI getLocalesTypeAPI() {
        return getAPI().getArtworkLocaleListTypeAPI();
    }

    public int getAttributesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("CharacterArtwork", ordinal, "attributes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public ArtworkAttributesTypeAPI getAttributesTypeAPI() {
        return getAPI().getArtworkAttributesTypeAPI();
    }

    public long getOrdinalPriority(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("CharacterArtwork", ordinal, "ordinalPriority");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("CharacterArtwork", ordinal, "ordinalPriority");
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
            return missingDataHandler().handleReferencedOrdinal("CharacterArtwork", ordinal, "fileImageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getFileImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CharacterArtworkDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}