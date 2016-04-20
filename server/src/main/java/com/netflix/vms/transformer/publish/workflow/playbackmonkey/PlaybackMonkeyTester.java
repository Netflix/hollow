package com.netflix.vms.transformer.publish.workflow.playbackmonkey;

import com.netflix.hollow.util.SimultaneousExecutor;

import com.netflix.hermes.exception.EntityNotFoundException;
import com.netflix.niws.client.IClientResponse;
import com.netflix.niws.client.NFMultivaluedMap;
import com.netflix.niws.client.NIWSClientException;
import com.netflix.niws.client.RestClient;
import com.netflix.niws.client.RestClient.Verb;
import com.netflix.niws.client.RestClientFactory;
import com.netflix.niws.client.RestClientManager;
import com.netflix.playback.monkey.model.ParseUtil;
import com.netflix.playback.monkey.model.PlaybackMonkeyTestResults;
import com.netflix.playback.monkey.model.PlaybackMonkeyTestResults.Status;
import com.netflix.playback.monkey.model.VideoTestDetails;
import com.netflix.servo.monitor.DynamicCounter;
import com.netflix.videometadata.audit.VMSErrorCode.ErrorCode;
import com.netflix.vms.transformer.TransformerLogger;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider.VideoCountryKey;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.codehaus.jackson.JsonProcessingException;

public class PlaybackMonkeyTester {

    public static final String INITATE_TEST_URL = "/REST/playback/monkey/viewable/";
    public static final String TEST_RESULT_URL = "/REST/playback/monkey/test/";
    public static final String INSTANCE_LIST_URL = "/REST/playback/monkey/system/instances/";

    private static final String PBM_REST_CLIENT_NAME = "vms-pbm-client";

    private final RestClient pbmRestClient;

    public PlaybackMonkeyTester() {
    	this.pbmRestClient = createClient();
    }

	public Map<VideoCountryKey, Boolean> testVideoCountryKeysWithRetry(TransformerLogger logger, List<VideoCountryKey> keys, int numOfTries) throws Exception {
    	Map<VideoCountryKey, Boolean> playBackMonkeyResult = new HashMap<>(keys.size());
    	for(VideoCountryKey key: keys){
    		playBackMonkeyResult.put(key, false);
    	}

    	int currentTry = 0;
    	List<VideoCountryKey> videosToTest = keys;
    	while (currentTry++ <= numOfTries){
    		Map<VideoCountryKey, Boolean> result = testVideoCountryKeys(logger, videosToTest);

    		List<VideoCountryKey> failedVideos = new ArrayList<VideoCountryKey>(result.size());
			for(Entry<VideoCountryKey, Boolean> entry: result.entrySet()){
	    		if(entry.getValue()){
	    			playBackMonkeyResult.put(entry.getKey(), entry.getValue());
	    		} else{
	    			failedVideos.add(entry.getKey());
	    		}
	    	}
			logger.info("PlaybackMonkeyInfo", "PBM run number: "+currentTry+". Video sent: "+videosToTest.size()+". Failed videos: "+failedVideos.size()+".");
			if(failedVideos.size() == 0)
				break;
			videosToTest = failedVideos;
    	}

    	return playBackMonkeyResult;
    }

