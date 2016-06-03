package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PackageStreamTypeAPI extends HollowObjectTypeAPI {

    private final PackageStreamDelegateLookupImpl delegateLookupImpl;

    PackageStreamTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "downloadableId",
            "streamProfileId",
            "modifications",
            "fileIdentification",
            "dimensions",
            "tags",
            "assetType",
            "imageInfo",
            "nonImageInfo",
            "deployment"
        });
        this.delegateLookupImpl = new PackageStreamDelegateLookupImpl(this);
    }

    public long getDownloadableId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("PackageStream", ordinal, "downloadableId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getDownloadableIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("PackageStream", ordinal, "downloadableId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getStreamProfileId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("PackageStream", ordinal, "streamProfileId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("PackageStream", ordinal, "streamProfileId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getModificationsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "modifications");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ListOfStringTypeAPI getModificationsTypeAPI() {
        return getAPI().getListOfStringTypeAPI();
    }

    public int getFileIdentificationOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "fileIdentification");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StreamFileIdentificationTypeAPI getFileIdentificationTypeAPI() {
        return getAPI().getStreamFileIdentificationTypeAPI();
    }

    public int getDimensionsOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "dimensions");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StreamDimensionsTypeAPI getDimensionsTypeAPI() {
        return getAPI().getStreamDimensionsTypeAPI();
    }

    public int getTagsOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "tags");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getTagsTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getAssetTypeOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "assetType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public StreamAssetTypeTypeAPI getAssetTypeTypeAPI() {
        return getAPI().getStreamAssetTypeTypeAPI();
    }

    public int getImageInfoOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "imageInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public ImageStreamInfoTypeAPI getImageInfoTypeAPI() {
        return getAPI().getImageStreamInfoTypeAPI();
    }

    public int getNonImageInfoOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "nonImageInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StreamNonImageInfoTypeAPI getNonImageInfoTypeAPI() {
        return getAPI().getStreamNonImageInfoTypeAPI();
    }

    public int getDeploymentOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("PackageStream", ordinal, "deployment");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StreamDeploymentTypeAPI getDeploymentTypeAPI() {
        return getAPI().getStreamDeploymentTypeAPI();
    }

    public PackageStreamDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}