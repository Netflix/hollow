package com.netflix.vms.transformer.publish.workflow.playbackmonkey;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PlaybackMonkey;

import com.netflix.config.FastProperty;
import com.netflix.hermes.exception.EntityNotFoundException;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.niws.client.IClientResponse;
import com.netflix.niws.client.NFMultivaluedMap;
import com.netflix.niws.client.NIWSClientException;
import com.netflix.niws.client.RestClient;
import com.netflix.niws.client.RestClient.Verb;
import com.netflix.niws.client.RestClientManager;
import com.netflix.playback.monkey.model.ParseUtil;
import com.netflix.playback.monkey.model.PlaybackMonkeyTestResults;
import com.netflix.playback.monkey.model.PlaybackMonkeyTestResults.Status;
import com.netflix.playback.monkey.model.VideoTestDetails;
import com.netflix.servo.monitor.DynamicCounter;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.publish.workflow.VideoCountryKey;
import com.netflix.vms.transformer.publish.workflow.job.impl.ValuableVideoHolder.ValuableVideo;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.codehaus.jackson.JsonProcessingException;

public class PlaybackMonkeyTester {

    public static final String INITATE_TEST_URL = "/REST/playback/monkey/viewable/";
    public static final String TEST_RESULT_URL = "/REST/playback/monkey/test/";
    public static final String INSTANCE_LIST_URL = "/REST/playback/monkey/system/instances/";

    private static final String PBM_REST_CLIENT_NAME = "vms-pbm-client";
    
	private static final FastProperty.BooleanProperty passDownloadFlag = new FastProperty.BooleanProperty("playback.monkey.passDownloadFlag", true);

    private final RestClient pbmRestClient;

    public PlaybackMonkeyTester() {
        this.pbmRestClient = createClient();
    }
    
    public PlaybackMonkeyTester(RestClientManager restClientManager) {
        this.pbmRestClient = createClient(restClientManager);
    }

    private RestClient createClient(RestClientManager restClientManager) {
        if(pbmRestClient == null) {
            try {
                return restClientManager.registerRestClientUsingProperties(PBM_REST_CLIENT_NAME);
            } catch (NIWSClientException e) {
                e.printStackTrace();
            }
        }
        return pbmRestClient;
    }

