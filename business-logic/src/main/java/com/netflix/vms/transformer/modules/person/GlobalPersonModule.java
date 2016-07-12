package com.netflix.vms.transformer.modules.person;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ExplicitDateHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.ListOfVideoIdsHollow;
import com.netflix.vms.transformer.hollowinput.PersonBioHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoAliasIdHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoAliasIdsListHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoRoleHollow;
import com.netflix.vms.transformer.hollowinput.PersonVideoRolesListHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoIdHollow;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GlobalPersonModule extends AbstractTransformModule {

    private final HollowPrimaryKeyIndex personBioIndex;

    public GlobalPersonModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);

        this.personBioIndex = indexer.getPrimaryKeyIndex(IndexSpec.PERSON_BIO);
    }

    @Override
    public void transform() {
        transformPersons();
    }

    public List<GlobalPerson> transformPersons() {
    	/// short-circuit Fastlane
    	if(ctx.getFastlaneIds() != null)
    		return Collections.emptyList();
    	
        List<GlobalPerson> personList = new ArrayList<GlobalPerson>();

        for (PersonVideoHollow input : api.getAllPersonVideoHollow()) {
            GlobalPerson output = new GlobalPerson();
            output.id = (int) input._getPersonId();

            VPerson person = new VPerson(output.id);
            output.aliasesIds = getAliasIds(input._getAliasIds());
            output.personRoles = getPersonRoles(person, input._getRoles());

            int personBioOrdinal = personBioIndex.getMatchingOrdinal(input._getPersonId());
            if (personBioOrdinal != -1) {
                PersonBioHollow personBioInput = api.getPersonBioHollow(personBioOrdinal);
                output.spouses = stringsList(personBioInput._getSpouses());
                output.partners = stringsList(personBioInput._getPartners());
                output.birthDate = birthDate(personBioInput._getBirthDate());
                output.topVideos = videoList(personBioInput._getMovieIds());
                StringHollow currentRelationship = personBioInput._getCurrentRelationship();
                if(currentRelationship != null) {
                    output.currentRelationship = new Strings(currentRelationship._getValue());
                }
                ListOfStringHollow relationships = personBioInput._getRelationships();
                if(relationships != null) {
                    output.relationships = stringsList(relationships);
                }
            }

            mapper.addObject(output);
            personList.add(output);
        }

        return personList;
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

    private List<PersonRole> getPersonRoles(VPerson person, PersonVideoRolesListHollow roleList) {
        if (roleList == null || roleList.isEmpty()) return Collections.emptyList();

        List<PersonRole> result = new ArrayList<>();
        Iterator<PersonVideoRoleHollow> iter = roleList.iterator();
        while (iter.hasNext()) {
            PersonVideoRoleHollow item = iter.next();
            PersonRole output = new PersonRole();

            output.person = person;
            output.roleType = new VRole(item._getRoleTypeId());
            output.video = new Video((int) item._getVideoId());
            output.weight = item._getSequenceNumber();

            // @TODO: Zeno/Hollow should have a constant defined
            if (output.weight == java.lang.Integer.MIN_VALUE) {
                output.weight = 0;
            }

            result.add(output);
        }
        return result;
    }

    private List<Integer> getAliasIds(PersonVideoAliasIdsListHollow aliasList) {
        if (aliasList == null || aliasList.isEmpty()) return Collections.emptyList();

        List<Integer> result = new ArrayList<>();
        Iterator<PersonVideoAliasIdHollow> iter = aliasList.iterator();
        while (iter.hasNext()) {
            PersonVideoAliasIdHollow item = iter.next();

            result.add(new Integer(item._getValue()));
        }

        return result;
    }
}
