package netflix.admin.videometadata.uploadstat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VMSServerCycleUploadStatus {

    private final String version;
    private final ConcurrentHashMap<String, VMSFileUploadStatus> uploadStatusMap;


    public VMSServerCycleUploadStatus(String version) {
        this.version = version;
        this.uploadStatusMap = new ConcurrentHashMap<String, VMSFileUploadStatus>();
    }

    public Collection<VMSFileUploadStatus> getUploadStatuses() {
        List<VMSFileUploadStatus> statuses = new ArrayList<VMSFileUploadStatus>(uploadStatusMap.values());

        Collections.sort(statuses, new Comparator<VMSFileUploadStatus>() {
            public int compare(VMSFileUploadStatus o1, VMSFileUploadStatus o2) {
                return o1.getKeybase().compareTo(o2.getKeybase());
            }
        });

        return statuses;
    }

    public String getVersion() {
        return version;
    }

    public VMSFileUploadStatus getStatus(String keybase, long size) {
        VMSFileUploadStatus status = uploadStatusMap.get(keybase);
        if(status == null) {
            status = new VMSFileUploadStatus(keybase, size);
            VMSFileUploadStatus existing = uploadStatusMap.putIfAbsent(keybase, status);
            if(existing != null) {
                status = existing;
            }
        }
        return status;
    }

    /**
     * Removes all completely successful artifact statuses from the uploadStatusMap.
     *
     * @return true if all artifact statuses were removed.
     */
    boolean removeAllSuccessful() {
        Iterator<Map.Entry<String, VMSFileUploadStatus>> iter  = uploadStatusMap.entrySet().iterator();

        while(iter.hasNext()) {
            Map.Entry<String, VMSFileUploadStatus> entry = iter.next();
            if(entry.getValue().isAllSuccessful())
                iter.remove();
        }

        return uploadStatusMap.isEmpty();
    }

}
