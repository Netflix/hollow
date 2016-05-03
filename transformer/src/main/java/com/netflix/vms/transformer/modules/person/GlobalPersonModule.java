package com.netflix.vms.transformer.modules.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.*;
import com.netflix.vms.transformer.hollowoutput.BirthDate;
import com.netflix.vms.transformer.hollowoutput.GlobalPerson;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.PersonRole;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.VRole;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

public class GlobalPersonModule extends AbstractTransformModule {

    private final HollowPrimaryKeyIndex personBioIndex;

    public GlobalPersonModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);

        this.personBioIndex = indexer.getPrimaryKeyIndex(IndexSpec.PERSON_BIO);
    }

    @Override
    public void transform() {
        for (VideoPersonHollow input : api.getAllVideoPersonHollow()) {
            GlobalPerson output = new GlobalPerson();
            output.id = (int) input._getPersonId();

            VPerson person = new VPerson(output.id);
            output.aliasesIds = getAliasIds(input._getAlias());
            output.personRoles = getPersonRoles(person, input._getCast());

            int personBioOrdinal = personBioIndex.getMatchingOrdinal(input._getPersonId());
            if (personBioOrdinal != -1) {
                PersonBioHollow personBioInput = api.getPersonBioHollow(personBioOrdinal);
                output.spouses = stringsList(personBioInput._getSpouses());
                output.partners = stringsList(personBioInput._getPartners());
                output.birthDate = birthDate(personBioInput._getBirthDate());
                output.topVideos = videoList(personBioInput._getMovieIds());
            }

            mapper.addObject(output);
        }
    }

    private List<Video> videoList(ListOfVideoIdsHollow videos) {
        List<Video> result = new ArrayList<>();
        for (VideoIdHollow id : videos) {
            result.add(new Video((int) id._getValue()));
        }
        return result;
    }

    private BirthDate birthDate(ExplicitDateHollow date) {
        if (date == null) return null;

        BirthDate bDate = new BirthDate();
        bDate.day = nullableInteger(date._getDayOfMonthBoxed());
        bDate.month = nullableInteger(date._getMonthOfYearBoxed());
        bDate.year = nullableInteger(date._getYearBoxed());
        return bDate;
    }

    private Integer nullableInteger(java.lang.Integer value) {
        if (value == null) return null;
        return new Integer(value);
    }

    private List<Strings> stringsList(ListOfStringHollow listHollow) {
        List<Strings> result = new ArrayList<>();
        for (StringHollow value : listHollow) {
            result.add(new Strings(value._getValue()));
        }
        return result;
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
