package com.netflix.vms.transformer.modules.meta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.Strings;

import static org.mockito.Mockito.when;

public class DescendingMerchStillsOrderTest {
    private final Map<String, Set<VideoHierarchy>> showHierarchiesByCountry = new HashMap<>();
    private final Map<String, Map<Integer, Set<Artwork>>> countryArtworkMap = new HashMap<>();


    @Test
    public void testGDocCase() {
        countryArtworkMap.clear();
        showHierarchiesByCountry.clear();

        Set<String> rollupSourceFieldIds = getStringSet("c", "e", "j", "k");
        Set<String> merchstillSourceFieldIds = getStringSet("a","b","c","d","e","f","g","h","i","j","k","l");

        VideoHierarchy hierarchy = populateVideoHierarchy(new Example1());
        // log(hierarchy);
        Set<VideoHierarchy> hset = new HashSet<>();
        hset.add(hierarchy);
        showHierarchiesByCountry.put("US", hset);

        VideoImagesDataModule.rollupMerchstillsDescOrder(rollupSourceFieldIds, 
                showHierarchiesByCountry, 
                merchstillSourceFieldIds, 
                countryArtworkMap, 
                new EDAvailabilityChecker() {
                    @Override
                    public boolean isAvailableForED(int videoId, String countryCode) {
                        return true;
                    }
                },
                6
            );

        Map<Integer, Set<Artwork>> videoIdMap = countryArtworkMap.get("US"); 
        // log(videoIdMap);
        Set<Artwork> artSet = videoIdMap.get(0); //show
        // j, k, c, e, g, h
        Assert.assertTrue(contains(artSet, "j", 2));
        Assert.assertTrue(contains(artSet, "k", 2));
        Assert.assertTrue(contains(artSet, "c", 3));
        Assert.assertTrue(contains(artSet, "e", 4));
        Assert.assertTrue(contains(artSet, "g", 5));
        Assert.assertTrue(contains(artSet, "h", 6));
        
        artSet = videoIdMap.get(1); //season 1
        // c, e, a, b, d, f
        Assert.assertTrue(lessThan(artSet, "c", "e"));
        Assert.assertTrue(lessThan(artSet, "e", "a"));
        Assert.assertTrue(lessThan(artSet, "a", "b"));
        Assert.assertTrue(lessThan(artSet, "b", "d"));
        Assert.assertTrue(lessThan(artSet, "d", "f"));

        artSet = videoIdMap.get(2); //season 2
        // j, k, g, h, i, l
        Assert.assertTrue(iequalTo(artSet, "j", "k"));
        Assert.assertTrue(lessThan(artSet, "k", "g"));
        Assert.assertTrue(lessThan(artSet, "g", "h"));
        Assert.assertTrue(lessThan(artSet, "h", "i"));
        Assert.assertTrue(lessThan(artSet, "i", "l"));
    }

    private VideoHierarchy populateVideoHierarchy(ArtworkPopulator artworkPopulator) {
        VideoHierarchy hierarchy = Mockito.mock(VideoHierarchy.class);
        Map<Integer, Set<Artwork>> videoArtMap = new HashMap<>();
        countryArtworkMap.put("US", videoArtMap);
        
        int[][] episodeIds = new int[2][2];
        for(int iseason = 0; iseason < 2; iseason++) {
            for(int iepisode=0; iepisode < 2; iepisode++) {
                int videoId = (iseason+1)*10 + iepisode+1;
                episodeIds[iseason][iepisode] = videoId;
                videoArtMap.put(videoId, artworkPopulator.getArtwork(iseason, iepisode));
            }
        }
        videoArtMap.put(0, new HashSet<>());
        when(hierarchy.getSeasonIds()).thenReturn(new int[] {1,2});
        when(hierarchy.getEpisodeIds()).thenReturn(episodeIds);
        when(hierarchy.getTopNodeId()).thenReturn(0);
        return hierarchy;
    }

    private boolean contains( Set<Artwork> artSet, String sourceFieldId, int seqNum) {
        for(Artwork art : artSet) {
            if(new String(art.sourceFileId.value).equals(sourceFieldId) && art.seqNum == seqNum)
                return true;
        }
        return false;
    }

    private boolean lessThan(Set<Artwork> artSet, String s1, String s2) {
        Artwork o1 = get(artSet, s1);
        Artwork o2 = get(artSet, s2);
        return o1.seqNum < o2.seqNum; 
    }

    private boolean iequalTo(Set<Artwork> artSet, String s1, String s2) {
        Artwork o1 = get(artSet, s1);
        Artwork o2 = get(artSet, s2);
        return o1.seqNum  == o2.seqNum; 
    }

    private Artwork get(Set<Artwork> artSet, String sourceFieldId) {
        for(Artwork art : artSet) {
            if(new String(art.sourceFileId.value).equals(sourceFieldId))
                return art;
        }
        return null;
    }

    static interface ArtworkPopulator {
        Set<Artwork> getArtwork(int season, int episode);
    }

    // https://docs.google.com/document/d/1paFkF8WyWJ6dB1Rh7lWZ_y8hk1WwjPoXt-TD0hzc-z0/edit#
    // slight modification for episode 2, season 2
    static class Example1 implements ArtworkPopulator {

        @Override
        public Set<Artwork> getArtwork(int iseason, int iepisode) {
            Set<Artwork> set = new HashSet<>();
            for(int file_seq = 1; file_seq <=3; file_seq++) {
                char srcField = 'a';
                int offset = iseason*6 + iepisode*3 + file_seq - 1;
                String sourceFieldId = String.valueOf((char)(srcField + offset));
                Artwork artwork = new Artwork();
                artwork.file_seq = file_seq;
                artwork.sourceFileId = new Strings(sourceFieldId);
                set.add(artwork);
            }
            return set;
        }
    }

    private Set<String> getStringSet(String... vals) {
        Set<String> set = new HashSet<>();
        for(String val : vals) {
            set.add(val);
        }
        return set;
    }

    void log(VideoHierarchy hierarchy) {
        int[][] episodeIds = hierarchy.getEpisodeIds();
        for(int iseason = 0; iseason < 2; iseason++) {
            for(int iepisode=0; iepisode < 2; iepisode++) {
                System.out.println("season[" + iseason + "] episode:" + iepisode + ", id=" + episodeIds[iseason][iepisode]);
            }
        }
    }
    
    void log(Map<Integer, Set<Artwork>> videoMap) {
        for(Integer videoId : videoMap.keySet()) {
            Set<Artwork> artSet = videoMap.get(videoId);
            System.out.println("\nvideoId=" + videoId);
            for(Artwork art : artSet) {
                System.out.println("    " + art.sourceFileId + "=" + art.file_seq + ", seqNum=" + art.seqNum);
            }
        }
    }
}
