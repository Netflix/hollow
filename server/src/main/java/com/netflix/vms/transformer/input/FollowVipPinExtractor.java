package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.FollowVip;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.UpstreamDatasetConfig;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FollowVipPinExtractor {

    private final FileStore fileStore;
    
    public FollowVipPinExtractor(FileStore fileStore) {
        this.fileStore = fileStore;
    }
    
    public FollowVipPin retrieveFollowVipPin(TransformerContext ctx) {
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
                Map<UpstreamDatasetHolder.Dataset, Long> inputVersions = new EnumMap<>(UpstreamDatasetHolder.Dataset.class);
                UpstreamDatasetConfig.getNamespaces().keySet().forEach(dataset -> {
                    Optional<Long> inputVersion = FileStoreUtil.getInputVersion(latestItem, dataset);
                    if (inputVersion.isPresent())
                        inputVersions.put(dataset, inputVersion.get());
                    else {
                        ctx.getLogger().error(FollowVip, "Could not find input version for dataset={} in followVip={}.",
                                dataset, followVip);
                        throw new RuntimeException("Could not find input version for dataset=" + dataset
                                + " in followVip=" + followVip);
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
                    return new FollowVipPin(followVip, inputVersions, publishCycleDataTS);
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
