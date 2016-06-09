package com.netflix.vms.transformer.rest.blobinfo;

import com.google.inject.Singleton;

import java.io.IOException;
import com.netflix.vms.transformer.fastproperties.ClientPinningUtil;
import com.google.common.base.Strings;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Singleton
@Path("/vms/pinclients")
public class VMSPinClientsAdmin {

    @GET
    @Produces({ "text/plain", "text/html" })
    public String pinClients(@QueryParam("vip") String vipName, @QueryParam("version") String blobVersion, @QueryParam("region") String regionStr) throws IOException {

        if(!Strings.isNullOrEmpty(regionStr)) {
            if(regionStr.equals("all")) {
                for(RegionEnum region : VMSBlobInfoAdmin.regions) {
                    ClientPinningUtil.pinClients(vipName, blobVersion, region);
                    ///getPinEventRecorder().postPinEvent(vipName, blobVersion, RegionEnum.toEnum(region.toString()));
                }
            } else {
                ClientPinningUtil.pinClients(vipName, blobVersion, RegionEnum.toEnum(regionStr));
                ///getPinEventRecorder().postPinEvent(vipName, blobVersion, RegionEnum.toEnum(regionStr));
            }
        }


        return "Successfully pinned clients in VIP: " + vipName + " to version: " + blobVersion + " (" + regionStr + " region(s))";
    }
}
