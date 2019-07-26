package com.netflix.vms.transformer.rest;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.input.FileStoreUtil;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vms/followvipsameversion")
public class VMSFollowVipSameVersionAdmin {

    private final FileStore fileStore;
    private final TransformerConfig config;

    @Inject
    public VMSFollowVipSameVersionAdmin(FileStore fileStore, TransformerConfig config) {
        this.fileStore = fileStore;
        this.config = config;
    }

    @GET
    @Produces({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    public Response findSameVersions(@QueryParam("fromVip") String fromVip, @QueryParam("toVip") String toVip, @QueryParam("format") String format) throws Exception {
        if(fromVip == null)
            fromVip = config.getFollowVip();
        if(toVip == null)
            toVip = config.getTransformerVip();

        Map<String, Set<Long>> fromVipVersions = getVersionsByInputParameters(fromVip);
        Map<String, Set<Long>> toVipVersions = getVersionsByInputParameters(toVip);

        List<VersionPair> versionPairs = new ArrayList<VersionPair>();

        for (Map.Entry<String, Set<Long>> entry : fromVipVersions.entrySet()) {
            Long fromVipVersion = getLatest(entry.getValue());

            Set<Long> toVipVersionList = toVipVersions.get(entry.getKey());
            if (toVipVersionList != null) {
                for (long toVipVersion : toVipVersionList) {
                    versionPairs.add(new VersionPair(fromVipVersion, toVipVersion));
                }
            }
        }

        Collections.sort(versionPairs);

        String openResponse = "";
        String dataFormat = "%s:%s=%s:%s";
        String lineBreak = "\n";
        String closeResponse = "";

        boolean isHTML = false;
        String mediaType = MediaType.TEXT_PLAIN;
        if ("json".equals(format)) {
            openResponse = "[";
            dataFormat = "{\"%s\":%s, \"%s\":%s}";
            closeResponse = "]";
            lineBreak = ",\n";
            mediaType = MediaType.APPLICATION_JSON;
        } else if ("html".equals(format)) {
            isHTML = true;
            openResponse = "<html><head></head><body>";
            dataFormat = "[%s:%s, %s:%s] - %s";
            closeResponse = "</body><html>";
            lineBreak = "<br>";
            mediaType = MediaType.TEXT_HTML;
        }


        StringBuilder response = new StringBuilder(openResponse);
        for(int i=0;i<versionPairs.size();i++) {
            if (i > 0) response.append(lineBreak);

            long fromVer = versionPairs.get(i).getFromVersion();
            long toVer = versionPairs.get(i).getToVersion();
            String dataEntry = isHTML ? String.format(dataFormat, fromVip, fromVer, toVip, toVer, createDiffLink(fromVip, fromVer, toVip, toVer)) : String.format(dataFormat, fromVip, fromVer, toVip, toVer);
            response.append(dataEntry);
        }
        response.append(closeResponse);

        return Response.ok(response.toString(), mediaType).build();
    }

    private final Map<String, Set<Long>> getVersionsByInputParameters(String vip) throws Exception {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(vip);

        Map<String, Set<Long>> map = new HashMap<>();
        addVersionsByKeybase(map, keybaseBuilder.getSnapshotKeybase());
        addVersionsByKeybase(map, keybaseBuilder.getDeltaKeybase());

        return map;
    }

    private final Long getLatest(Set<Long> versions) {
        Long result = null;

        for (Long v : versions) {
            if (result == null || result.longValue() < v.longValue()) {
                result = v;
            }
        }

        return result;
    }

    // Need to support multiple outputs using same inputVersion+cycleDataTimestamp
    private void addVersionsByKeybase(Map<String, Set<Long>> map, String snapshotKeybase) {
        List<FileAccessItem> allVersionItems = fileStore.getAllFileAccessItems(snapshotKeybase);

        for(FileAccessItem item : allVersionItems) {
            Long outputVersion = FileStoreUtil.getToVersion(item);
            Optional<Long> converterInputVersion = FileStoreUtil.getInputVersion(item, CONVERTER);
            if (!converterInputVersion.isPresent())
                converterInputVersion = Optional.of(Long.MIN_VALUE);
            Long cycleDataTimestamp = FileStoreUtil.getPublishCycleDataTS(item);

            String key = converterInputVersion.get() + "_" + cycleDataTimestamp;
            Set<Long> outputVersionSet = map.get(key);
            if (outputVersionSet == null) {
                outputVersionSet = new HashSet<>();
                map.put(key, outputVersionSet);
            }
            outputVersionSet.add(outputVersion);
        }
    }

    private String createDiffLink(String primaryVip, long primaryCycleId, String secondaryVip, long secondaryCycleId) {
        String env = config.getNetflixEnvironment();
        String diffName = String.format("dataio:(%s:%s::%s:%s)", primaryVip, primaryCycleId, secondaryVip, secondaryCycleId);
        String url = String.format("http://go/vmsdiff-%s?action=submit&diffName=%s&fromVip=%s&fromVersion=%s&toVip=%s&toVersion=%s",
                env, diffName, primaryVip, primaryCycleId, secondaryVip, secondaryCycleId);

        return String.format("[ <a href=\"%s\" target=\"_blank\">diff</a> ]", url);
    }

    private static class VersionPair implements Comparable<VersionPair> {
        private final long fromVersion;
        private final long toVersion;

        public VersionPair(long fromVersion, long toVersion) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
        }

        public long getFromVersion() {
            return fromVersion;
        }

        public long getToVersion() {
            return toVersion;
        }

        @Override
        public int compareTo(VersionPair other) {
            int result = Long.compare(other.fromVersion, fromVersion);
            if (result != 0) return result;

            return Long.compare(other.toVersion, toVersion);
        }
    }

}
