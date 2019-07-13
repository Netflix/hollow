package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class IndividualSupplementalTypeAPI extends HollowObjectTypeAPI {

    private final IndividualSupplementalDelegateLookupImpl delegateLookupImpl;

    public IndividualSupplementalTypeAPI(SupplementalAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "sequenceNumber",
            "subType",
            "themes",
            "identifiers",
            "usages",
            "postplay",
            "general",
            "thematic",
            "approvedForExploit"
        });
        this.delegateLookupImpl = new IndividualSupplementalDelegateLookupImpl(this);
    }

    public long getMovieId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("IndividualSupplemental", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("IndividualSupplemental", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getSequenceNumber(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("IndividualSupplemental", ordinal, "sequenceNumber");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("IndividualSupplemental", ordinal, "sequenceNumber");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getSubTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "subType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getSubTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getThemesOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "themes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public IndividualSupplementalThemeSetTypeAPI getThemesTypeAPI() {
        return getAPI().getIndividualSupplementalThemeSetTypeAPI();
    }

    public int getIdentifiersOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "identifiers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public IndividualSupplementalIdentifierSetTypeAPI getIdentifiersTypeAPI() {
        return getAPI().getIndividualSupplementalIdentifierSetTypeAPI();
    }

    public int getUsagesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("IndividualSupplemental", ordinal, "usages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public IndividualSupplementalUsageSetTypeAPI getUsagesTypeAPI() {
        return getAPI().getIndividualSupplementalUsageSetTypeAPI();
    }

    public boolean getPostplay(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "postplay"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getPostplayBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "postplay");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public boolean getGeneral(int ordinal) {
        if(fieldIndex[7] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "general"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]));
    }

    public Boolean getGeneralBoxed(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "general");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[7]);
    }



    public boolean getThematic(int ordinal) {
        if(fieldIndex[8] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "thematic"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]));
    }

    public Boolean getThematicBoxed(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "thematic");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[8]);
    }



    public boolean getApprovedForExploit(int ordinal) {
        if(fieldIndex[9] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "approvedForExploit"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]));
    }

    public Boolean getApprovedForExploitBoxed(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleBoolean("IndividualSupplemental", ordinal, "approvedForExploit");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]);
    }



    public IndividualSupplementalDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public SupplementalAPI getAPI() {
        return (SupplementalAPI) api;
    }

}