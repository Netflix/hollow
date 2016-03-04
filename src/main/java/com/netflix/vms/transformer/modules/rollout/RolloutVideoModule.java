package com.netflix.vms.transformer.modules.rollout;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualTrailerHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MultiValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutMapOfLaunchDatesHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseCastHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseCastListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseCharacterHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseCharacterListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseElementsHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseImageIdHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseLocalizedMetadataHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseOldArtworkListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseTrailerHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseTrailerListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseTrailerSupplementalInfoHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowMapHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhasesElementsTrailerSupplementalInfoMapHollow;
import com.netflix.vms.transformer.hollowinput.SingleValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TrailerHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.AvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.Phase;
import com.netflix.vms.transformer.hollowoutput.RolloutCast;
import com.netflix.vms.transformer.hollowoutput.RolloutInfo;
import com.netflix.vms.transformer.hollowoutput.RolloutPhaseWindow;
import com.netflix.vms.transformer.hollowoutput.RolloutRole;
import com.netflix.vms.transformer.hollowoutput.RolloutSummary;
import com.netflix.vms.transformer.hollowoutput.RolloutTrailer;
import com.netflix.vms.transformer.hollowoutput.RolloutVideo;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalInfoType;
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.TrailerInfo;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RolloutVideoModule extends AbstractTransformModule {
    private final HollowPrimaryKeyIndex supplementalIndex; // !! TODO: not used
    private final HollowPrimaryKeyIndex rolloutVideoTypeIndex;

    public RolloutVideoModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, mapper);
        this.supplementalIndex = indexer.getPrimaryKeyIndex(IndexSpec.SUPPLEMENTAL);
        this.rolloutVideoTypeIndex = indexer.getPrimaryKeyIndex(IndexSpec.ROLLOUT);
    }

    @Override
    public void transform() {

        Map<Integer, List<RolloutHollow>> videoIdToRolloutMap = new HashMap<>();

        for (RolloutHollow input : api.getAllRolloutHollow()) {
            Integer videoId = (int)input._getMovieId();
            List<RolloutHollow> rolloutList = videoIdToRolloutMap.get(videoId);
            if (rolloutList == null) {
                rolloutList = new ArrayList<>();
                videoIdToRolloutMap.put(videoId, rolloutList);
            }
            rolloutList.add(input);
        }

        Map<Integer, IndividualTrailerHollow> trailerIdToTrailerMap = new HashMap<>();

        for (TrailerHollow trailerHollow : api.getAllTrailerHollow()) {
            for (IndividualTrailerHollow trailer : trailerHollow._getTrailers()) {
                trailerIdToTrailerMap.put((int) trailer._getMovieId(), trailer);
            }
        }

        for (Integer videoId : videoIdToRolloutMap.keySet()) {
            RolloutVideo output = new RolloutVideo();
            output.video = new Video(videoId.intValue());

            Map<Strings, RolloutSummary> summaryMap = new HashMap<>();
            output.summaryMap = summaryMap;

            List<RolloutHollow> rolloutList = videoIdToRolloutMap.get(videoId);
            for (RolloutHollow rollout : rolloutList) {
                Strings rolloutType = new Strings(rollout._getRolloutType()._getValue());
                RolloutSummary summary = summaryMap.get(rolloutType);
                if (summary == null) {
                    summary = new RolloutSummary();
                    summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
                    summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
                    summary.allPhases = new ArrayList<Phase>();
                    summary.phaseWindowMap = new HashMap<ISOCountry, List<RolloutPhaseWindow>>();

                    summary.type = rollout._getRolloutType()._getValue().toCharArray();
                    summary.video = output.video;
                    summaryMap.put(rolloutType, summary);
                }

                RolloutInfo info = new RolloutInfo();
                info.name = rollout._getRolloutName()._getValue().toCharArray();
                info.rolloutId = (int) rollout._getRolloutId();
                info.video = output.video;
                info.type = rollout._getRolloutType()._getValue().toCharArray();
                summary.rolloutInfoMap.put(new com.netflix.vms.transformer.hollowoutput.Integer(info.rolloutId), info);

                RolloutPhaseListHollow phaseListHollow = rollout._getPhases();
                for (RolloutPhaseHollow phaseHollow : phaseListHollow) {
                    Phase phase = new Phase();
                    phase.projectedLaunchDates = new HashMap<ISOCountry, Date>(); // !! TODO
                    phase.windowsMap = new HashMap<ISOCountry, AvailabilityWindow>();
                    phase.artWorkImageIds = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();
                    phase.sourceFileIds = new HashSet<Strings>();
                    phase.trailers = new ArrayList<RolloutTrailer>();

                    phase.rolloutId = info.rolloutId;
                    phase.video = info.video;
                    phase.name = phaseHollow._getName()._getValue().toCharArray();
                    phase.isCoreMetaDataShown = phaseHollow._getShowCoreMetadata();

                    // add artwork ids
                    RolloutPhaseOldArtworkListHollow artworkIdList = phaseHollow._getElements()._getArtwork();
                    for (RolloutPhaseImageIdHollow idHollow : artworkIdList) {
                        phase.artWorkImageIds.add(new com.netflix.vms.transformer.hollowoutput.Long(idHollow._getImageId()));
                    }

                    // add artwork source fields
                    RolloutPhaseArtworkSourceFileIdListHollow artworkSourceFieldList = phaseHollow._getElements()._getArtwork_new()._getSourceFileIds();
                    for (RolloutPhaseArtworkSourceFileIdHollow sourceFieldHollow : artworkSourceFieldList) {
                        phase.sourceFileIds.add(new Strings(sourceFieldHollow._getValue()._getValue()));
                    }

                    // trailers
                    RolloutPhaseTrailerListHollow inputTrailerList = phaseHollow._getElements()._getTrailers();
                    for (RolloutPhaseTrailerHollow phaseTrailer : inputTrailerList) {
                        RolloutTrailer outputTrailer = new RolloutTrailer();
                        outputTrailer.sequenceNumber = (int) phaseTrailer._getSequenceNumber();
                        outputTrailer.video = new Video((int) phaseTrailer._getTrailerMovieId());
                        outputTrailer.supplementalInfos = new HashMap<SupplementalInfoType, TrailerInfo>();
                        RolloutPhasesElementsTrailerSupplementalInfoMapHollow inputInfoMap = phaseTrailer._getSupplementalInfo();

                        for (Entry<MapKeyHollow, RolloutPhaseTrailerSupplementalInfoHollow> entry : inputInfoMap.entrySet()) {
                            SupplementalInfoType typeOut = new SupplementalInfoType(entry.getKey()._getValue());
                            RolloutPhaseTrailerSupplementalInfoHollow infoIn = entry.getValue();

                            TrailerInfo infoOut = new TrailerInfo();
                            infoOut.imageBackgroundTone = infoIn._getImageBackgroundTone()._getValue().toCharArray();
                            infoOut.imageTag = infoIn._getImageTag()._getValue().toCharArray();
                            infoOut.priority = (int) infoIn._getPriority();
                            infoOut.seasonNumber = (int) infoIn._getSeasonNumber();
                            infoOut.subtitleLocale = infoIn._getSubtitleLocale()._getValue().toCharArray();
                            infoOut.type = typeOut;
                            infoOut.video = infoIn._getVideo()._getValue().toCharArray();
                            infoOut.videoLength = (int) infoIn._getVideoLength();
                            infoOut.videoValue = infoIn._getVideoValue()._getValue().toCharArray();

                            // #for-parity
                            if(infoOut.seasonNumber == Integer.MIN_VALUE) infoOut.seasonNumber = 1;

                            outputTrailer.supplementalInfos.put(typeOut, infoOut);
                        }

                        phase.trailers.add(outputTrailer);
                    }

                    RolloutPhaseWindowMapHollow phaseWindows = phaseHollow._getWindows();
                    for(Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phaseWindows.entrySet()) {
                        AvailabilityWindow w = new AvailabilityWindow();
                        RolloutPhaseWindowHollow inputPhaseWindow = entry.getValue();
                        w.startDate = new Date(inputPhaseWindow._getStartDate()._getValue());
                        w.endDate = new Date(inputPhaseWindow._getEndDate()._getValue());
                        ISOCountry country = new ISOCountry(entry.getKey()._getValue());
                        phase.windowsMap.put(country, w);

                        List<RolloutPhaseWindow> phaseWindowList = summary.phaseWindowMap.get(country);
                        if (phaseWindowList == null) {
                            phaseWindowList = new ArrayList<RolloutPhaseWindow>();
                            summary.phaseWindowMap.put(country, phaseWindowList);
                        }
                        RolloutPhaseWindow phaseWindow = new RolloutPhaseWindow();
                        phaseWindow.phaseWindow = w;
                        phaseWindow.phaseOrdinal = inputPhaseWindow.getOrdinal();
                        phaseWindowList.add(phaseWindow);
                    }

                    // Sort phaseWindows
                    for (ISOCountry country : summary.phaseWindowMap.keySet()) {
                        List<RolloutPhaseWindow> phaseWindowList = summary.phaseWindowMap.get(country);
                        Collections.sort(phaseWindowList, new RolloutPhaseWindowComparator());
                        int index = 0;
                        for (RolloutPhaseWindow window : phaseWindowList) {
                            window.phaseOrdinal = ++index;
                        }
                    }

                    phase.rawL10nAttribs = new HashMap<Strings, Strings>();
                    RolloutPhaseElementsHollow phaseElements = phaseHollow._getElements();
                    RolloutPhaseLocalizedMetadataHollow localized = phaseElements._getLocalized_metadata();

                    if (localized._getSUPPLEMENTAL_MESSAGE() != null)
                        phase.rawL10nAttribs.put(new Strings("SUPPLEMENTAL_MESSAGE"), new Strings(localized._getSUPPLEMENTAL_MESSAGE()._getValue()));
                    if (localized._getTAGLINE() != null)
                        phase.rawL10nAttribs.put(new Strings("TAGLINE"), new Strings(localized._getTAGLINE()._getValue()));

                    phase.supplementalVideos = new ArrayList<SupplementalVideo>();

                    for (RolloutTrailer rolloutTrailer : phase.trailers) {
                        IndividualTrailerHollow indivTrailerHollow = trailerIdToTrailerMap.get(rolloutTrailer.video.value);
                        if (indivTrailerHollow == null)
                            continue;

                        SupplementalVideo sv = new SupplementalVideo();
                        sv.id = new Video((int) indivTrailerHollow._getMovieId());
                        sv.parent = new Video(videoId);
                        sv.sequenceNumber = rolloutTrailer.sequenceNumber; // #cleanup instead of sv's sequence number?
                        sv.attributes = new HashMap<Strings, Strings>();

                        SingleValuePassthroughMapHollow singleValPassthrough = indivTrailerHollow._getPassthrough()._getSingleValues();
                        for (Map.Entry<MapKeyHollow, StringHollow> entry : singleValPassthrough.entrySet()) {
                            sv.attributes.put(new Strings(entry.getKey()._getValue()), new Strings(entry.getValue()._getValue()));
                        }

                        sv.attributes.put(new Strings("type"), new Strings("trailer"));
                        sv.attributes.put(new Strings("identifier"), new Strings(indivTrailerHollow._getIdentifier()._getValue()));
                        phase.supplementalVideos.add(sv);

                        sv.multiValueAttributes = new HashMap<Strings, List<Strings>>();
                        MultiValuePassthroughMapHollow multiValPassthrough = indivTrailerHollow._getPassthrough()._getMultiValues();
                        for (Map.Entry<MapKeyHollow, ListOfStringHollow> entry : multiValPassthrough.entrySet()) {
                            List<Strings> vals = new ArrayList<>();
                            for (StringHollow val : entry.getValue()) {
                                vals.add(new Strings(val._getValue()));
                            }
                            sv.multiValueAttributes.put(new Strings(entry.getKey()._getValue()), vals);
                        }
                    }
                    Collections.sort(phase.supplementalVideos, new SupplementalVideoComparator());

                    phase.roles = new ArrayList<RolloutRole>();

                    RolloutPhaseCharacterListHollow charList = phaseHollow._getElements()._getCharacters();
                    for (RolloutPhaseCharacterHollow phaseChar : charList) {
                        RolloutRole role = new RolloutRole();
                        role.characterId = (int) phaseChar._getCharacterId();
                        role.id = (int) phaseChar._getRoleId();
                        role.sequenceNumber = (int) phaseChar._getSequenceNumber();
                        role.person = new VPerson((int) phaseChar._getPersonId());
                        phase.roles.add(role);
                    }

                    phase.projectedLaunchDates = new HashMap<ISOCountry, Date>();
                    // #cleanup: launch-dates are pulled down, and duplicated for every phase level
                    RolloutMapOfLaunchDatesHollow launchMap = rollout._getLaunchDates();
                    for (Map.Entry<ISOCountryHollow, DateHollow> entry : launchMap.entrySet()) {
                        phase.projectedLaunchDates.put(new ISOCountry(entry.getKey()._getValue()), new Date(entry.getValue()._getValue()));
                    }

                    phase.casts = new ArrayList<RolloutCast>();
                    RolloutPhaseCastListHollow castList = phaseHollow._getElements()._getCast();
                    for (RolloutPhaseCastHollow castIn : castList) {
                        RolloutCast rc = new RolloutCast();
                        rc.person = new VPerson((int) castIn._getPersonId());
                        rc.sequenceNumber = (int) castIn._getSequenceNumber();
                        phase.casts.add(rc);
                    }

                    summary.allPhases.add(phase);
                }

            }
            mapper.addObject(output);
        }
    }



}
