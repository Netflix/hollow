package com.netflix.vms.transformer.modules.drmsystem;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.DrmSystemIdentifiersHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.DrmSystem;
import java.util.HashMap;

public class DrmSystemModule {

    private final DrmKeyString HEADER_DATA_AVAILABLE = new DrmKeyString("headerDataAvailable");
    private final DrmKeyString TRUE = new DrmKeyString("true");
    private final DrmKeyString FALSE = new DrmKeyString("false");

    private final VMSHollowVideoInputAPI api;
    private final HollowObjectMapper mapper;

    public DrmSystemModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public void transform() {
        for(DrmSystemIdentifiersHollow drmSystemIdentifier : api.getAllDrmSystemIdentifiersHollow()) {
            DrmSystem system = new DrmSystem();
            system.id = (int)drmSystemIdentifier._getId();
            system.guid = new DrmKeyString(drmSystemIdentifier._getGuid()._getValue());
            system.name = new DrmKeyString(drmSystemIdentifier._getName()._getValue());

            system.attributes = new HashMap<DrmKeyString, DrmKeyString>();

            system.attributes.put(HEADER_DATA_AVAILABLE, drmSystemIdentifier._getHeaderDataAvailable() ? TRUE : FALSE);

            mapper.addObject(system);
        }
    }

}
