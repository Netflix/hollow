package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.FollowVip;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.StaticInputs;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.api.BusinessLogicAPI;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class FollowVipPinExtractor {

    private final FileStore fileStore;

    public FollowVipPinExtractor(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    /**
     * This method is used to pin inputs to the transformer for specific versions. To achieve pinning, set-
     * FP "vms.staticInputVersions" to something like "vmsconverter-muon:20190712163036178;gatekeeper2_status_test:201907121634046"
     */
    public FollowVipPin getStaticInputVersions(TransformerContext ctx) {
        Map<String, Long> inputVersions = new HashMap<>();
        String staticInputVerions = ctx.getConfig().getStaticInputVersions();
        for (String inputPair : staticInputVerions.split(";")) {
            if (inputPair.trim().equals(StringUtils.EMPTY))
                continue;
            String[] parts = inputPair.split(":");
            if (parts.length != 2) {
                String msg = "Invalid static input config: %s" + staticInputVerions;
                ctx.getLogger().error(StaticInputs, msg);
                throw new RuntimeException(msg);
            }
            inputVersions.put(parts[0], Long.valueOf(parts[1]));
        }
        ctx.getLogger().info(StaticInputs, "Static inputs configured to " + staticInputVerions);
        return new FollowVipPin(inputVersions, System.currentTimeMillis());
    }


    public FollowVipPin retrieveFollowVipPin(BusinessLogicAPI businessLogic, TransformerContext ctx) {
        String followVip = ctx.getConfig().getFollowVip();
        if(followVip != null) {
            HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(followVip);
            
            FileAccessItem snapshotItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getSnapshotKeybase());
            FileAccessItem deltaItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getDeltaKeybase());
            
            long snapshotVersion = snapshotItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(snapshotItem);
            long deltaVersion = deltaItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(deltaItem);
            
            FileAccessItem latestItem = snapshotVersion > deltaVersion ? snapshotItem : deltaItem;
            long dataVersion = snapshotVersion > deltaVersion ? snapshotVersion : deltaVersion;
            
            if(latestItem != null) {
                Map<String, Long> inputVersions = new HashMap<>();
                businessLogic.getInputNamespaces().stream().forEach(dataset -> {
                    try {
                        Long inputVersion = FileStoreUtil.getInputVersion(latestItem, dataset);
                        if (inputVersion != null)
                            inputVersions.put(dataset, inputVersion);
                    } catch (Exception ex) {
                        ctx.getLogger().warn(FollowVip, "Could not find input version for namespace={} in followVip={}.",
                                dataset, followVip);
                    }
                });

                String logInputVersions = inputVersions.entrySet().stream()
                        .map(Map.Entry::toString)
                        .collect(Collectors.joining(" "));

                long publishCycleDataTS = FileStoreUtil.getPublishCycleDataTS(latestItem);


                if(!inputVersions.isEmpty() && publishCycleDataTS != Long.MIN_VALUE) {
                    ctx.getLogger().info(FollowVip, "Following VIP " + followVip + " version " + dataVersion
                            + " dataTS: " + publishCycleDataTS
                            + " inputs= (" + logInputVersions + ")");
                    return new FollowVipPin(inputVersions, publishCycleDataTS);
                } else {
                    ctx.getLogger().error(FollowVip, "Could not determine pin values from " + followVip);
                    throw new RuntimeException("Could not determine pin values from " + followVip);
                }
            } else {
                ctx.getLogger().error(FollowVip, "Could not follow VIP " + followVip + ": no existing data discovered!");
                throw new RuntimeException("Could not follow VIP " + followVip + ": no existing data discovered!");
            }
        }

        return null;
    }
    
}
