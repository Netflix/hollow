package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoArtWorkTypeAPI extends HollowObjectTypeAPI {

    private final VideoArtWorkDelegateLookupImpl delegateLookupImpl;

    VideoArtWorkTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "imageFormat",
            "recipes",
            "imageId",
            "seqNum",
            "movieId",
            "extensions",
            "locales",
            "imageTypeId",
            "ordinalPriority",
            "attributes",
            "imageType",
            "sourceAttributes"
        });
        this.delegateLookupImpl = new VideoArtWorkDelegateLookupImpl(this);
    }

    public int getImageFormatOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "imageFormat");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getImageFormatTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRecipesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "recipes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoArtWorkArrayOfRecipesTypeAPI getRecipesTypeAPI() {
        return getAPI().getVideoArtWorkArrayOfRecipesTypeAPI();
    }

    public long getImageId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoArtWork", ordinal, "imageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getImageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoArtWork", ordinal, "imageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getSeqNum(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoArtWork", ordinal, "seqNum");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getSeqNumBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoArtWork", ordinal, "seqNum");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMovieId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoArtWork", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoArtWork", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getExtensionsOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "extensions");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public VideoArtWorkArrayOfExtensionsTypeAPI getExtensionsTypeAPI() {
        return getAPI().getVideoArtWorkArrayOfExtensionsTypeAPI();
    }

    public int getLocalesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "locales");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public VideoArtWorkArrayOfLocalesTypeAPI getLocalesTypeAPI() {
        return getAPI().getVideoArtWorkArrayOfLocalesTypeAPI();
    }

    public long getImageTypeId(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleLong("VideoArtWork", ordinal, "imageTypeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
    }

    public Long getImageTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[7] == -1) {
            l = missingDataHandler().handleLong("VideoArtWork", ordinal, "imageTypeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[7]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[7]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getOrdinalPriority(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleLong("VideoArtWork", ordinal, "ordinalPriority");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        long l;
        if(fieldIndex[8] == -1) {
            l = missingDataHandler().handleLong("VideoArtWork", ordinal, "ordinalPriority");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[8]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getAttributesOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "attributes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public VideoArtWorkArrayOfAttributesTypeAPI getAttributesTypeAPI() {
        return getAPI().getVideoArtWorkArrayOfAttributesTypeAPI();
    }

    public int getImageTypeOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "imageType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getImageTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSourceAttributesOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoArtWork", ordinal, "sourceAttributes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public VideoArtWorkSourceAttributesTypeAPI getSourceAttributesTypeAPI() {
        return getAPI().getVideoArtWorkSourceAttributesTypeAPI();
    }

    public VideoArtWorkDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}