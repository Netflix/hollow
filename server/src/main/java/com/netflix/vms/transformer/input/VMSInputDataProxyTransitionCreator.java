package com.netflix.vms.transformer.input;

import com.netflix.hollow.client.HollowTransitionCreator;
import com.netflix.hollow.client.HollowUpdateTransition;
import com.netflix.vms.transformer.http.HttpHelper;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VMSInputDataProxyTransitionCreator implements HollowTransitionCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(VMSInputDataProxyTransitionCreator.class);

    private final String baseProxyURL;
    private final String localDataDir;
    private final VMSInputDataKeybaseBuilder keybaseBuilder;

    public VMSInputDataProxyTransitionCreator(String baseProxyURL, String localDataDir, String converterVip) {
        this.baseProxyURL = baseProxyURL;
        this.localDataDir = localDataDir;
        this.keybaseBuilder = new VMSInputDataKeybaseBuilder(converterVip);
    }

    @Override
    public HollowUpdateTransition createSnapshotTransition(long desiredVersion) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date expectedDate = dateFormat.parse(String.valueOf(desiredVersion));
            
            String url = baseProxyURL + "/filestore-version?"
                    + "keybase=" + keybaseBuilder.getSnapshotKeybase() 
                    + "&timestamp=" + expectedDate.getTime()
                    + "&nocache=true";
            String snapshotVersionStr = HttpHelper.getStringResponse(url);
            
            long snapshotVersion = Long.parseLong(snapshotVersionStr);
            
            return new VMSInputDataProxyHollowUpdateTransition(baseProxyURL, localDataDir, keybaseBuilder.getSnapshotKeybase(), snapshotVersion);
        } catch(Exception e) {
            LOGGER.error("Could not retrieve snapshot version from proxy", e);
            return null;
        }
    }

    @Override
    public HollowUpdateTransition createDeltaTransition(long currentVersion) {
        try {
            String url = baseProxyURL + "/filestore-attribute?"
                    + "keybase=" + keybaseBuilder.getDeltaKeybase()
                    + "&version=" + currentVersion;
            
            Properties props = new Properties();
            try(InputStream is = HttpHelper.getInputStream(url)) {
                props.load(is);
            }
            
            long fromVersion = Long.parseLong(props.getProperty("fromVersion"));
            long toVersion = Long.parseLong(props.getProperty("toVersion"));
            
            return new VMSInputDataProxyHollowUpdateTransition(baseProxyURL, localDataDir, keybaseBuilder.getDeltaKeybase(), fromVersion, toVersion);
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public HollowUpdateTransition createReverseDeltaTransition(long currentVersion) {
        return null;
    }

}
