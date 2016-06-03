package netflix.admin.videometadata.uploadstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VMSServerUploadStatus implements ServerUploadStatus {

    private final List<ServerCycleUploadStatus> cycleStatuses;

    public VMSServerUploadStatus() {
        this.cycleStatuses = new ArrayList<>();
    }

    /**
     * Get the cycle upload status which is trying to produce the specified version.
     *
     * @param version
     * @return
     */
    @Override
    public synchronized ServerCycleUploadStatus getCycle(String version) {
        for(ServerCycleUploadStatus status : cycleStatuses) {
            if(version.equals(status.getVersion())) {
                return status;
            }
        }

        VMSServerCycleUploadStatus status = new VMSServerCycleUploadStatus(version);
        cycleStatuses.add(status);

        cleanUpUploadStatuses(10);

        return status;
    }

    /**
     * Get all available cycle statuses
     */
    @Override
    public synchronized List<ServerCycleUploadStatus> getCycleStatuses() {
        List<ServerCycleUploadStatus> cycles = new ArrayList<>(cycleStatuses);

        Collections.sort(cycles, new Comparator<ServerCycleUploadStatus>() {
            public int compare(ServerCycleUploadStatus o1, ServerCycleUploadStatus o2) {
                return o2.getVersion().compareTo(o1.getVersion());
            }
        });

        return cycles;
    }

    /**
     * Try to remove cycle upload statuses before <code>desiredCycles</code> ago.
     *
     * Only remove cycles for which all artifacts have been fully deployed to all regions.
     *
     * @param desiredCycles
     */
    @Override
    public synchronized void cleanUpUploadStatuses(int desiredCycles) {
        for(int i=0;i < cycleStatuses.size() - desiredCycles;i++) {
            if(cycleStatuses.get(i).removeAllSuccessful()) {
                cycleStatuses.remove(i);
                i--;
            }
        }
    }

    private static final ServerUploadStatus theInstance = new VMSServerUploadStatus();

    public static ServerUploadStatus get() {
        return theInstance;
    }

}
