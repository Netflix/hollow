package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.http.HttpHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class VMSProxyUtil {
    public static final String TEST_PROXY_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-test";
    public static final String PROD_PROXY_URL = "http://discovery.cloud.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-prod";

    public static final String DOWNLOAD_ENDPOINT = "filestore-download";
    public static final String VERSION_ENDPOINT = "filestore-version";

    public static String getProxyURL(boolean isProd) {
        return isProd ? PROD_PROXY_URL : TEST_PROXY_URL;
    }

    public static long getLatestVersion(String proxyURL, String keybase) {
        String proxyUrl = String.format("%s/%s?keybase=%s", proxyURL, VERSION_ENDPOINT, keybase);
        String version = HttpHelper.getStringResponse(proxyUrl);
        return Long.parseLong(version);
    }

    public static long getLatestVersion(boolean isProd, String keybase) {
        return getLatestVersion(getProxyURL(isProd), keybase);
    }

    public static InputStream fetch(String proxyURL, String keybase, String version, boolean isPrintStackTrace) {
        String proxyUrl = null;
        if (version != null) {
            proxyUrl = String.format("%s/%s?keybase=%s&version=%s", proxyURL, DOWNLOAD_ENDPOINT, keybase, version);
        } else {
            proxyUrl = String.format("%s/%s?keybase=%s", proxyURL, DOWNLOAD_ENDPOINT, keybase);
        }
        return HttpHelper.getInputStream(proxyUrl, isPrintStackTrace);
    }

    public static InputStream fetch(boolean isProd, String keybase, String version, boolean isPrintStackTrace) {
        return fetch(getProxyURL(isProd), keybase, version, isPrintStackTrace);
    }

    public static boolean download(String proxyURL, String keybase, String version, File downloadToFile, boolean isThrowExceptionOnFailure) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = fetch(proxyURL, keybase, version, isThrowExceptionOnFailure);
            os = new FileOutputStream(downloadToFile);
            IOUtils.copy(is, os);
            return true;
        } catch (Exception e) {
            if (isThrowExceptionOnFailure) throw new RuntimeException("Unable to download file " + downloadToFile.getAbsolutePath(), e);
            return false;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    public static boolean download(boolean isProd, String keybase, String version, File downloadToFile, boolean isThrowExceptionOnFailure) {
        return download(getProxyURL(isProd), keybase, version, downloadToFile, isThrowExceptionOnFailure);
    }
}
