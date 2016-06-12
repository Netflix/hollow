package netflix.admin.videometadata.uploadstat;

import java.util.List;

public interface ServerUploadStatus {
    /**
     * Get the cycle upload status which is trying to produce the specified version.
     *
     * @param version
     * @return
     */
    ServerCycleUploadStatus getCycle(String version);

    /**
     * Get all available cycle statuses
     */
    List<ServerCycleUploadStatus> getCycleStatuses();

    /**
     * Try to remove cycle upload statuses before <code>desiredCycles</code> ago.
     *
     * Only remove cycles for which all artifacts have been fully deployed to all regions.
     *
     * @param desiredCycles
     */
    void cleanUpUploadStatuses(int desiredCycles);
}
