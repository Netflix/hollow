package com.netflix.vms.transformer;

import com.google.common.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *     A SHOW has SEASONs
 *     A SHOW is a topNode
 *
 *     A SEASON has children (EPISODEs or DISCs) and is part of a show
 *
 *     A SEREIS has children (EPISODEs or DISCs) but is NOT part of a show
 *
 *     A STANDALONE_SEASON is part of a show but does not have any children
 *     A STANDALONE_SEASON is a viewable
 *     A STANDALONE_SEASON is a leaf node
 *
 *     A Movie is not part of a show and does not have children
 *     A MOVIE is a viewable or deliverable
 *     A MOVIE is a topNode and leafNode ( no parent and no children )
 *
 *     An EPISODE is a viewable
 *     A DISC is a deliverable
 *     An EPISODE and DISC are leafNodes
 */
public enum VideoNodeType {
    // Do not publish SUPPLEMENTAL until all clients have had the chance to upgrade to the first VMS client version that uses this additional enum
    UNKNOWN(-1, "Unknown"), SHOW(1, "Show"), SEASON(2, "Series"), MOVIE(4, "Standalone"), EPISODE(5, "Episode"), SUPPLEMENTAL(8, "Supplemental");

    // NOTE: STANDALONE_SEASON is a legacy issue.  We have older long tail discs that were set up before the concept of episodes.
    // I'm told that this data will not be cleaned up.

    private int id;
    private String typeName;

    VideoNodeType(final int id, final String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isShow() {
        return SHOW.equals(this);
    }

    public boolean isSeason() {
        return SEASON.equals(this);
    }

    public boolean isMovie() {
        return MOVIE.equals(this);
    }

    public boolean isEpisode() {
        return EPISODE.equals(this);
    }

    public boolean isSupplementalVideo() {
        return SUPPLEMENTAL.equals(this);
    }

    public boolean isUnknown() {
        return UNKNOWN.equals(this);
    }

    public boolean isViewable() {
        return EPISODE.equals(this) || MOVIE.equals(this) || SUPPLEMENTAL.equals(this);
    }

    public boolean isTopNode() {
        return isTopNode(this);
    }

    private static final Set<VideoNodeType> topNodeSet = Collections.unmodifiableSet(EnumSet.of(SHOW, MOVIE));
    private static final Set<VideoNodeType> standaloneSet = Collections.unmodifiableSet(EnumSet.of(MOVIE));
    private static final Map<String, VideoNodeType> map = new HashMap<String, VideoNodeType>();
    private static final Map<VideoNodeType, String> reverseMap = new HashMap<VideoNodeType, String>();

    static {
        map.put("Show", SHOW);
        map.put("Standalone", MOVIE);
        map.put("Series", SEASON); // Temp? -> Current
        map.put("Season", SEASON); // Temp? -> Proposed
        map.put("Episode", EPISODE);
        map.put("Supplemental", SUPPLEMENTAL);

        for (Map.Entry<String, VideoNodeType> i : map.entrySet()) {
            reverseMap.put(i.getValue(), i.getKey());
        }
    }

    @VisibleForTesting
    public static String _getReverseLookupForTesting(VideoNodeType type) {
        return reverseMap.get(type);
    }

    public static VideoNodeType of(String name) {
        VideoNodeType type = map.get(name);
        if (type == null) return UNKNOWN;
        return type;
    }

    public static boolean isTopNode(VideoNodeType type) {
        return topNodeSet.contains(type);
    }

    public static boolean isStandalone(VideoNodeType type) {
        return standaloneSet.contains(type);
    }

    public static boolean isStandaloneOrTopNode(VideoNodeType type) {
        return isTopNode(type) || isStandalone(type);
    }
}