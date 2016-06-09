
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.HollowBlobHeader;
import com.netflix.hollow.read.engine.HollowBlobHeaderReader;
import com.netflix.videometadata.client.VMSQuickTestInitializer;
import com.netflix.videometadata.util.HttpHelper;
import com.netflix.vms.transformer.fastproperties.PersistedPropertiesUtil;
import com.netflix.vms.transformer.input.VMSInputDataKeybaseBuilder;
import com.netflix.vms.transformer.io.LZ4VMSInputStream;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * The two tests here must be triggered sequentially, but with a gap of about 30 minutes between them.
 * 
 * Test 1) downloadOldPipelineOutputAndPinNewPipeline().
 * 
 * This will download the latest output from the noevent legacy pipeline cluster, then pin 
 * for the noevent vmsconverter in PROD to the corresponding coldstart versions.
 * 
 * ----------------
 * After Test 1, we need to wait for the newly pinned coldstarts to make their way through the vmsconverter
 * and vmstransformer.  30 minutes should be sufficient time.
 * ----------------
 * 
 * Test 2) downloadNewPipelineOutputAndInputAndUnpinNewPipeline()
 * 
 * This will download the latest output from the newnoevent vmstransformer, ensuring that
 * the coldstart versions match the desired based on the legacy output.  Then, it will
 * trigger a diff on go/vmsdiff-prod between these versions.  Then, it will download
 * the input blob which was used to produce that data.  Then, it will unpin the noevent vmsconverter in PROD.
 * 
 *
 */
public class RollingDiff {

    private static String WORKING_DIR = "/space/transformer-data";
    
    @Test
    public void downloadOldPipelineOutputAndPinNewPipeline() throws IOException {
        downloadOldPipelineOutput();
        pinNewPipeline();
    }
    
    @Test
    public void downloadNewPipelineOutputAndUnpinNewPipeline() throws IOException {
        downloadNewPipelineOutput();
        triggerDiff();
        //downloadNewPipelineInput();
        unpinNewPipeline();
    }
    
    private void downloadOldPipelineOutput() throws IOException {
        String latestNoeventVersion = HttpHelper.getStringResponse(VMSQuickTestInitializer.PROD_URL + "/" + "filestore-version?keybase=" + new HollowBlobKeybaseBuilder("noevent").getSnapshotKeybase());
        
        File dir = new File(WORKING_DIR, "rolling-diff");
        dir.mkdir();
        
        InputStream is = HttpHelper.getInputStream(VMSQuickTestInitializer.PROD_URL + "/" + "filestore-download?keybase=" + new HollowBlobKeybaseBuilder("noevent").getSnapshotKeybase() + "&version=" + latestNoeventVersion);
        FileOutputStream fos = new FileOutputStream(new File(dir, "oldpipeline-snapshot"));
        
        IOUtils.copyLarge(is, fos);
        
        is.close();
        fos.close();
    }
    
