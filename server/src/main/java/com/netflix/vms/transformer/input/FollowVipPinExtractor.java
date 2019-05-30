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
                String converterVip = FileStoreUtil.getConverterVip(latestItem);
                if(!ctx.getConfig().getConverterVip().equals(converterVip)) {
                    ctx.getLogger().warn(FollowVip, "Could not follow VIP " + followVip
                            + ": not using same converter VIP (I'm using " + ctx.getConfig().getConverterVip()
                            + " and it's using " + converterVip + ")");
                    return null;
                }
                
                Map<UpstreamDatasetHolder.Dataset, Long> inputVersions = new EnumMap<>(UpstreamDatasetHolder.Dataset.class);
                UpstreamDatasetConfig.getNamespaces().keySet().forEach(dataset -> {
                    Long inputVersion = FileStoreUtil.getInputVersion(latestItem, dataset);
                    if (inputVersion != null)
                        inputVersions.put(dataset, inputVersion);
                    else
                        ctx.getLogger().error(FollowVip, "Could not find input version for dataset={} in followVip={}."
                                + " Continuing with remaining inputs.", dataset, followVip);
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
                    ctx.getLogger().warn(FollowVip, "Could not determine pin values from " + followVip);
                }
            } else {
                ctx.getLogger().warn(FollowVip, "Could not follow VIP " + followVip + ": no existing data discovered!");
            }
        }
        
        return null;
    }
    
}
