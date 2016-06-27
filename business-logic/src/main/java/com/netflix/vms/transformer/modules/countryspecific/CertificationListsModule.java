package com.netflix.vms.transformer.modules.countryspecific;

import java.util.Collections;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertSystemRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertificationSystemsHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoCountryRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingsHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRatingAdvisoriesHollow;
import com.netflix.vms.transformer.hollowinput.VideoRatingAdvisoryIdHollow;
import com.netflix.vms.transformer.hollowoutput.Certification;
import com.netflix.vms.transformer.hollowoutput.CertificationSystem;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.MovieCertification;
import com.netflix.vms.transformer.hollowoutput.MovieRatingReason;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificationListsModule {

    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex videoRatingsIdx;
    private final HollowPrimaryKeyIndex certSystemIdx;
    private final HollowPrimaryKeyIndex certSystemRatingIdx;

    private final Map<Integer, Map<String, List<Certification>>> perCountryCertificationLists;

    public CertificationListsModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRatingsIdx = indexer.getPrimaryKeyIndex(IndexSpec.CONSOLIDATED_VIDEO_RATINGS);
        this.certSystemIdx = indexer.getPrimaryKeyIndex(IndexSpec.CONSOLIDATED_CERT_SYSTEMS);
        this.certSystemRatingIdx = indexer.getPrimaryKeyIndex(IndexSpec.CERT_SYSTEM_RATING);
        
        this.perCountryCertificationLists = new HashMap<Integer, Map<String,List<Certification>>>();
    }
    
    public void populateCertificationLists(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data) {
        Map<String, List<Certification>> perCountryCertLists = perCountryCertificationLists.get(videoId);
        if(perCountryCertLists == null) {
            perCountryCertLists = buildCertificationListsByCountry(videoId);
            perCountryCertificationLists.put(videoId, perCountryCertLists);
        }

        List<Certification> certList = perCountryCertLists.get(countryCode);
        data.certificationList = certList == null ? Collections.emptyList() : certList;

        data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger();
        data.dateWindowWiseSeasonSequenceNumberMap.map = Collections.emptyMap();
    }
    
    private Map<String, List<Certification>> buildCertificationListsByCountry(Integer videoId) {
        Map<String, List<Certification>> certificationListMap = new HashMap<String, List<Certification>>();

        int ratingsOrdinal = videoRatingsIdx.getMatchingOrdinal(videoId.longValue());
        if(ratingsOrdinal != -1) {
            ConsolidatedVideoRatingsHollow videoRatings = api.getConsolidatedVideoRatingsHollow(ratingsOrdinal);

            List<List<Certification>> certificationListsToPopulate = new ArrayList<List<Certification>>();

            for(ConsolidatedVideoRatingHollow rating : videoRatings._getRatings()) {
                certificationListsToPopulate.clear();

                for(ISOCountryHollow country : rating._getCountryList()) {
                    String countryCode = country._getValue();
                    List<Certification> countryCertList = certificationListMap.get(countryCode);
                    if(countryCertList == null) {
                        countryCertList = new ArrayList<Certification>();
                        certificationListMap.put(countryCode, countryCertList);
                    }
                    certificationListsToPopulate.add(countryCertList);
                }

                for(ConsolidatedVideoCountryRatingHollow countryRating : rating._getCountryRatings()) {
                    long certSystemId = countryRating._getCertificationSystemId();
                    int certSystemOrdinal = certSystemIdx.getMatchingOrdinal(certSystemId);
                    if(certSystemOrdinal != -1) {
                        ConsolidatedCertificationSystemsHollow certSystem = api.getConsolidatedCertificationSystemsHollow(certSystemOrdinal);

                        Certification cert = new Certification();
                        cert.movieCert = new MovieCertification();

                        cert.movieCert.certificationSystemId = (int) certSystemId;
                        cert.movieCert.ratingId = (int) countryRating._getRatingId();

                        VideoRatingAdvisoriesHollow advisories = countryRating._getAdvisories();
                        if(advisories != null) {
                            cert.movieCert.ratingReason = new MovieRatingReason();
                            cert.movieCert.ratingReason.isDisplayImageOnly = advisories._getImageOnly();
                            cert.movieCert.ratingReason.isDisplayOrderSpecific = advisories._getOrdered();
                            List<VideoRatingAdvisoryIdHollow> ids = advisories._getIds();
                            if(ids != null) {
                                cert.movieCert.ratingReason.reasonIds = new ArrayList<com.netflix.vms.transformer.hollowoutput.Integer>(ids.size());
                                for(VideoRatingAdvisoryIdHollow id : ids) {
                                    cert.movieCert.ratingReason.reasonIds.add(new com.netflix.vms.transformer.hollowoutput.Integer((int)id._getValue()));
                                }
                            }
                        }

                        cert.movieCert.videoId = new Video(videoId.intValue());

                        int certSystemRatingOrdinal = certSystemRatingIdx.getMatchingOrdinal((long)cert.movieCert.ratingId);
                        if(certSystemRatingOrdinal != -1) {
                            ConsolidatedCertSystemRatingHollow certSystemRating = api.getConsolidatedCertSystemRatingHollow(certSystemRatingOrdinal);
                            cert.movieCert.maturityLevel = (int) certSystemRating._getMaturityLevel();
                        }

                        cert.certSystem = new CertificationSystem();
                        cert.certSystem.id = (int) certSystem._getCertificationSystemId();
                        cert.certSystem.country = new ISOCountry(certSystem._getCountryCode()._getValue());
                        StringHollow officialURL = certSystem._getOfficialURL();
                        if(officialURL != null)
                            cert.certSystem.officialURL = new Strings(officialURL._getValue());

                        for(List<Certification> certList : certificationListsToPopulate) {
                            certList.add(cert);
                        }
                    }
                }
            }
        }

        return certificationListMap;
    }

    public void reset() {
        perCountryCertificationLists.clear();
    }


}
