package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamProfilesTypeAPI extends HollowObjectTypeAPI {

    private final StreamProfilesDelegateLookupImpl delegateLookupImpl;

    public StreamProfilesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "drmType",
            "description",
            "is3D",
            "name27AndAbove",
            "mimeType",
            "drmKeyGroup",
            "name26AndBelow",
            "audioChannelCount",
            "profileType",
            "fileExtension",
            "isAdaptiveSwitching"
        });
        this.delegateLookupImpl = new StreamProfilesDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("StreamProfiles", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("StreamProfiles", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDrmType(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("StreamProfiles", ordinal, "drmType");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getDrmTypeBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("StreamProfiles", ordinal, "drmType");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getDescriptionTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getIs3D(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("StreamProfiles", ordinal, "is3D") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]) == Boolean.TRUE;
    }

    public Boolean getIs3DBoxed(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleBoolean("StreamProfiles", ordinal, "is3D");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[3]);
    }



    public int getName27AndAboveOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "name27AndAbove");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getName27AndAboveTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getMimeTypeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "mimeType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getMimeTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getDrmKeyGroup(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleLong("StreamProfiles", ordinal, "drmKeyGroup");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
    }

    public Long getDrmKeyGroupBoxed(int ordinal) {
        long l;
        if(fieldIndex[6] == -1) {
            l = missingDataHandler().handleLong("StreamProfiles", ordinal, "drmKeyGroup");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[6]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getName26AndBelowOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "name26AndBelow");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getName26AndBelowTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getAudioChannelCount(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleLong("StreamProfiles", ordinal, "audioChannelCount");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
    }

    public Long getAudioChannelCountBoxed(int ordinal) {
        long l;
        if(fieldIndex[8] == -1) {
            l = missingDataHandler().handleLong("StreamProfiles", ordinal, "audioChannelCount");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getProfileTypeOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "profileType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getProfileTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getFileExtensionOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("StreamProfiles", ordinal, "fileExtension");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getFileExtensionTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getIsAdaptiveSwitching(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("StreamProfiles", ordinal, "isAdaptiveSwitching") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]) == Boolean.TRUE;
    }

    public Boolean getIsAdaptiveSwitchingBoxed(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("StreamProfiles", ordinal, "isAdaptiveSwitching");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]);
    }



    public StreamProfilesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}