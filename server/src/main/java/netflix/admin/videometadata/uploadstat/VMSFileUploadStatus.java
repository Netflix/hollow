package netflix.admin.videometadata.uploadstat;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class VMSFileUploadStatus implements FileUploadStatus {

    private final String keybase;
    private final long size;
    private final Map<RegionEnum, FileRegionUploadStatus> uploadStatusMap;


    public VMSFileUploadStatus(String keybase, long size) {
        this.keybase = keybase;
        this.size = size;
        this.uploadStatusMap = new EnumMap<RegionEnum, FileRegionUploadStatus>(RegionEnum.class);
    }

    public String getKeybase() {
        return keybase;
    }

    public long getFileSize() {
        return size;
    }

    public FileRegionUploadStatus getUploadStatus(RegionEnum region) {
        FileRegionUploadStatus upload = uploadStatusMap.get(region);
        if(upload == null) {
            upload = new VMSFileRegionUploadStatus(region);
            uploadStatusMap.put(region, upload);
        }
        return upload;
    }

    public Collection<FileRegionUploadStatus> getUploadStatuses() {
        return uploadStatusMap.values();
    }

    public boolean isAllSuccessful() {
        for(FileRegionUploadStatus uploadStatus : getUploadStatuses()) {
            if(uploadStatus.getStatus() != UploadStatus.SUCCESS) {
                return false;
            }
        }
        return true;
    }

    public static class VMSFileRegionUploadStatus implements FileRegionUploadStatus {
        private final RegionEnum region;
        private UploadStatus status;
        private int retryCount;
        private long beginTime;
        private long completionTime;

        public VMSFileRegionUploadStatus(RegionEnum region) {
            this.region = region;
            this.status = UploadStatus.UPLOADING;
            this.beginTime = System.currentTimeMillis();
        }

        public RegionEnum getRegion() {
            return region;
        }

        public UploadStatus getStatus() {
            return status;
        }

        public long getDuration() {
            if(completionTime == 0) {
                return System.currentTimeMillis() - beginTime;
            }

            return completionTime - beginTime;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setStatus(UploadStatus status) {
            this.status = status;
            if(status == UploadStatus.SUCCESS || status == UploadStatus.FAILED || status == UploadStatus.ABORTED) {
                completionTime = System.currentTimeMillis();
            }
        }

        public void incrementRetryCount() {
            retryCount++;
        }
    }
}
