package netflix.admin.videometadata.uploadstat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VMSServerCycleUploadStatus implements ServerCycleUploadStatus {

    private final String version;
    private final ConcurrentHashMap<String, FileUploadStatus> uploadStatusMap;


    public VMSServerCycleUploadStatus(String version) {
        this.version = version;
        this.uploadStatusMap = new ConcurrentHashMap<>();
    }

    @Override
    public Collection<FileUploadStatus> getUploadStatuses() {
        List<FileUploadStatus> statuses = new ArrayList<>(uploadStatusMap.values());

        Collections.sort(statuses, new Comparator<FileUploadStatus>() {
            public int compare(FileUploadStatus o1, FileUploadStatus o2) {
                return o1.getKeybase().compareTo(o2.getKeybase());
            }
        });

        return statuses;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FileUploadStatus getStatus(String keybase, long size) {
        FileUploadStatus status = uploadStatusMap.get(keybase);
        if(status == null) {
            status = new VMSFileUploadStatus(keybase, size);
            FileUploadStatus existing = uploadStatusMap.putIfAbsent(keybase, status);
            if(existing != null) {
                status = existing;
            }
        }
        return status;
    }

    @Override
    public boolean removeAllSuccessful() {
        Iterator<Map.Entry<String, FileUploadStatus>> iter  = uploadStatusMap.entrySet().iterator();

        while(iter.hasNext()) {
            Map.Entry<String, FileUploadStatus> entry = iter.next();
            if(entry.getValue().isAllSuccessful())
                iter.remove();
        }

        return uploadStatusMap.isEmpty();
    }
}
