package com.netflix.vms.transformer.modules.mpl;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.DrmSystemIdentifiersHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.DrmSystem;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.HashMap;

public class DrmSystemModule extends AbstractTransformModule {

    private final DrmKeyString HEADER_DATA_AVAILABLE = new DrmKeyString("headerDataAvailable");
    private final DrmKeyString TRUE = new DrmKeyString("true");
    private final DrmKeyString FALSE = new DrmKeyString("false");

    public DrmSystemModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        super(api, mapper);
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
