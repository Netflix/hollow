package com.netflix.vms.transformer.modules.rollout;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MultiValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseElementsHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseLocalizedMetadataHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowMapHollow;
import com.netflix.vms.transformer.hollowinput.SingleValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
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
import com.netflix.vms.transformer.hollowoutput.SupplementalVideo;
import com.netflix.vms.transformer.hollowoutput.TrailerInfo;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RolloutVideoModule extends AbstractTransformModule {

    public RolloutVideoModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
    }

    @Override
    public void transform() {
        Map<Integer, List<RolloutHollow>> videoIdToRolloutMap = getVideoIdToRolloutMap();
        Map<Integer, IndividualSupplementalHollow> trailerIdToTrailerMap = getTrailerIdToTrailerMap();

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
                    initialize(summary);
                    summary.type = rollout._getRolloutType()._getValue().toCharArray();
                    summary.video = output.video;
                    summaryMap.put(rolloutType, summary);
                }

                RolloutInfo info = new RolloutInfo();
                info.video = output.video;
                copy(rollout, info);
                summary.rolloutInfoMap.put(new com.netflix.vms.transformer.hollowoutput.Integer(info.rolloutId), info);

                RolloutPhaseListHollow phaseListHollow = rollout._getPhases();
                for (RolloutPhaseHollow phaseHollow : phaseListHollow) {
                    Phase phase = new Phase();
                    initialize(phase);
                    phase.rolloutId = info.rolloutId;
                    phase.video = info.video;

                    copy(phaseHollow, phase);

                    RolloutPhaseWindowMapHollow phaseWindows = phaseHollow._getWindows();
                    for(Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phaseWindows.entrySet()) {
                        AvailabilityWindow w = new AvailabilityWindow();
                        RolloutPhaseWindowHollow inputPhaseWindow = entry.getValue();
                        copy(inputPhaseWindow, w);
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

                    RolloutPhaseElementsHollow phaseElements = phaseHollow._getElements();
                    RolloutPhaseLocalizedMetadataHollow localized = phaseElements._getLocalized_metadata();

                    if (localized._getSUPPLEMENTAL_MESSAGE() != null)
                        phase.rawL10nAttribs.put(new Strings("SUPPLEMENTAL_MESSAGE"), new Strings(localized._getSUPPLEMENTAL_MESSAGE()._getValue()));
                    if (localized._getTAGLINE() != null)
                        phase.rawL10nAttribs.put(new Strings("TAGLINE"), new Strings(localized._getTAGLINE()._getValue()));


                    for (RolloutTrailer rolloutTrailer : phase.trailers) {
                        IndividualSupplementalHollow indivTrailerHollow = trailerIdToTrailerMap.get(rolloutTrailer.video.value);
                        if (indivTrailerHollow == null)
                            continue;

                        SupplementalVideo sv = new SupplementalVideo();
                        copy(rolloutTrailer, sv);
                        copy(indivTrailerHollow, sv);
                        sv.parent = new Video(videoId);
                        phase.supplementalVideos.add(sv);
                    }
                    Collections.sort(phase.supplementalVideos, new SupplementalVideoComparator());

                    summary.allPhases.add(phase);
                }

            }
            mapper.addObject(output);
        }
    }

    private Map<Integer, List<RolloutHollow>> getVideoIdToRolloutMap() {
        Map<Integer, List<RolloutHollow>> videoIdToRolloutMap = new HashMap<>();

        for (RolloutHollow input : api.getAllRolloutHollow()) {
            Integer videoId = (int) input._getMovieId();
            List<RolloutHollow> rolloutList = videoIdToRolloutMap.get(videoId);
            if (rolloutList == null) {
                rolloutList = new ArrayList<>();
                videoIdToRolloutMap.put(videoId, rolloutList);
            }
            rolloutList.add(input);
        }
        return videoIdToRolloutMap;
    }

    private Map<Integer, IndividualSupplementalHollow> getTrailerIdToTrailerMap() {
        Map<Integer, IndividualSupplementalHollow> trailerIdToTrailerMap = new HashMap<>();

        for (SupplementalsHollow suppHollow : api.getAllSupplementalsHollow()) {
            for (IndividualSupplementalHollow trailer : suppHollow._getSupplementals()) {
                trailerIdToTrailerMap.put((int) trailer._getMovieId(), trailer);
            }
        }

        return trailerIdToTrailerMap;
    }

    private void initialize(RolloutSummary summary) {
        summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
        summary.rolloutInfoMap = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, RolloutInfo>();
        summary.allPhases = new ArrayList<Phase>();
        summary.phaseWindowMap = new HashMap<ISOCountry, List<RolloutPhaseWindow>>();
    }

    private void initialize(Phase phase) {
        phase.projectedLaunchDates = new HashMap<ISOCountry, Date>();
        phase.windowsMap = new HashMap<ISOCountry, AvailabilityWindow>();
        phase.artWorkImageIds = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();
        phase.sourceFileIds = new HashSet<Strings>();
        phase.trailers = new ArrayList<RolloutTrailer>();
        phase.casts = new ArrayList<RolloutCast>();
        phase.roles = new ArrayList<RolloutRole>();
        phase.supplementalVideos = new ArrayList<SupplementalVideo>();
        phase.rawL10nAttribs = new HashMap<Strings, Strings>();
    }

    // src -> dest
    private void copy(RolloutHollow rollout, RolloutInfo info) {
        info.name = rollout._getRolloutName()._getValue().toCharArray();
        info.rolloutId = (int) rollout._getRolloutId();
        info.type = rollout._getRolloutType()._getValue().toCharArray();
    }

    // src -> dest
    private void copy(RolloutPhaseHollow phaseHollow, Phase phase) {
        phase.name = phaseHollow._getName()._getValue().toCharArray();
        phase.isCoreMetaDataShown = phaseHollow._getShowCoreMetadata();
        phase.isOnHold = phaseHollow._getOnHold();
        phase.seasonVideo = (int)(phaseHollow._getSeasonMovieId()) == 0 ? null : new Video((int)phaseHollow._getSeasonMovieId());
        phase.phaseType = phaseHollow._getPhaseType() == null ? null :  phaseHollow._getPhaseType()._getValue().toCharArray();

        // add artwork source fields
        RolloutPhaseArtworkSourceFileIdListHollow artworkSourceFieldList = phaseHollow._getElements()._getArtwork()._getSourceFileIds();
        for (RolloutPhaseArtworkSourceFileIdHollow sourceFieldHollow : artworkSourceFieldList) {
            phase.sourceFileIds.add(new Strings(sourceFieldHollow._getValue()._getValue()));
        }
    }

    // src -> dest
    private void copy(RolloutPhaseWindowHollow inputPhaseWindow, AvailabilityWindow w) {
        w.startDate = new Date(inputPhaseWindow._getStartDate());
        w.endDate = new Date(inputPhaseWindow._getEndDate());
    }

    // src -> dest
    private void copy(RolloutTrailer rolloutTrailer, SupplementalVideo sv) {
        sv.sequenceNumber = rolloutTrailer.sequenceNumber; // #cleanup instead of sv's sequence number?

        if (rolloutTrailer.supplementalInfos != null) {
            Iterator<TrailerInfo> it = rolloutTrailer.supplementalInfos.values().iterator();
            if (it.hasNext()) {
                sv.seasonNumber = it.next().seasonNumber;
            }
        }
    }

    // src -> dest
    private void copy(IndividualSupplementalHollow indivTrailerHollow, SupplementalVideo sv) {
        sv.id = new Video((int) indivTrailerHollow._getMovieId());
        sv.attributes = new HashMap<Strings, Strings>();
        SingleValuePassthroughMapHollow singleValPassthrough = indivTrailerHollow._getPassthrough()._getSingleValues();
        for (Map.Entry<MapKeyHollow, StringHollow> entry : singleValPassthrough.entrySet()) {
            sv.attributes.put(new Strings(entry.getKey()._getValue()), new Strings(entry.getValue()._getValue()));
        }

        sv.attributes.put(new Strings("type"), new Strings("trailer"));
        StringHollow identifier = indivTrailerHollow._getIdentifier();
        if(identifier != null)
            sv.attributes.put(new Strings("identifier"), new Strings(indivTrailerHollow._getIdentifier()._getValue()));

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
}