    private void pinNewPipeline() throws IOException {
        HollowBlobHeaderReader reader = new HollowBlobHeaderReader();

        File dir = new File(WORKING_DIR, "rolling-diff");

        HollowBlobHeader header = reader.readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "oldpipeline-snapshot"))));
        
        for(Map.Entry<String, String> headerEntry : header.getHeaderTags().entrySet()) {
            if(headerEntry.getKey().endsWith("_ColdStartManager")) {
                String mutationGroup = headerEntry.getKey().substring(6, headerEntry.getKey().indexOf("_ColdStartManager"));
                String latestColdstartVersion = headerEntry.getValue().substring(8);
                
                System.out.println("pinning " + mutationGroup + "=" + latestColdstartVersion);
                
                PersistedPropertiesUtil.updateFastProperty(
                        "vms.pinnedColdstartVersion." + mutationGroup, 
                        latestColdstartVersion, 
                        "vmsconverter", 
                        EnvironmentEnum.prod, 
                        RegionEnum.US_EAST_1, 
                        null, 
                        "noevent", 
                        null);
            }
        }
    }
    
    private void downloadNewPipelineOutput() throws IOException {
        String latestNewNoeventVersion = HttpHelper.getStringResponse(VMSQuickTestInitializer.PROD_URL + "/" + "filestore-version?keybase=" + new HollowBlobKeybaseBuilder("newnoevent").getSnapshotKeybase());
        
        File dir = new File(WORKING_DIR, "rolling-diff");
        dir.mkdir();
        
        InputStream is = HttpHelper.getInputStream(VMSQuickTestInitializer.PROD_URL + "/" + "filestore-download?keybase=" + new HollowBlobKeybaseBuilder("newnoevent").getSnapshotKeybase() + "&version=" + latestNewNoeventVersion);
        FileOutputStream fos = new FileOutputStream(new File(dir, "newpipeline-snapshot"));
        
        IOUtils.copyLarge(is, fos);
        
        is.close();
        fos.close();
        
        Map<String, String> expectedColdstartVersions = new HashMap<>();
        HollowBlobHeader header = new HollowBlobHeaderReader().readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "oldpipeline-snapshot"))));
        for(Map.Entry<String, String> headerEntry : header.getHeaderTags().entrySet()) {
            if(headerEntry.getKey().endsWith("_ColdStartManager")) {
                String mutationGroup = headerEntry.getKey().substring(6, headerEntry.getKey().indexOf("_ColdStartManager"));
                String latestColdstartVersion = headerEntry.getValue().substring(8);
                
                expectedColdstartVersions.put(mutationGroup, latestColdstartVersion);
            }
        }
        
        header = new HollowBlobHeaderReader().readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "newpipeline-snapshot"))));
        for(Map.Entry<String, String> headerEntry : header.getHeaderTags().entrySet()) {
            if(headerEntry.getKey().endsWith("_ColdStartManager")) {
                String mutationGroup = headerEntry.getKey().substring(6, headerEntry.getKey().indexOf("_ColdStartManager"));
                String latestColdstartVersion = headerEntry.getValue().substring(8);
                
                String expectedVersion = expectedColdstartVersions.remove(mutationGroup);
                
                Assert.assertEquals(mutationGroup + " coldstart versions do not match -- you may just need to wait a bit longer after pinning inputs", 
                        expectedVersion, 
                        latestColdstartVersion);
            }
        }
        
        Assert.assertTrue(expectedColdstartVersions.isEmpty());
    }
    
    private void triggerDiff() throws IOException {
        File dir = new File(WORKING_DIR, "rolling-diff");
        HollowBlobHeader header = new HollowBlobHeaderReader().readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "oldpipeline-snapshot"))));
        String oldVersion = header.getHeaderTags().get("dataVersion");
        header = new HollowBlobHeaderReader().readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "newpipeline-snapshot"))));
        String newVersion = header.getHeaderTags().get("dataVersion");
        
        String url = "http://go/vmsdiff-prod?action=submit&User=&Name=ROLLING_DIFF&diffName=_ROLLING_DIFF_" + newVersion + "&fromVip=noevent&fromVersion=" + oldVersion + "&toVip=newnoevent&toVersion=" + newVersion;
        
        HttpHelper.getStringResponse(url);
    }
    
    private void downloadNewPipelineInput() throws IOException {
        File dir = new File(WORKING_DIR, "rolling-diff");
        HollowBlobHeader header = new HollowBlobHeaderReader().readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "newpipeline-snapshot"))));
        String inputVersionId = header.getHeaderTags().get("sourceDataVersion");
        
        InputStream is = HttpHelper.getInputStream(VMSQuickTestInitializer.PROD_URL_US_EAST + "/" + "filestore-download?keybase=" + new VMSInputDataKeybaseBuilder("noevent").getSnapshotKeybase() + "&version=" + inputVersionId);
        FileOutputStream fos = new FileOutputStream(new File(dir, "input-snapshot"));

        IOUtils.copy(is, fos);
        
        is.close();
        fos.close();
    }

    
    @Test
    public void unpinNewPipeline() throws IOException {
        HollowBlobHeaderReader reader = new HollowBlobHeaderReader();

        File dir = new File(WORKING_DIR, "rolling-diff");

        HollowBlobHeader header = reader.readHeader(new LZ4VMSInputStream(new FileInputStream(new File(dir, "oldpipeline-snapshot"))));
        
        for(Map.Entry<String, String> headerEntry : header.getHeaderTags().entrySet()) {
            if(headerEntry.getKey().endsWith("_ColdStartManager")) {
                String mutationGroup = headerEntry.getKey().substring(6, headerEntry.getKey().indexOf("_ColdStartManager"));
                
                PersistedPropertiesUtil.deleteFastProperty(
                        "vms.pinnedColdstartVersion." + mutationGroup, 
                        "vmsconverter", 
                        EnvironmentEnum.prod, 
                        RegionEnum.US_EAST_1, 
                        null, 
                        "noevent", 
                        null);
            }
        }

    }
    
}
