package com.netflix.vms.transformer.input;

import com.netflix.hollow.api.client.HollowBlob;
import com.netflix.vms.transformer.http.HttpHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.jpountz.lz4.LZ4BlockInputStream;
import org.apache.commons.io.IOUtils;

public class VMSInputDataProxyHollowUpdateTransition extends HollowBlob {

    private final String baseProxyURL;
    private final String keybase;
    private final String localDataDir;
    
    public VMSInputDataProxyHollowUpdateTransition(String baseProxyURL, String localDataDir, String keybase, long toVersion) {
        super(toVersion);
        this.baseProxyURL = baseProxyURL;
        this.localDataDir = localDataDir;
        this.keybase = keybase;
    }
    
    public VMSInputDataProxyHollowUpdateTransition(String baseProxyURL, String localDataDir, String keybase, long fromVersion, long toVersion) {
        super(fromVersion, toVersion);
        this.baseProxyURL = baseProxyURL;
        this.localDataDir = localDataDir;
        this.keybase = keybase;
    }

    @Override
    @SuppressWarnings("resource")
    public InputStream getInputStream() throws IOException {
        String url = baseProxyURL + "/filestore-download?keybase=" + keybase + "&version=" + getFileStoreVersion();
        
        if(localDataDir == null)
            return new LZ4BlockInputStream(HttpHelper.getInputStream(url));
            
        
        File localFile = new File(localDataDir, keybase + "_" + getFileStoreVersion());
        
        if(!localFile.exists()) {
            File localIncompleteFile = new File(localDataDir, keybase + "_" + getFileStoreVersion() + ".incomplete");
            
            try (
                    InputStream directInputStream = HttpHelper.getInputStream(url);
                    OutputStream fileOutputStream = new FileOutputStream(localIncompleteFile)
                ) {
                
                IOUtils.copyLarge(directInputStream, fileOutputStream);
            }
            
            localIncompleteFile.renameTo(localFile);
        }
        
        return new LZ4BlockInputStream(new FileInputStream(localFile));
    }
    
    private long getFileStoreVersion() {
        if(isSnapshot() || isReverseDelta())
            return getToVersion();
        else
            return getFromVersion();
    }

}
