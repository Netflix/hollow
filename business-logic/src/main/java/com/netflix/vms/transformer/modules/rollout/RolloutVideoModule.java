package com.netflix.vms.transformer.modules.rollout;

import com.netflix.config.FastProperty;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;
import com.netflix.vms.transformer.hollowinput.RolloutHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseArtworkSourceFileIdListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseElementsHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseListHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseLocalizedMetadataHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowHollow;
import com.netflix.vms.transformer.hollowinput.RolloutPhaseWindowMapHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourceString;
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
import com.netflix.vms.transformer.util.OutputUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RolloutVideoModule extends AbstractTransformModule {

    public static final String POST_PLAY_ATTR = "postPlay";
    public static final String GENERAL_ATTR = "general";
    public static final String THEMATIC_ATTR = "thematic";
    public static final String SUB_TYPE_ATTR = "subType";

    public static final String IDENTIFIERS_ATTR = "identifiers";
    public static final String THEMES_ATTR = "themes";
    public static final String USAGES_ATTR = "usages";

    public static FastProperty.BooleanProperty ADD_ASPECT_RATIO = new FastProperty.BooleanProperty("transformer.supplementalAttributes.aspectRatio", true);


    public RolloutVideoModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, cycleConstants, mapper);
    }

    @Override
    public void transform() {
        Map<Integer, List<RolloutHollow>> videoIdToRolloutMap = getVideoIdToRolloutMap();
        Map<Integer, IndividualSupplementalHollow> trailerIdToTrailerMap = getTrailerIdToTrailerMap();

        for (Integer videoId : videoIdToRolloutMap.keySet()) {
            /// short circuit irrelevant Videos in Fastlane
            if(ctx.getFastlaneIds() != null && !ctx.getFastlaneIds().contains(videoId))
                continue;

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
                if (phaseListHollow != null) {
                    for (RolloutPhaseHollow phaseHollow : phaseListHollow) {
                        Phase phase = new Phase();
                        initialize(phase);
                        phase.rolloutId = info.rolloutId;
                        phase.video = info.video;

                        copy(phaseHollow, phase);

                        RolloutPhaseWindowMapHollow phaseWindows = phaseHollow._getWindows();
                        for (Entry<ISOCountryHollow, RolloutPhaseWindowHollow> entry : phaseWindows.entrySet()) {
                            AvailabilityWindow w = new AvailabilityWindow();
                            RolloutPhaseWindowHollow inputPhaseWindow = entry.getValue();
                            copy(inputPhaseWindow, w);
                            ISOCountry country = cycleConstants.getISOCountry(entry.getKey()._getValue());
                            phase.windowsMap.put(country, w);

                            List<RolloutPhaseWindow> phaseWindowList = summary.phaseWindowMap.get(country);
                            if (phaseWindowList == null) {
                                phaseWindowList = new ArrayList<RolloutPhaseWindow>();
                                summary.phaseWindowMap.put(country, phaseWindowList);
                            }
                            RolloutPhaseWindow phaseWindow = new RolloutPhaseWindow();
                            phaseWindow.phaseWindow = w;
                            phaseWindowList.add(phaseWindow);
                        }

                        RolloutPhaseElementsHollow phaseElements = phaseHollow._getElements();
                        RolloutPhaseLocalizedMetadataHollow localized = phaseElements._getLocalized_metadata();

                        if (localized._getSUPPLEMENTAL_MESSAGE() != null)
                            phase.rawL10nAttribs.put(new Strings("SUPPLEMENTAL_MESSAGE"), new Strings(localized._getSUPPLEMENTAL_MESSAGE()._getValue()));
                        if (localized._getTAGLINE() != null)
                            phase.rawL10nAttribs.put(new Strings("TAGLINE"), new Strings(localized._getTAGLINE()._getValue()));
                        if (localized._getMERCH_OVERRIDE_MESSAGE() != null)
                            phase.rawL10nAttribs.put(new Strings("MERCH_OVERRIDE_MESSAGE"), new Strings(localized._getMERCH_OVERRIDE_MESSAGE()._getValue()));
                        if (localized._getPOSTPLAY_OVERRIDE_MESSAGE() != null)
                            phase.rawL10nAttribs.put(new Strings("POSTPLAY_OVERRIDE_MESSAGE"), new Strings(localized._getPOSTPLAY_OVERRIDE_MESSAGE()._getValue()));
                        if (localized._getODP_OVERRIDE_MESSAGE() != null)
                            phase.rawL10nAttribs.put(new Strings("ODP_OVERRIDE_MESSAGE"), new Strings(localized._getODP_OVERRIDE_MESSAGE()._getValue()));


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
        phase.sourceFileIds = new HashSet<ArtworkSourceString>();
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
        if (phaseHollow._getElements() != null && phaseHollow._getElements()._getArtwork() != null && phaseHollow._getElements()._getArtwork()._getSourceFileIds() != null) {

            RolloutPhaseArtworkSourceFileIdListHollow artworkSourceFieldList = phaseHollow._getElements()._getArtwork()._getSourceFileIds();
            for (RolloutPhaseArtworkSourceFileIdHollow sourceFieldHollow : artworkSourceFieldList) {
                phase.sourceFileIds.add(new ArtworkSourceString(sourceFieldHollow._getValue()._getValue()));
            }
        }
    }

    // src -> dest
    private void copy(RolloutPhaseWindowHollow inputPhaseWindow, AvailabilityWindow w) {
        w.startDate = OutputUtil.getRoundedDate(inputPhaseWindow._getStartDate());
        w.endDate = OutputUtil.getRoundedDate(inputPhaseWindow._getEndDate());
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

        sv.attributes = new HashMap<>();
        // Supplemental pass-through does not exists anymore, each field in the schema needs to be added.
        sv.attributes.put(new Strings(POST_PLAY_ATTR), new Strings(String.valueOf(indivTrailerHollow._getPostplay())));
        sv.attributes.put(new Strings(GENERAL_ATTR), new Strings(String.valueOf(indivTrailerHollow._getGeneral())));
        sv.attributes.put(new Strings(THEMATIC_ATTR), new Strings(String.valueOf(indivTrailerHollow._getThematic())));
        if (indivTrailerHollow._getSubType() != null && indivTrailerHollow._getSubType()._getValue() != null) {
            sv.attributes.put(new Strings(SUB_TYPE_ATTR), new Strings(indivTrailerHollow._getSubType()._getValue()));
        }
        sv.attributes.put(new Strings("type"), new Strings("trailer"));

        if (ADD_ASPECT_RATIO.get()) {
            sv.attributes.put(new Strings("aspectRation"), new Strings(""));
        }

        sv.multiValueAttributes = new HashMap<>();
        // process themes
        List<Strings> themes = new ArrayList<>();
        if (indivTrailerHollow._getThemes() != null) {
            Iterator<StringHollow> it = indivTrailerHollow._getThemes().iterator();
            while (it.hasNext()) {
                themes.add(new Strings(it.next()._getValue()));
            }
        }
        sv.multiValueAttributes.put(new Strings(THEMES_ATTR), themes);

        // process identifiers
        List<Strings> identifiers = new ArrayList<>();
        if (indivTrailerHollow._getIdentifiers() != null) {
            Iterator<StringHollow> it = indivTrailerHollow._getIdentifiers().iterator();
            while (it.hasNext()) {
                identifiers.add(new Strings(it.next()._getValue()));
            }
        }
        sv.multiValueAttributes.put(new Strings(IDENTIFIERS_ATTR), identifiers);

        // process usages
        List<Strings> usages = new ArrayList<>();
        if (indivTrailerHollow._getUsages() != null) {
            Iterator<StringHollow> it = indivTrailerHollow._getUsages().iterator();
            while (it.hasNext()) {
                usages.add(new Strings(it.next()._getValue()));
            }
        }
        sv.multiValueAttributes.put(new Strings(USAGES_ATTR), usages);
    }
}
