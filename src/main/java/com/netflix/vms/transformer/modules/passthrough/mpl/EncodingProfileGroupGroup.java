package com.netflix.vms.transformer.modules.passthrough.mpl;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.StreamProfileGroupsHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfileIdHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfileIdListHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.EncodingProfileGroup;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import java.util.Collection;
import java.util.HashSet;

public class EncodingProfileGroupGroup extends AbstractTransformModule {

    public EncodingProfileGroupGroup(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        super(api, mapper);
    }

    @Override
    public void transform() {
        Collection<StreamProfileGroupsHollow> inputs = api.getAllStreamProfileGroupsHollow();
        for (StreamProfileGroupsHollow input : inputs) {
            EncodingProfileGroup profileGroup = new EncodingProfileGroup();
            profileGroup.encodingProfileIds = new HashSet<com.netflix.vms.transformer.hollowoutput.Integer>();
            profileGroup.groupNameStr = input._getGroupName()._getValue().toCharArray();
            StreamProfileIdListHollow profileList = input._getStreamProfileIds();

            for (StreamProfileIdHollow profileId : profileList) {
                profileGroup.encodingProfileIds.add(new com.netflix.vms.transformer.hollowoutput.Integer((int) profileId._getValue()));
            }
            mapper.addObject(profileGroup);
        }

    }

}
