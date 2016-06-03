package netflix.admin.videometadata.uploadstat;

import java.util.Collection;

public interface ServerCycleUploadStatus {
    Collection<FileUploadStatus> getUploadStatuses();

    String getVersion();

    FileUploadStatus getStatus(String keybase, long size);

    /**
     * Removes all completely successful artifact statuses from the uploadStatusMap.
     *
     * @return true if all artifact statuses were removed.
     */
    boolean removeAllSuccessful();
}
