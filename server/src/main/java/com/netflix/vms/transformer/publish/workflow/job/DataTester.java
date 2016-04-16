package com.netflix.vms.transformer.publish.workflow.job;

import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import java.util.List;
import java.util.Map;

public interface DataTester {

	public Map<VideoCountryKey, Boolean> testVideoCountryKeys(List<VideoCountryKey> keys) throws Exception;

    public String getInstanceInPlayBackMonkeyStack() throws Exception;

    public Map<VideoCountryKey, Boolean> testVideoCountryKeysWithRetry(List<VideoCountryKey> keys, int numOfRetries) throws Exception;

}