    public Map<VideoCountryKey, Boolean> testVideoCountryKeysWithRetry(TaggingLogger logger, Set<ValuableVideo> mostValuableChangedVideos, int numOfTries) throws Exception {
        Map<VideoCountryKey, Boolean> playBackMonkeyResult = new HashMap<>(mostValuableChangedVideos.size());
        for (ValuableVideo valuableVideo : mostValuableChangedVideos) {
            playBackMonkeyResult.put(new VideoCountryKey(valuableVideo.getCountry(), valuableVideo.getVideoId()), false);
        }

        int currentTry = 0;
        List<ValuableVideo> videosToTest = new ArrayList<>(mostValuableChangedVideos);
        while (currentTry++ <= numOfTries) {
            Map<ValuableVideo, Boolean> resultMap = testVideoCountryKeys(logger, videosToTest);

            List<ValuableVideo> failedVideos = new ArrayList<ValuableVideo>(resultMap.size());
            for (Entry<ValuableVideo, Boolean> entry : resultMap.entrySet()) {
                ValuableVideo valuableVideo = entry.getKey();
				Boolean result = entry.getValue();
				if (result) {
                    playBackMonkeyResult.put(new VideoCountryKey(valuableVideo.getCountry(), valuableVideo.getVideoId()), result);
                } else {
                    failedVideos.add(valuableVideo);
                }
            }
            logger.info(PlaybackMonkey, "PBM run number: {}. Video sent: {}. Failed videos: {}.", currentTry, videosToTest.size(), failedVideos.size());
            if (failedVideos.size() == 0)
                break;
            videosToTest = failedVideos;
        }
        return playBackMonkeyResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.videometadata.hollow.publish.workflow.playbackmonkey.DataTester
     * #testVideoCountryKeys(java.util.List)
     */
    public Map<ValuableVideo, Boolean> testVideoCountryKeys(TaggingLogger logger, List<ValuableVideo> keys) throws Exception {
        final String[] testIds = new String[keys.size()];
        final boolean testCompleted[] = new boolean[keys.size()];
        final boolean testSuccess[] = new boolean[keys.size()];

        SimultaneousExecutor executor = new SimultaneousExecutor(getClass(), "workflow-playbackmonkey-testkeys");
        final int numThreads = executor.getCorePoolSize();

        logger.info(PlaybackMonkey, "keys.size(): {} ; testIds.length: {}", keys.size(), testIds.length);
        for (int i = 0; i < numThreads; i++) {
            final int threadNumber = i;
            executor.execute(new Runnable() {
                public void run() {
                    // LOGGER.logf(ErrorCode.PlayBackMonkeyInfo,"threadNumber: %d keys.size(): %d ; testIds.length: %d ",
                    // threadNumber, keys.size(), testIds.length);
                    for (int i = threadNumber; i < keys.size(); i += numThreads) {
                        ValuableVideo key = keys.get(i);
                        try {
                            testIds[i] = initiateTest(key);
                            // System.out.println("Initiated test for : "+key);
                        } catch (Exception e) {
                            logger.warn(PlaybackMonkey, "Playback monkey test failed", e);
                        }
                    }
                }

            });
        }

        executor.awaitSuccessfulCompletion();

        logger.info(PlaybackMonkey, "Initiated {} number of video country tests.", keys.size());

        executor = new SimultaneousExecutor(getClass(), "testVideoCountryKeys");
        
        // TODO: make timeout a fast property
        long timeToQuit = System.currentTimeMillis() + 1 * 60 * 1000; 

        for (int i = 0; i < numThreads; i++) {
            final int threadNumber = i;
            executor.execute(new Runnable() {
                public void run() {
                    boolean allComplete = false;
                    while(!allComplete) { // retry
                    	// Quit retrying if we have already tried for enough time. 
                    	// This will protect against infinite retries. 
                    	if(System.currentTimeMillis() >= timeToQuit)
                    		break;
                        allComplete = true;
                        for(int i=threadNumber;i<keys.size();i += numThreads) {
                            if(!testCompleted[i]) {
                                try {
                                	//LOGGER.logf(ErrorCode.PlayBackMonkeyInfo,"threadNumber: %d keys.size(): %d ; testIds.length: %d ", threadNumber, keys.size(), testIds.length);
                                    PlaybackMonkeyTestResults results = getTestResults(testIds[i]);
                                    Status status = results.getStatus();
                                    switch(status){
									case COMPLETE:
										// Test on PBM completed successfully. No retry needed.
										if (!results.isSuccess()) {
											// Playback failed
											testCompleted[i] = true;
										} else {
											// Playback passed
											testCompleted[i] = true;
											testSuccess[i] = true;
										}
										break;
									case PENDING:
										// Retry this pbm id again for result
										allComplete = false;
										break;
									case EXPIRED:
									case FAILED:
										// For some reason pbm failed processing this id or was dropped from queue.
										// So quit and leave the id marked failed. No retry needed.
										testCompleted[i] = true;
										break;
                                    }
                                } catch(Exception e) {
                                    logger.warn(PlaybackMonkey, "Exception running PBM tests.", e);
                                }
                            }
                        }
                    }
                }
            });
        }

        executor.awaitSuccessfulCompletion();

        Map<ValuableVideo, Boolean> testResults = new HashMap<ValuableVideo, Boolean>();

        for (int i = 0; i < keys.size(); i++)
            testResults.put(keys.get(i), testSuccess[i] ? Boolean.TRUE : Boolean.FALSE);

        // System.out.println("Completed PlaybackMonkeyTester with testResults size "+testResults.size());
        logger.info(PlaybackMonkey, "Completed PlaybackMonkeyTester with testResults size {}", testResults.size());
        return testResults;
    }

    public String getInstanceInPlayBackMonkeyStack() throws Exception {
        String json = getResponseJson(pbmRestClient, new URI(INSTANCE_LIST_URL), null);
        return json.substring(json.indexOf('[') + 1, json.lastIndexOf(']')).trim().replaceAll("\"", "");
    }

    private String initiateTest(ValuableVideo valuableVideo) throws Exception {
        String url = INITATE_TEST_URL + valuableVideo.getVideoId() + "?countryList=" + valuableVideo.getCountry();
        if(passDownloadFlag.get()) {
        	url += "&testForDownload=" + valuableVideo.isAvailableForDownload();
        }
        String json = getResponseJson(pbmRestClient, new URI(url), null);
        json = json.substring(json.indexOf('[') + 1, json.lastIndexOf(']'));
        VideoTestDetails test = (VideoTestDetails) ParseUtil.deserialize(json, VideoTestDetails.class);
        return test.getId();
    }

    private PlaybackMonkeyTestResults getTestResults(String testId) throws Exception {
        String json = getResponseJson(pbmRestClient, new URI(TEST_RESULT_URL + testId), null);
        PlaybackMonkeyTestResults result = (PlaybackMonkeyTestResults) ParseUtil.deserialize(json, PlaybackMonkeyTestResults.class);
        return result;
    }

    @SuppressWarnings("deprecation")
    private RestClient createClient() {
        RestClientManager restClientManager = RestClientManager.getInstance();
        RestClient pbmRestClient = restClientManager.getClient(PBM_REST_CLIENT_NAME);
        if (pbmRestClient == null) {
            try {
                pbmRestClient = com.netflix.niws.client.RestClientFactory.registerRestClientUsingProperties(PBM_REST_CLIENT_NAME);
            } catch (NIWSClientException e) {
                e.printStackTrace();
            }
        }
        return pbmRestClient;
    }

    public static String getResponseJson(final RestClient client, final URI uri, final NFMultivaluedMap<String, String> params) throws EntityNotFoundException, JsonProcessingException, IllegalArgumentException, IOException, NIWSClientException {
        IClientResponse response = null;
        try {
            response = client.execute(Verb.GET, uri, null, params, null, null, null);
            final int statusCode = response.getStatus();
            DynamicCounter.increment("vms.restclientutil.response", "statusCode", String.valueOf(statusCode), "clientname", client.getRestClientName());
            if (statusCode == 200) {
                return response.getEntity(String.class);
            } else if (statusCode == 204) {
                throw new IOException("204 returned. Entity is unchanged");
            } else if (statusCode == 503) {
                throw new IOException("503 returned. system under maintanence");
            } else {
                throw new RuntimeException("HTTP request error. Status:" + response.getStatus() + " Entity:" + response.getEntity(String.class));
            }
        } finally {
            if (response != null) {
                response.releaseResources();
            }
        }
    }
}
