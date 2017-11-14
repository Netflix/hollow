package com.netflix.vms.transformer;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.util.IntList;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.hollowinput.EpisodeHollow;
import com.netflix.vms.transformer.hollowinput.SeasonHollow;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoHierarchy {

    private final TransformerContext ctx;
    private final int topNodeId;
    private final boolean isStandalone;
    private final int seasonIds[];
    private final int seasonSequenceNumbers[];
    private final int episodeIds[][];
    private final int episodeSequenceNumbers[][];
    private final int supplementalIds[];
    private final int hashCode;
    private final Set<Integer> allIds = new HashSet<>();
    private final Set<Integer> droppedIds = new HashSet<>();
    private final Map<Integer, Integer> supplementalSeasonSeqNumMap = new HashMap<>();

    private final ShowMerchBehavior showMerchBehavior;
    private final SeasonMerchBehavior[] seasonMerchBehavior;
    private final EpisodeMerchBehavior[][] episodeMerchBehavior;

    public VideoHierarchy(TransformerContext ctx, int topNodeId, boolean isStandalone, ShowSeasonEpisodeHollow set, String countryCode, VideoHierarchyInitializer initializer) {
        this.ctx = ctx;
        this.topNodeId = topNodeId;
        this.isStandalone = isStandalone;
        int hashCode = HashCodes.hashInt(topNodeId);

        IntList supplementalIds = new IntList();
        initializer.addSupplementalVideos(topNodeId, countryCode, supplementalIds, droppedIds);

        List<SeasonHollow> seasons = null;

        if (set != null)
            seasons = set._getSeasons();

        if (seasons != null) {
            seasons = new ArrayList<SeasonHollow>(seasons);
            Collections.sort(seasons, new Comparator<SeasonHollow>() {
                @Override
                public int compare(SeasonHollow o1, SeasonHollow o2) {
                    return (int) o1._getSequenceNumber() - (int) o2._getSequenceNumber();
                }
            });

            int seasonIds[] = new int[seasons.size()];
            int seasonSequenceNumbers[] = new int[seasons.size()];
            SeasonMerchBehavior[] seasonMerchBehaviorAttributes = new SeasonMerchBehavior[seasons.size()];

            int episodeIds[][] = new int[seasons.size()][];
            int episodeSequenceNumbers[][] = new int[seasons.size()][];
            EpisodeMerchBehavior[][] episodeMerchBehaviorAttributes = new EpisodeMerchBehavior[seasons.size()][];

            int seasonCounter = 0;

            for (int i = 0; i < seasons.size(); i++) {
                SeasonHollow season = seasons.get(i);

                int seasonId = (int) season._getMovieId();
                if (!initializer.isChildNodeIncluded(seasonId, countryCode)) {
                    initializer.addSeasonAndAllChildren(season, droppedIds);
                    continue;
                }

                int seasonSequenceNumber = (int) season._getSequenceNumber();
                Set<Integer> addedSeasonSupplementals = initializer.addSupplementalVideos(seasonId, countryCode, supplementalIds, droppedIds);
                addToSupplementalSeasonSeqNumMap(topNodeId, seasonId, null, seasonSequenceNumber, addedSeasonSupplementals);

                seasonIds[seasonCounter] = seasonId;
                seasonSequenceNumbers[seasonCounter] = seasonSequenceNumber;
                seasonMerchBehaviorAttributes[seasonCounter] = getMerchBehavior(season);

                hashCode ^= seasonIds[i];
                hashCode = HashCodes.hashInt(hashCode);

                List<EpisodeHollow> episodes = new ArrayList<EpisodeHollow>(season._getEpisodes());
                Collections.sort(episodes, new Comparator<EpisodeHollow>() {
                    @Override
                    public int compare(EpisodeHollow o1, EpisodeHollow o2) {
                        return (int) o1._getSequenceNumber() - (int) o2._getSequenceNumber();
                    }
                });

                episodeIds[seasonCounter] = new int[episodes.size()];
                episodeSequenceNumbers[seasonCounter] = new int[episodes.size()];
                episodeMerchBehaviorAttributes[seasonCounter] = new EpisodeMerchBehavior[episodes.size()];

                int episodeCounter = 0;

                for (int j = 0; j < episodes.size(); j++) {
                    EpisodeHollow episode = episodes.get(j);

                    int episodeId = (int) episode._getMovieId();
                    if (!initializer.isChildNodeIncluded(episodeId, countryCode)) {
                        initializer.addVideoAndAssociatedSupplementals(episodeId, droppedIds);
                        continue;
                    }

                    Set<Integer> addedEpisodeSupplementals = initializer.addSupplementalVideos(episode._getMovieId(), countryCode, supplementalIds, droppedIds);
                    addToSupplementalSeasonSeqNumMap(topNodeId, seasonId, episodeId, seasonSequenceNumber, addedEpisodeSupplementals);

                    episodeIds[seasonCounter][episodeCounter] = (int) episode._getMovieId();
                    episodeSequenceNumbers[seasonCounter][episodeCounter] = (int) episode._getSequenceNumber();
                    episodeMerchBehaviorAttributes[seasonCounter][episodeCounter] = getMerchBehavior(episode);

                    hashCode ^= episodeIds[seasonCounter][episodeCounter];
                    hashCode = HashCodes.hashInt(hashCode);
                    episodeCounter++;
                }

                if (episodeCounter != episodeIds[seasonCounter].length)
                    episodeIds[seasonCounter] = Arrays.copyOf(episodeIds[seasonCounter], episodeCounter);

                seasonCounter++;
            }

            if (seasonCounter != seasonIds.length) {
                seasonIds = Arrays.copyOf(seasonIds, seasonCounter);
                episodeIds = Arrays.copyOf(episodeIds, seasonCounter);
            }

            this.seasonMerchBehavior = seasonMerchBehaviorAttributes;
            this.episodeMerchBehavior = episodeMerchBehaviorAttributes;
            this.showMerchBehavior = getMerchBehavior(set);

            this.seasonIds = seasonIds;
            this.episodeIds = episodeIds;
            this.seasonSequenceNumbers = seasonSequenceNumbers;
            this.episodeSequenceNumbers = episodeSequenceNumbers;
        } else {

            this.seasonMerchBehavior = new SeasonMerchBehavior[0];
            this.episodeMerchBehavior = new EpisodeMerchBehavior[0][];
            this.showMerchBehavior = null;

            this.seasonIds = new int[0];
            this.episodeIds = new int[0][];
            this.seasonSequenceNumbers = new int[0];
            this.episodeSequenceNumbers = new int[0][];
        }

        this.supplementalIds = supplementalIds.arrayCopyOfRange(0, supplementalIds.size());

        for (int i = 0; i < supplementalIds.size(); i++) {
            hashCode ^= HashCodes.hashInt(supplementalIds.get(i));
        }

        this.hashCode = hashCode;

        // Track all ids
        addIds(this.topNodeId);
        addIds(this.seasonIds);
        for (int[] episodeIdsPerSeason : this.episodeIds) {
            addIds(episodeIdsPerSeason);
        }
        addIds(this.supplementalIds);
    }

    private void addToSupplementalSeasonSeqNumMap(int topNodeId, int seasonId, Integer episodeId, int seasonSeqNum, Set<Integer> supplementalIds) {
        if (!ctx.getConfig().isSeasonNumberForChildrenEnabled() || supplementalIds == null) return;

        for (Integer supId : supplementalIds) {
            Integer prevSeasonSeqNum = supplementalSeasonSeqNumMap.get(supId);
            if (prevSeasonSeqNum != null) {
                ctx.getLogger().error(TransformerLogTag.SupplementalSeasonSeqNumConflict, "SupplementalVideo={} for Episode={}/Season={}/Show={} already has previousSeasonSeqNum={} vs currentSeasonSeqNum={}", supId, episodeId == null ? "" : episodeId, seasonId, topNodeId, prevSeasonSeqNum, seasonSeqNum);
            }
            supplementalSeasonSeqNumMap.put(supId, seasonSeqNum);
        }
    }

    public Map<Integer, Integer> getSupplementalSeasonSeqNumMap() {
        return supplementalSeasonSeqNumMap;
    }

    public Set<Integer> getDroppedIds() {
        return droppedIds;
    }

    private void addIds(int... ids) {
        for (int id : ids) {
            allIds.add(id);
        }
    }

    public Set<Integer> getAllIds() {
        return allIds;
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public int getTopNodeId() {
        return topNodeId;
    }

    public int[] getSeasonIds() {
        return seasonIds;
    }

    public int[] getSeasonSequenceNumbers() {
        return seasonSequenceNumbers;
    }

    public int[][] getEpisodeIds() {
        return episodeIds;
    }

    public int[][] getEpisodeSequenceNumbers() {
        return episodeSequenceNumbers;
    }

    public int[] getSupplementalIds() {
        return supplementalIds;
    }

    public boolean includesSupplementalId(int id) {
        for (int supplementalId : supplementalIds)
            if (supplementalId == id)
                return true;
        return false;
    }

    public ShowMerchBehavior getShowMerchBehavior() {
        return showMerchBehavior;
    }

    public EpisodeMerchBehavior getEpisodeMerchingBehaviour(int seasonNum, int episodeNum) {
        if (seasonNum <= episodeMerchBehavior.length && episodeNum <= episodeMerchBehavior[seasonNum].length) {
            return episodeMerchBehavior[seasonNum][episodeNum];
        }
        return null;
    }

    public SeasonMerchBehavior getSeasonMerchingBehaviour(int seasonNum) {
        if (seasonNum <= seasonMerchBehavior.length)
            return seasonMerchBehavior[seasonNum];
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VideoHierarchy))
            return false;
        VideoHierarchy other = (VideoHierarchy) obj;
        if (topNodeId != other.topNodeId)
            return false;
        if (!Arrays.equals(seasonIds, other.seasonIds))
            return false;
        for (int i = 0; i < seasonIds.length; i++) {
            if (seasonSequenceNumbers[i] != other.seasonSequenceNumbers[i])
                return false;
        }

        for (int i = 0; i < episodeIds.length; i++) {
            if (!Arrays.equals(episodeIds[i], other.episodeIds[i]))
                return false;
            for (int j = 0; j < episodeIds[i].length; j++) {
                if (episodeSequenceNumbers[i][j] != other.episodeSequenceNumbers[i][j])
                    return false;
            }
        }
        if (!Arrays.equals(supplementalIds, other.supplementalIds))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("topNodeId=").append(topNodeId);
        sb.append("\nisStandalone=").append(isStandalone);
        for (int i = 0; i < seasonIds.length; i++) {
            sb.append("\n\tseasonId=").append(seasonIds[i]).append("(").append(seasonSequenceNumbers[i]).append(")");
            for (int j = 0; j < episodeIds[i].length; j++) {
                sb.append("\n\t\tepisodeIds=").append(episodeIds[i][j]).append("(").append(episodeSequenceNumbers[i][j]).append(")");
            }
        }

        sb.append("\nsupplementalIds=");
        for (int supId : supplementalIds) {
            sb.append(supId).append(" ");
        }
        sb.append("\ndroppedIds=").append(droppedIds);

        return sb.toString();
    }

    private ShowMerchBehavior getMerchBehavior(ShowSeasonEpisodeHollow showSeasonEpisodeHollow) {
        ShowMerchBehavior attributes = new ShowMerchBehavior();
        attributes.hideSeasonNumbers = showSeasonEpisodeHollow._getHideSeasonNumbers();
        attributes.episodicNewBadge = showSeasonEpisodeHollow._getEpisodicNewBadge();
        attributes.merchOrder = showSeasonEpisodeHollow._getMerchOrder()._getValue();
        return attributes;
    }

    private SeasonMerchBehavior getMerchBehavior(SeasonHollow seasonHollow) {
        SeasonMerchBehavior attributes = new SeasonMerchBehavior();
        attributes.hideEpisodeNumbers = seasonHollow._getHideEpisodeNumbers();
        attributes.episodicNewBadge = seasonHollow._getEpisodicNewBadge();
        attributes.episodeSkipping = seasonHollow._getEpisodeSkipping();
        attributes.filterUnavailableEpisodes = seasonHollow._getFilterUnavailableEpisodes();
        attributes.useLatestEpisodeAsDefault = seasonHollow._getUseLatestEpisodeAsDefault();
        attributes.merchOrder = seasonHollow._getMerchOrder()._getValue();
        return attributes;
    }

    private EpisodeMerchBehavior getMerchBehavior(EpisodeHollow episodeHollow) {
        EpisodeMerchBehavior attributes = new EpisodeMerchBehavior();
        attributes.midSeason = episodeHollow._getMidSeason();
        attributes.seasonFinale = episodeHollow._getSeasonFinale();
        attributes.showFinale = episodeHollow._getShowFinale();
        return attributes;
    }

    public static class ShowMerchBehavior {
        public boolean hideSeasonNumbers;
        public boolean episodicNewBadge;
        public String merchOrder;
    }

    public static class SeasonMerchBehavior {
        public boolean hideEpisodeNumbers;
        public boolean episodicNewBadge;
        public int episodeSkipping;
        public boolean filterUnavailableEpisodes;
        public boolean useLatestEpisodeAsDefault;
        public String merchOrder;
    }

    public static class EpisodeMerchBehavior {
        public boolean midSeason;
        public boolean seasonFinale;
        public boolean showFinale;
    }
}