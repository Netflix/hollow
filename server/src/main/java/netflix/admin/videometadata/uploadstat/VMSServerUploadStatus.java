package netflix.admin.videometadata.uploadstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VMSServerUploadStatus {

    private final List<VMSServerCycleUploadStatus> cycleStatuses;

    public VMSServerUploadStatus() {
        this.cycleStatuses = new ArrayList<VMSServerCycleUploadStatus>();
    }

    /**
     * Get the cycle upload status which is trying to produce the specified version.
     *
     * @param version
     * @return
     */
    public synchronized VMSServerCycleUploadStatus getCycle(String version) {
        for(VMSServerCycleUploadStatus status : cycleStatuses) {
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
    public synchronized List<VMSServerCycleUploadStatus> getCycleStatuses() {
        List<VMSServerCycleUploadStatus> cycles = new ArrayList<VMSServerCycleUploadStatus>(cycleStatuses);

        Collections.sort(cycles, new Comparator<VMSServerCycleUploadStatus>() {
            public int compare(VMSServerCycleUploadStatus o1, VMSServerCycleUploadStatus o2) {
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
    public synchronized void cleanUpUploadStatuses(int desiredCycles) {
        for(int i=0;i < cycleStatuses.size() - desiredCycles;i++) {
            if(cycleStatuses.get(i).removeAllSuccessful()) {
                cycleStatuses.remove(i);
                i--;
            }
        }
    }

    private static final VMSServerUploadStatus theInstance = new VMSServerUploadStatus();

    public static VMSServerUploadStatus get() {
        return theInstance;
    }

}
