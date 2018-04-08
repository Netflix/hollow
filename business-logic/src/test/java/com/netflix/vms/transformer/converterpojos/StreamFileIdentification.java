package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.Arrays;


@SuppressWarnings("all")
@HollowTypeName(name="StreamFileIdentification")
public class StreamFileIdentification implements Cloneable {

    public char[] filename = null;
    public long fileSizeInBytes = java.lang.Long.MIN_VALUE;
    public long sha1_1 = java.lang.Long.MIN_VALUE;
    public long sha1_2 = java.lang.Long.MIN_VALUE;
    public long sha1_3 = java.lang.Long.MIN_VALUE;
    public long crc32 = java.lang.Long.MIN_VALUE;
    public long createdTimeSeconds = java.lang.Long.MIN_VALUE;

    public StreamFileIdentification setFilename(char[] filename) {
        this.filename = filename;
        return this;
    }
    public StreamFileIdentification setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
        return this;
    }
    public StreamFileIdentification setSha1_1(long sha1_1) {
        this.sha1_1 = sha1_1;
        return this;
    }
    public StreamFileIdentification setSha1_2(long sha1_2) {
        this.sha1_2 = sha1_2;
        return this;
    }
    public StreamFileIdentification setSha1_3(long sha1_3) {
        this.sha1_3 = sha1_3;
        return this;
    }
    public StreamFileIdentification setCrc32(long crc32) {
        this.crc32 = crc32;
        return this;
    }
    public StreamFileIdentification setCreatedTimeSeconds(long createdTimeSeconds) {
        this.createdTimeSeconds = createdTimeSeconds;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamFileIdentification))
            return false;

        StreamFileIdentification o = (StreamFileIdentification) other;
        if(!Arrays.equals(o.filename, filename)) return false;
        if(o.fileSizeInBytes != fileSizeInBytes) return false;
        if(o.sha1_1 != sha1_1) return false;
        if(o.sha1_2 != sha1_2) return false;
        if(o.sha1_3 != sha1_3) return false;
        if(o.crc32 != crc32) return false;
        if(o.createdTimeSeconds != createdTimeSeconds) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + Arrays.hashCode(filename);
        hashCode = hashCode * 31 + (int) (fileSizeInBytes ^ (fileSizeInBytes >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_1 ^ (sha1_1 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_2 ^ (sha1_2 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_3 ^ (sha1_3 >>> 32));
        hashCode = hashCode * 31 + (int) (crc32 ^ (crc32 >>> 32));
        hashCode = hashCode * 31 + (int) (createdTimeSeconds ^ (createdTimeSeconds >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamFileIdentification{");
        builder.append("filename=").append(filename);
        builder.append(",fileSizeInBytes=").append(fileSizeInBytes);
        builder.append(",sha1_1=").append(sha1_1);
        builder.append(",sha1_2=").append(sha1_2);
        builder.append(",sha1_3=").append(sha1_3);
        builder.append(",crc32=").append(crc32);
        builder.append(",createdTimeSeconds=").append(createdTimeSeconds);
        builder.append("}");
        return builder.toString();
    }

    public StreamFileIdentification clone() {
        try {
            StreamFileIdentification clone = (StreamFileIdentification)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}