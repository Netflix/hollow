package com.netflix.vms.transformer.publish.workflow.logmessage;

import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ViewShareMessageTest {

    @Test
    public void invokeWithInvalidVideo() throws Exception {
        List<VideoCountryKey> failedIDs = new ArrayList<>();
        failedIDs.add(new VideoCountryKey("US", 1001));
        failedIDs.add(new VideoCountryKey("CA", 1002));
        failedIDs.add(new VideoCountryKey("US", 1003));
        failedIDs.add(new VideoCountryKey("CA", 1004));

        ViewShareMessage msg = new ViewShareMessage("testMSG", "CA", failedIDs, 1.0f, 1.0f);
        System.out.print(msg.toString());
    }
}
