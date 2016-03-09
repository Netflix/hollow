package com.netflix.vms.transformer.modules.countryspecific;

import java.util.Arrays;

import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.ShowHierarchy;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertSystemRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedCertificationSystemsHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoCountryRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingHollow;
import com.netflix.vms.transformer.hollowinput.ConsolidatedVideoRatingsHollow;
import com.netflix.vms.transformer.hollowinput.DateHollow;
import com.netflix.vms.transformer.hollowinput.ISOCountryHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MapOfFirstDisplayDatesHollow;
import com.netflix.vms.transformer.hollowinput.PackageStreamHollow;
import com.netflix.vms.transformer.hollowinput.PackagesHollow;
import com.netflix.vms.transformer.hollowinput.StreamNonImageInfoHollow;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoRatingAdvisoriesHollow;
import com.netflix.vms.transformer.hollowinput.VideoRatingAdvisoryIdHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractIdHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsContractPackageHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsFlagsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsRightsHollow;
import com.netflix.vms.transformer.hollowinput.VideoRightsWindowHollow;
import com.netflix.vms.transformer.hollowoutput.Certification;
import com.netflix.vms.transformer.hollowoutput.CertificationSystem;
import com.netflix.vms.transformer.hollowoutput.CompleteVideoCountrySpecificData;
import com.netflix.vms.transformer.hollowoutput.Date;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.LinkedHashSetOfStrings;
import com.netflix.vms.transformer.hollowoutput.MovieCertification;
import com.netflix.vms.transformer.hollowoutput.MovieRatingReason;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.SortedMapOfDateWindowToListOfInteger;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.VMSAvailabilityWindow;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoContractInfo;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoPackageInfo;
import com.netflix.vms.transformer.hollowoutput.WindowPackageContractInfo;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.packages.VideoFormatDescriptorIdentifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CountrySpecificDataModule {

    private final com.netflix.vms.transformer.hollowoutput.Integer ZERO = new com.netflix.vms.transformer.hollowoutput.Integer(0);
    private final VideoPackageInfo FILTERED_VIDEO_PACKAGE_INFO;

    private final VMSHollowVideoInputAPI api;
    private final HollowPrimaryKeyIndex videoRightsIdx;
    private final HollowPrimaryKeyIndex videoRatingsIdx;
    private final HollowPrimaryKeyIndex certSystemIdx;
    private final HollowPrimaryKeyIndex certSystemRatingIdx;
    private final HollowPrimaryKeyIndex packageIdx;
    private final HollowPrimaryKeyIndex streamProfileIdx;

    private final VideoFormatDescriptorIdentifier videoFormatIdentifier;


    private Map<Integer, Map<String, List<Certification>>> perCountryCertificationLists;
    private Map<Integer, List<PackageData>> transformedPackageData;

    public CountrySpecificDataModule(VMSHollowVideoInputAPI api, VMSTransformerIndexer indexer) {
        this.api = api;
        this.videoRightsIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_RIGHTS);
        this.videoRatingsIdx = indexer.getPrimaryKeyIndex(IndexSpec.CONSOLIDATED_VIDEO_RATINGS);
        this.certSystemIdx = indexer.getPrimaryKeyIndex(IndexSpec.CONSOLIDATED_CERT_SYSTEMS);
        this.certSystemRatingIdx = indexer.getPrimaryKeyIndex(IndexSpec.CERT_SYSTEM_RATING);
        this.packageIdx = indexer.getPrimaryKeyIndex(IndexSpec.PACKAGES);
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);

        this.perCountryCertificationLists = new HashMap<Integer, Map<String,List<Certification>>>();

        this.videoFormatIdentifier = new VideoFormatDescriptorIdentifier(api, indexer);

        FILTERED_VIDEO_PACKAGE_INFO = getFilteredVideoPackageInfo();
    }

    public Map<String, Map<Integer, CompleteVideoCountrySpecificData>> buildCountrySpecificDataByCountry(Map<String, ShowHierarchy> showHierarchiesByCountry, Map<Integer, List<PackageData>> transformedPackageData) {
        this.transformedPackageData = transformedPackageData;

        Map<String, Map<Integer, CompleteVideoCountrySpecificData>> allCountrySpecificDataMap = new HashMap<String, Map<Integer,CompleteVideoCountrySpecificData>>();

        for(Map.Entry<String, ShowHierarchy> entry : showHierarchiesByCountry.entrySet()) {
            String countryCode = entry.getKey();
            Map<Integer, CompleteVideoCountrySpecificData> countryMap = new HashMap<Integer, CompleteVideoCountrySpecificData>();
            allCountrySpecificDataMap.put(entry.getKey(), countryMap);

            ShowHierarchy hierarchy = entry.getValue();

            for(int i=0;i<hierarchy.getSeasonIds().length;i++) {
                for(int j=0;j<hierarchy.getEpisodeIds()[i].length;j++) {
                    int videoId = hierarchy.getEpisodeIds()[i][j];
                    convert(videoId, countryCode, countryMap);
                }

                convert(hierarchy.getSeasonIds()[i], countryCode, countryMap);
            }

            convert(hierarchy.getTopNodeId(), countryCode, countryMap);

            for(int i=0;i<hierarchy.getSupplementalIds().length;i++) {
                convert(hierarchy.getSupplementalIds()[i], countryCode, countryMap);
            }

        }

        perCountryCertificationLists.clear();

        return allCountrySpecificDataMap;
    }

    private void convert(Integer videoId, String countryCode, Map<Integer, CompleteVideoCountrySpecificData> countryMap) {
        CompleteVideoCountrySpecificData data = new CompleteVideoCountrySpecificData();

        populateRightsData(videoId, countryCode, data);
        populateCertificationLists(videoId, countryCode, data);



        countryMap.put(videoId, data);

    }

    private void populateCertificationLists(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data) {
        Map<String, List<Certification>> perCountryCertLists = perCountryCertificationLists.get(videoId);
        if(perCountryCertLists == null) {
            perCountryCertLists = buildCertificationListsByCountry(videoId);
            perCountryCertificationLists.put(videoId, perCountryCertLists);
        }

        data.certificationList = perCountryCertLists.get(countryCode);

        data.dateWindowWiseSeasonSequenceNumberMap = new SortedMapOfDateWindowToListOfInteger();
    }


    private void populateRightsData(Integer videoId, String countryCode, CompleteVideoCountrySpecificData data) {
        int rightsOrdinal = videoRightsIdx.getMatchingOrdinal(videoId.longValue(), countryCode);
        if(rightsOrdinal != -1) {
            VideoRightsHollow rights = api.getVideoRightsHollow(rightsOrdinal);

            populateWindowData(videoId, data, rights);
            populateFirstDisplayDateData(data, rights);
        }
    }

    private void populateWindowData(Integer videoId, CompleteVideoCountrySpecificData data, VideoRightsHollow videoRights) {
        List<VMSAvailabilityWindow> availabilityWindows = new ArrayList<VMSAvailabilityWindow>();

        boolean isGoLive = isGoLive(videoRights);

        VideoRightsRightsHollow rights = videoRights._getRights();

        for(VideoRightsWindowHollow window : rights._getWindows()) {
            VMSAvailabilityWindow outputWindow = new VMSAvailabilityWindow();
            outputWindow.startDate = new Date(window._getStartDate()._getValue());
            outputWindow.endDate = new Date(window._getEndDate()._getValue());
            outputWindow.windowInfosByPackageId = new HashMap<com.netflix.vms.transformer.hollowoutput.Integer, WindowPackageContractInfo>();

            for(VideoRightsContractIdHollow contractIdHollow : window._getContractIds()) {
                VideoRightsContractHollow contract = getContract(rights, contractIdHollow._getValue());



                if(contract != null) {
                    List<VideoRightsContractPackageHollow> packageIdList = contract._getPackages();

                    for(VideoRightsContractPackageHollow pkg : packageIdList) {
                        com.netflix.vms.transformer.hollowoutput.Integer packageId = new com.netflix.vms.transformer.hollowoutput.Integer((int)pkg._getPackageId());

                        WindowPackageContractInfo windowPackageContractInfo = outputWindow.windowInfosByPackageId.get(packageId);
                        if(windowPackageContractInfo != null) {
                            VideoPackageInfo videoPackageInfo = windowPackageContractInfo.videoPackageInfo;
                            //VideoContractInfo contractInfo = mergeMultiple


                        } else {
                            PackageData packageData = getPackageData(videoId, pkg._getPackageId());
                            if(packageData != null) {
                                if(shouldFilterOutWindowInfo(isGoLive, contract))
                                    outputWindow.windowInfosByPackageId.put(ZERO, buildFilteredWindowPackageContractInfo((int) contractIdHollow._getValue()));
                                else
                                    outputWindow.windowInfosByPackageId.put(packageId, buildWindowPackageContractInfo(packageData, contract));
                            }
                        }

                    }
                }


            }


            availabilityWindows.add(outputWindow);
        }

        Collections.sort(availabilityWindows, new Comparator<VMSAvailabilityWindow>() {
            public int compare(VMSAvailabilityWindow o1, VMSAvailabilityWindow o2) {
                return Long.compare(o1.startDate.val, o2.startDate.val);
            }
        });

        data.mediaAvailabilityWindows = availabilityWindows;
        data.imagesAvailabilityWindows = availabilityWindows;
    }

    private PackageData getPackageData(Integer videoId, long packageId) {
        List<PackageData> list = transformedPackageData.get(videoId);
        if(list == null)
            return null;

        for(int i=0;i<list.size();i++) {
            if(list.get(i).id == packageId)
                return list.get(i);
        }

        return null;
    }

    private boolean shouldFilterOutWindowInfo(boolean isGoLive, VideoRightsContractHollow contract) {
        if(isGoLive)
            return false;

        if (contract._getDayAfterBroadcast()) return false;

        return contract._getPrePromotionDays() <= 0;
    }


    private boolean isGoLive(VideoRightsHollow rights) {
        VideoRightsFlagsHollow flags = rights._getFlags();
        return flags != null && flags._getGoLive();
    }

    private WindowPackageContractInfo buildFilteredWindowPackageContractInfo(int contractId) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = getFilteredVideoContractInfo(contractId);
        info.videoPackageInfo = FILTERED_VIDEO_PACKAGE_INFO;
        return info;
    }

    private WindowPackageContractInfo buildWindowPackageContractInfo(PackageData packageData, VideoRightsContractHollow contract) {
        WindowPackageContractInfo info = new WindowPackageContractInfo();
        info.videoContractInfo = new VideoContractInfo();
        info.videoContractInfo.contractId = (int) contract._getContractId();
        info.videoContractInfo.primaryPackageId = (int) contract._getPackageId();
        if(contract._getPrePromotionDays() != 0)
            info.videoContractInfo.prePromotionDays = (int) contract._getPrePromotionDays();
        info.videoContractInfo.isDayAfterBroadcast = contract._getDayAfterBroadcast();
        info.videoContractInfo.hasRollingEpisodes = contract._getDayAfterBroadcast();
        info.videoContractInfo.cupTokens = new LinkedHashSetOfStrings(Collections.singletonList(new Strings(contract._getCupToken()._getValue())));



        info.videoPackageInfo = new VideoPackageInfo();
        info.videoPackageInfo.formats = new HashSet<VideoFormatDescriptor>();

        long longestRuntimeInSeconds = 0;

        for(StreamData streamData : packageData.streams) {
            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long) streamData.downloadDescriptor.encodingProfileId);
            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String streamProfileType = profile._getProfileType()._getValue();

            /// add the videoFormatDescriptor
            VideoFormatDescriptor descriptor = streamData.downloadDescriptor.videoFormatDescriptor;
            if(descriptor.id == 1 || descriptor.id == 3 || descriptor.id == 4)  // Only interested in HD or better
                info.videoPackageInfo.formats.add(descriptor);

            if("VIDEO".equals(streamProfileType)) {
                if(streamData.streamDataDescriptor.runTimeInSeconds > longestRuntimeInSeconds)
                    longestRuntimeInSeconds = streamData.streamDataDescriptor.runTimeInSeconds;
            } else if("AUDIO".equals(streamProfileType)) {
                profile._getAudioChannelCount();
            }
        }

        info.videoPackageInfo.runtimeInSeconds = (int) longestRuntimeInSeconds;

        return info;
    }

    private VideoRightsContractHollow getContract(VideoRightsRightsHollow rights, long contractId) {
        for(VideoRightsContractHollow contract : rights._getContracts()) {
            if(contract._getContractId() == contractId)
                return contract;
        }
        return null;
    }

    private void populateFirstDisplayDateData(CompleteVideoCountrySpecificData data, VideoRightsHollow rights) {
        VideoRightsFlagsHollow flags = rights._getFlags();

        DateHollow firstDisplayDate = flags._getFirstDisplayDate();
        if(firstDisplayDate != null)
            data.firstDisplayDate = new Date(firstDisplayDate._getValue() / 5000 * 5000);

        MapOfFirstDisplayDatesHollow firstDisplayDatesByLocale = flags._getFirstDisplayDates();

        if(firstDisplayDatesByLocale != null) {
            data.firstDisplayDateByLocale = new HashMap<NFLocale, Date>();
            for(Map.Entry<MapKeyHollow, DateHollow> entry : firstDisplayDatesByLocale.entrySet()) {
                data.firstDisplayDateByLocale.put(new NFLocale(entry.getKey()._getValue().replace('-', '_')), new Date(entry.getValue()._getValue() / 5000 * 5000));
            }
        }
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
                                cert.movieCert.ratingReason.reasonIds = new ArrayList(ids.size());
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

    private VideoContractInfo getFilteredVideoContractInfo(int contractId) {
        VideoContractInfo info = new VideoContractInfo();
        info.contractId = contractId;
        info.primaryPackageId = 0;
        info.cupTokens = new LinkedHashSetOfStrings();
        info.cupTokens.ordinals = Collections.emptyList();
        info.assetBcp47Codes = Collections.emptySet();
        return info;
    }

    private VideoPackageInfo getFilteredVideoPackageInfo() {
        VideoPackageInfo info = new VideoPackageInfo();
        info.packageId = 0;
        info.runtimeInSeconds = 0;
        info.soundTypes = Collections.emptyList();
        info.screenFormats = Collections.emptyList();
        info.phoneSnacks = Collections.emptyList();
        info.stillImagesMap = Collections.emptyMap();
        info.videoClipMap = Collections.emptyMap();
        info.trickPlayMap = Collections.emptyMap();
        info.formats = Collections.emptySet();
        return info;
    }

}
