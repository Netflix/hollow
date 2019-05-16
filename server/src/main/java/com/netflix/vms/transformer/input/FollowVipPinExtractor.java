package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.FollowVip;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;

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
                    ctx.getLogger().warn(FollowVip, "Could not follow VIP " + followVip + ": not using same converter VIP (I'm using " + ctx.getConfig().getConverterVip() + " and it's using " + converterVip + ")");
                    return null;
                }
                
                long inputVersion = FileStoreUtil.getInputDataVersion(latestItem);
                long gk2InputVersion = FileStoreUtil.getGk2InputDataVersion(latestItem);
                long publishCycleDataTS = FileStoreUtil.getPublishCycleDataTS(latestItem);
                
                if(inputVersion != Long.MIN_VALUE && publishCycleDataTS != Long.MIN_VALUE) {
                    ctx.getLogger().info(FollowVip, "Following VIP " + followVip + " version " + dataVersion + "(converter: " + converterVip + " inputVersion: " + inputVersion + " dataTS: " + publishCycleDataTS + ")");
                    return new FollowVipPin(followVip, inputVersion, gk2InputVersion, publishCycleDataTS);
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
