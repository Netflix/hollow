package com.netflix.vms.transformer.modules.person;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoPersonAliasHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonAliasListHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonCastHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonCastListHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonHollow;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.PersonRole;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.VRole;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GlobalPersonModule extends AbstractTransformModule {

    public GlobalPersonModule(VMSHollowVideoInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        for (VideoPersonHollow input : api.getAllVideoPersonHollow()) {
            GlobalPerson output = new GlobalPerson();
            output.id = (int) input._getPersonId();

            VPerson person = new VPerson(output.id);
            output.aliasesIds = getAliasIds(input._getAlias());
            output.personRoles = getPersonRoles(person, input._getCast());

            mapper.addObject(output);
        }
    }

    private List<PersonRole> getPersonRoles(VPerson person, VideoPersonCastListHollow castList) {
        if (castList == null || castList.isEmpty()) return Collections.emptyList();

        List<PersonRole> result = new ArrayList<>();
        Iterator<VideoPersonCastHollow> iter = castList.iterator();
        while (iter.hasNext()) {
            VideoPersonCastHollow item = iter.next();
            PersonRole output = new PersonRole();

            output.person = person;
            output.roleType = new VRole((int) item._getRoleTypeId());
            output.video = new Video((int) item._getVideoId());
            output.weight = (int) item._getSequenceNumber();

            result.add(output);
        }
        return result;
    }

    private List<Integer> getAliasIds(VideoPersonAliasListHollow aliasList) {
        if (aliasList == null || aliasList.isEmpty()) return Collections.emptyList();

        List<Integer> result = new ArrayList<>();
        Iterator<VideoPersonAliasHollow> iter = aliasList.iterator();
        while (iter.hasNext()) {
            VideoPersonAliasHollow item = iter.next();

            result.add(new Integer((int) item._getAliasId()));
        }

        return result;
    }
}