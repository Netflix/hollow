package com.netflix.vms.transformer.testutil.migration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    public static String getStringResponse(String url) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(url)));

        StringBuilder builder = new StringBuilder();

        try {
            String line = reader.readLine();
            while(line != null) {
                builder.append(line);
                line = reader.readLine();
                if(line != null)
                    builder.append("\n");
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }

        return builder.toString();
    }


    public static InputStream getInputStream(String url) {
        Throwable thrownException = null;

        for(int i=0;i<3;i++) {
            try {
                URL urlObj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

                int responseCode = conn.getResponseCode();

                while(responseCode != HttpURLConnection.HTTP_OK) {
                    switch(responseCode) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                    case HttpURLConnection.HTTP_SEE_OTHER:
                        urlObj = new URL(conn.getHeaderField("Location"));
                        conn = (HttpURLConnection) urlObj.openConnection();
                        responseCode = conn.getResponseCode();
                    default:
                        throw new Exception("Received response " + responseCode + " from proxy server");
                    }
                }

                return conn.getInputStream();

            } catch (Throwable th) {
                thrownException = th;
                th.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        }

        throw new RuntimeException(thrownException);
    }

}