    /* (non-Javadoc)
	 * @see com.netflix.videometadata.hollow.publish.workflow.playbackmonkey.DataTester#testVideoCountryKeys(java.util.List)
	 */
	public Map<VideoCountryKey, Boolean> testVideoCountryKeys(TransformerLogger logger, List<VideoCountryKey> keys) throws Exception {
        final String[] testIds = new String[keys.size()];
        final int retries[] = new int[keys.size()];
        final boolean testCompleted[] = new boolean[keys.size()];
        final boolean testSuccess[] = new boolean[keys.size()];

        SimultaneousExecutor executor = new SimultaneousExecutor();
        final int numThreads = executor.getCorePoolSize();

        logger.info("PlaybackMonkeyInfo", "keys.size(): "+keys.size()+" ; testIds.length: "+testIds.length);
        for(int i=0;i<numThreads;i++) {
            final int threadNumber = i;
            executor.execute(new Runnable() {
                public void run() {
                	//LOGGER.logf(ErrorCode.PlayBackMonkeyInfo,"threadNumber: %d keys.size(): %d ; testIds.length: %d ", threadNumber, keys.size(), testIds.length);
                    for(int i=threadNumber;i<keys.size();i += numThreads) {
                        VideoCountryKey key = keys.get(i);
                        try {
                            testIds[i] = initiateTest(key);
                            //System.out.println("Initiated test for : "+key);
                        } catch(Exception e) {
                            logger.error("PlaybackMonkeyError", "Playback monkey test failed", e);
                        }
                    }
                }

            });
        }

        executor.awaitSuccessfulCompletion();

        logger.info("PlaybackMonkeyInfo", "Initiated "+keys.size()+" number of video country tests.");

        executor = new SimultaneousExecutor();

        for(int i=0;i<numThreads;i++) {
            final int threadNumber = i;
            executor.execute(new Runnable() {
                public void run() {
                    boolean allComplete = false;
                    while(!allComplete) {
                        allComplete = true;
                        for(int i=threadNumber;i<keys.size();i += numThreads) {
                            if(!testCompleted[i]) {
                                try {
                                	//LOGGER.logf(ErrorCode.PlayBackMonkeyInfo,"threadNumber: %d keys.size(): %d ; testIds.length: %d ", threadNumber, keys.size(), testIds.length);
                                    PlaybackMonkeyTestResults results = getTestResults(testIds[i]);
                                    if(results.getStatus() != Status.COMPLETE) {
                                        allComplete = false;
                                        //System.out.println("PENDING " + testIds[i]);
                                    } else if(!results.isSuccess()) {
                                        if(++retries[i] > 3) {
                                            testCompleted[i] = true;
                                            //System.out.println("FAILED " + testIds[i]);
                                        } else {
                                            //System.out.println("RETRYING " + i + " (" + retries[i] + ")");
                                            allComplete = false;
                                        }
                                    } else {
                                        //System.out.println("SUCCESS " + testIds[i]);
                                        testCompleted[i] = true;
                                        testSuccess[i] = true;
                                    }
                                } catch(Exception e) {
                                    logger.error("PlaybackMonkeyError", "Could not finish playback monkey test", e);
                                }
                            }
                        }
                    }
                }
            });
        }

        executor.awaitSuccessfulCompletion();

        Map<VideoCountryKey, Boolean> testResults = new HashMap<VideoCountryKey, Boolean>();

        for(int i=0;i<keys.size();i++)
            testResults.put(keys.get(i), testSuccess[i] ? Boolean.TRUE : Boolean.FALSE);

        //System.out.println("Completed PlaybackMonkeyTester with testResults size "+testResults.size());
        logger.info("PlaybackMonkeyInfo", "Completed PlaybackMonkeyTester with testResults size " + testResults.size());
        return testResults;
    }

    public String getInstanceInPlayBackMonkeyStack() throws Exception{
		String json = getResponseJson(pbmRestClient, new URI( INSTANCE_LIST_URL), null);
		//LOGGER.logf(ErrorCode.PlayBackMonkeyInfo, "getInstanceInPlayBackMonkeyStack response: %s.",json);
		return json.substring(json.indexOf('[') + 1, json.lastIndexOf(']')).trim().replaceAll("\"", "");
    }

    private String initiateTest(VideoCountryKey key) throws Exception {
		String url =  INITATE_TEST_URL+ key.getVideoId() + "?countryList=" + key.getCountry();
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
    	RestClient pbmRestClient = RestClientManager.getInstance().getClient(PBM_REST_CLIENT_NAME);
    	if(pbmRestClient == null){
	    	try {
	    		pbmRestClient = RestClientFactory.registerRestClientUsingProperties(PBM_REST_CLIENT_NAME);
	        } catch (NIWSClientException e) {
	            throw new RuntimeException(e);
	        }
    	}
    	return pbmRestClient;
    }

	public static String getResponseJson(final RestClient client,
			final URI uri, final NFMultivaluedMap<String, String> params)
			throws EntityNotFoundException, JsonProcessingException,
			IllegalArgumentException, IOException, NIWSClientException {
		IClientResponse response = null;
		try {
			response = client.execute(Verb.GET, uri, null, params, null, null, null);
			final int statusCode = response.getStatus();
			DynamicCounter.increment("vms.restclientutil.response",
					"statusCode", String.valueOf(statusCode), "clientname",
					client.getRestClientName());
			if (statusCode == 200) {
				return response.getEntity(String.class);
			} else if (statusCode == 204) {
				throw new IOException(
						"204 returned. Entity is unchanged");
			} else if (statusCode == 503) {
				throw new IOException(
						"503 returned. system under maintanence");
			} else {
				throw new RuntimeException("HTTP request error. Status:"
						+ response.getStatus() + " Entity:"
						+ response.getEntity(String.class));
			}
		} finally {
			if (response != null) {
				response.releaseResources();
			}
		}
	}

}
