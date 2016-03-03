package com.netflix.vms.transformer.modules.packages;

public class SuperHDIdentifier {

    private final int encodingProfileId;
    private final int bitrate;

    public SuperHDIdentifier(int encodingProfileId, int bitrate) {
        this.encodingProfileId = encodingProfileId;
        this.bitrate = bitrate;
    }

    public int getEncodingProfileId() {
        return encodingProfileId;
    }

    public int getBitrate() {
        return bitrate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bitrate;
        result = prime * result + encodingProfileId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SuperHDIdentifier other = (SuperHDIdentifier) obj;
        if (bitrate != other.bitrate)
            return false;
        if (encodingProfileId != other.encodingProfileId)
            return false;
        return true;
    }

}
