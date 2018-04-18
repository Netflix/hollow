package com.netflix.vms.transformer.rest.blobinfo;

import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.vms.transformer.fastproperties.ClientPinningUtil;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Singleton
@Path("/vms/unpinclients")
public class VMSUnpinClientsAdmin {

    @GET
    @Produces({ "text/plain", "text/html" })
    public String unpinClients(@QueryParam("vip") String vipName, @QueryParam("region") String regionStr) throws IOException {

        if(!Strings.isNullOrEmpty(regionStr)) {
            if(regionStr.equals("all")) {
                for(RegionEnum region : VMSBlobInfoAdmin.regions) {
                    ClientPinningUtil.unpinClients(vipName, region);
                    //getPinEventRecorder().postUnpinEvent(vipName, region);
                }
            } else {
                ClientPinningUtil.unpinClients(vipName, RegionEnum.toEnum(regionStr));
                //getPinEventRecorder().postUnpinEvent(vipName, RegionEnum.toEnum(regionStr));
            }
        }

        return "Successfully unpinned clients in VIP: " + vipName + " (" + regionStr + " region(s))";
    }

}
