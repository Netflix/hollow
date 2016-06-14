package netflix.admin.videometadata.uploadstat;

import java.util.Collection;

import com.netflix.config.NetflixConfiguration.RegionEnum;

public interface FileUploadStatus {

    String getKeybase();

    long getFileSize();

    FileRegionUploadStatus getUploadStatus(RegionEnum region);

    Collection<FileRegionUploadStatus> getUploadStatuses();

    boolean isAllSuccessful();

    public static interface FileRegionUploadStatus {
        RegionEnum getRegion();

        UploadStatus getStatus();

        long getDuration();

        int getRetryCount();

        void setStatus(UploadStatus status);

        void incrementRetryCount();
    }

    public static enum UploadStatus {
        UPLOADING,
        COPYING,
        RETRYING,
        FAILED,
        SUCCESS,
        ABORTED
    }
}
