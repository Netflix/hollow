package com.netflix.vms.transformer.modules.meta;

import static com.netflix.hollow.read.iterator.HollowOrdinalIterator.NO_MORE_ORDINALS;
import static com.netflix.vms.transformer.index.IndexSpec.PERSONS_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.PERSON_ROLES_BY_VIDEO_ID;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_DATE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_GENERAL;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_RIGHTS;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE;
import static com.netflix.vms.transformer.index.IndexSpec.VIDEO_TYPE_COUNTRY;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoDateWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonCastHollow;
import com.netflix.vms.transformer.hollowinput.VideoPersonHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowinput.VideoTypeDescriptorHollow;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.VideoMetaData;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoMetaDataModule {

    private static final int ACTOR_ROLE_ID = 103;
    private static final int DIRECTOR_ROLE_ID = 104;
    private static final int CREATOR_ROLE_ID = 108;

    private final VideoSetType PAST = new VideoSetType("Past");
    private final VideoSetType PRESENT = new VideoSetType("Present");
    private final VideoSetType FUTURE = new VideoSetType("Future");
    private final VideoSetType CANON = new VideoSetType("Canon");
    private final VideoSetType EXTENDED = new VideoSetType("Extended");

    private final VMSHollowVideoInputAPI api;

    private final HollowPrimaryKeyIndex videoGeneralIdx;
    private final HollowPrimaryKeyIndex videoTypeIdx;
    private final HollowHashIndex videoTypeCountryIdx;
    private final HollowHashIndex videoDateIdx;
    private final HollowHashIndex videoPersonIdx;
    private final HollowHashIndex videoPersonRoleIdx;
    private final HollowPrimaryKeyIndex videoRightsIdx;

    private final Map<Integer, List<VPerson>> actorLists = new HashMap<Integer, List<VPerson>>();
    private final Map<Integer, List<VPerson>> directorLists = new HashMap<Integer, List<VPerson>>();
    private final Map<Integer, List<VPerson>> creatorLists = new HashMap<Integer, List<VPerson>>();

    public VideoMetaDataModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(VIDEO_GENERAL);
        this.videoTypeIdx = indexer.getPrimaryKeyIndex(VIDEO_TYPE);
        this.videoPersonIdx = indexer.getHashIndex(PERSONS_BY_VIDEO_ID);
        this.videoPersonRoleIdx = indexer.getHashIndex(PERSON_ROLES_BY_VIDEO_ID);
        this.videoDateIdx = indexer.getHashIndex(VIDEO_DATE);
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(VIDEO_RIGHTS);
        this.videoTypeCountryIdx = indexer.getHashIndex(VIDEO_TYPE_COUNTRY);
    }

    public Map<String, Map<Integer, VideoMetaData>> buildVideoMetaDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry) {
        Map<String, Map<Integer, VideoMetaData>> allVideoMetaDataMap = new HashMap<String, Map<Integer,VideoMetaData>>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            Map<Integer, VideoMetaData> countryMap = new HashMap<Integer, VideoMetaData>();
            allVideoMetaDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            convert(hierarchy.getTopNodeId(), countryCode, countryMap);

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    convert(hierarchy.getEpisodeIds()[i][j], countryCode, countryMap);
                }

                convert(hierarchy.getSeasonIds()[i], countryCode, countryMap);
            }

            for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap);
            }
        }

        return allVideoMetaDataMap;
    }

    private void convert(Integer videoId, String countryCode, Map<Integer, VideoMetaData> countryMap) {
        VideoMetaData vmd = new VideoMetaData();

        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal((long)videoId, countryCode);
        VideoRightsHollow rights = null;
        if(rightsOrdinal != -1) {
            rights = api.getVideoRightsHollow(rightsOrdinal);
            vmd.isSearchOnly = rights._getFlags()._getSearchOnly();
        }


        int typeOrdinal = videoTypeIdx.getMatchingOrdinal((long)videoId);
        if(typeOrdinal != -1)
            vmd.isTV = api.getVideoTypeTypeAPI().getIsTV(typeOrdinal);

        populateSetTypes(videoId, countryCode, rights, vmd);
        populateGeneral(videoId, vmd);
        populateRights(videoId, countryCode, vmd);
        populateDates(videoId, countryCode, vmd);
        populateCastLists(videoId, vmd);

        countryMap.put(videoId, vmd);
    }

    private void populateSetTypes(Integer videoId, String countryCode, VideoRightsHollow rights, VideoMetaData vmd) {
        HollowHashIndexResult videoTypeMatches = videoTypeCountryIdx.findMatches((long)videoId, countryCode);
        VideoTypeDescriptorHollow typeDescriptor = null;
        if(videoTypeMatches != null)
            typeDescriptor = api.getVideoTypeDescriptorHollow(videoTypeMatches.iterator().next());


        boolean isInWindow = false;
        boolean isInFuture = false;
        boolean isExtended = false;
        boolean isCanon = false;

        if(rights != null) {
            Set<VideoRightsWindowHollow> windows = rights._getRights()._getWindows();
            for(VideoRightsWindowHollow window : windows) {
                long windowStart = window._getStartDate()._getValue();
                if(windowStart < System.currentTimeMillis() && window._getEndDate()._getValue() > System.currentTimeMillis()) {
                    isInWindow = true;
                    break;
                } else if(windowStart > System.currentTimeMillis()) {
                    isInFuture = true;
                }
            }
        }

        if(typeDescriptor != null) {
            isExtended = typeDescriptor._getIsExtended();
            isCanon = typeDescriptor._getIsCanon();
        }

        Set<VideoSetType> setOfVideoSetType = new HashSet<VideoSetType>();

        if(isInWindow) {
            setOfVideoSetType.add(PRESENT);
        } else if(isInFuture) {
            setOfVideoSetType.add(FUTURE);
        } else if(isExtended) {
            setOfVideoSetType.add(EXTENDED);
        }

        if(isCanon)
            setOfVideoSetType.add(CANON);

        if(setOfVideoSetType.isEmpty())
            setOfVideoSetType.add(PAST);

        vmd.videoSetTypes = setOfVideoSetType;
    }

    private void populateGeneral(Integer videoId, VideoMetaData vmd) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal((long)videoId);
        if(ordinal != -1) {
            VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);

            StringHollow origCountry = general._getOriginCountryCode();
            if(origCountry != null)
                vmd.countryOfOrigin = new ISOCountry(origCountry._getValue());
            vmd.countryOfOriginNameLocale = new NFLocale(general._getCountryOfOriginNameLocale()._getValue());
            StringHollow origLang = general._getOriginalLanguageBcpCode();
            if(origLang != null)
                vmd.originalLanguageBcp47code = new Strings(origLang._getValue());
        }
    }

    private void populateRights(Integer videoId, String countryCode, VideoMetaData vmd) {
    }

    private void populateDates(Integer videoId, String countryCode, VideoMetaData vmd) {
        HollowHashIndexResult dateResult = videoDateIdx.findMatches((long)videoId, countryCode);

        if(dateResult != null) {
            int ordinal = dateResult.iterator().next();
            VideoDateWindowHollow dateWindow = api.getVideoDateWindowHollow(ordinal);

            vmd.isTheatricalRelease = dateWindow._getIsTheatricalRelease();
            vmd.year = dateWindow._getTheatricalReleaseYear();
            if(dateWindow._getTheatricalReleaseDate() != Long.MIN_VALUE)
                vmd.theatricalReleaseDate = new Date(dateWindow._getTheatricalReleaseDate());
            vmd.latestYear = vmd.year;
        } else {
            vmd.year = 0;
            vmd.latestYear = 0;
        }
    }

    private void populateCastLists(Integer videoId, VideoMetaData vmd) {
        List<VPerson> actorList = this.actorLists.get(videoId);

        if(actorList == null) {
            actorList = new ArrayList<VPerson>();
            List<VPerson> directorList = new ArrayList<VPerson>();
            List<VPerson> creatorList = new ArrayList<VPerson>();

            HollowHashIndexResult personMatches = videoPersonIdx.findMatches((long)videoId);
            if(personMatches != null) {
                HollowOrdinalIterator iter = personMatches.iterator();

                int personOrdinal = iter.next();
                while(personOrdinal != NO_MORE_ORDINALS) {
                    VideoPersonHollow person = api.getVideoPersonHollow(personOrdinal);

                    long personId = person._getPersonId();

                    HollowHashIndexResult roleMatches = videoPersonRoleIdx.findMatches(personId, (long)videoId);
                    HollowOrdinalIterator roleIter = roleMatches.iterator();

                    int roleOrdinal = roleIter.next();
                    while(roleOrdinal != NO_MORE_ORDINALS) {
                        VideoPersonCastHollow role = api.getVideoPersonCastHollow(roleOrdinal);

                        if(role._getRoleTypeId() == ACTOR_ROLE_ID) {
                            actorList.add(new VPerson((int)personId));
                        } else if(role._getRoleTypeId() == DIRECTOR_ROLE_ID) {
                            directorList.add(new VPerson((int)personId));
                        } else if(role._getRoleTypeId() == CREATOR_ROLE_ID) {
                            creatorList.add(new VPerson((int)personId));
                        }

                        roleOrdinal = roleIter.next();
                    }

                    personOrdinal = iter.next();
                }
            }

            this.actorLists.put(videoId, actorList);
            this.directorLists.put(videoId, directorList);
            this.creatorLists.put(videoId, creatorList);
        }

        vmd.actorList = actorList;
        vmd.directorList = directorLists.get(videoId);
        vmd.creatorList = creatorLists.get(videoId);
    }

}
