package com.netflix.vms.transformer.publish.workflow.playbackmonkey;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.config.NetflixConfiguration;
import com.netflix.lifecycle.NFLifecycleUnitTester;
import com.netflix.niws.client.RestClientManager;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.impl.ValuableVideoHolder.ValuableVideo;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PlaybackMonkeyInvokerTest {
    private static final String COUNTRY = "US";
    private static NFLifecycleUnitTester unitTester;
    private static RestClientManager restClientManager;
    private static PlaybackMonkeyTester pbmInvoker;
    
    private TaggingLogger logger = new TaggingLogger() {
        @Override
        public void log(Severity severity, Collection<LogTag> tags, String message, Object... args) {
        }
    };

    @BeforeClass
    public static void setup() throws Exception {
        if (unitTester == null) {
            unitTester = initEnv();
        }
        restClientManager = unitTester.getInjector().getInstance(RestClientManager.class);
        pbmInvoker = new PlaybackMonkeyTester(restClientManager);
    }
    
    @AfterClass
    public static void shutdownEnv() {
        if (unitTester != null) {
            try {
                unitTester.shutdown();
            } catch (Exception ignore) {
            }
        }
    }    

    static NFLifecycleUnitTester initEnv() throws Exception {
        NetflixConfiguration.setEnvironment("test");
        Properties props = new Properties();
        props.setProperty("platform.ListOfComponentsToInit", "LOGGING,AWS,S3");
        props.setProperty("vms-pbm-client.niws.client.AppName", "playbackmonkey");
        props.setProperty("vms-pbm-client.niws.client.DeploymentContextBasedVipAddresses", "playbackmonkey-:7001");
        props.setProperty("netflix.appinfo.validateInstanceId", "false");
        props.setProperty("netflix.discovery.registration.enabled", "false");        
        props.setProperty("netflix.appinfo.doNotInitWithAmazonInfo", "true");

        NFLifecycleUnitTester nfLifecycleUnitTester = new NFLifecycleUnitTester(props);
        nfLifecycleUnitTester.start();
        return nfLifecycleUnitTester;
    }
    
    @Test
    public void invokeWithInvalidVideo() throws Exception {
        Set<ValuableVideo> valuableVideos = new HashSet<>();
        int videoId = 1;
        valuableVideos.add(new ValuableVideo(COUNTRY, videoId, false));
        Map<VideoCountryKey, Boolean> results = pbmInvoker.testVideoCountryKeysWithRetry(logger, valuableVideos, 5);
        VideoCountryKey videoCountryKey = new VideoCountryKey(COUNTRY, videoId);
        assertFalse(results.get(videoCountryKey));
    }

    /*
     * Update/Check the validity of video id being used before running the TEST
     */
//    @Test
    public void invokeWithValidVideo() throws Exception {
        Set<ValuableVideo> valuableVideos = new HashSet<>();
        int videoId = 60001366;
        valuableVideos.add(new ValuableVideo(COUNTRY, videoId, true));
        Map<VideoCountryKey, Boolean> results = pbmInvoker.testVideoCountryKeysWithRetry(logger, valuableVideos, 5);
        VideoCountryKey videoCountryKey = new VideoCountryKey(COUNTRY, videoId);
        assertTrue(results.get(videoCountryKey));
    }
}
