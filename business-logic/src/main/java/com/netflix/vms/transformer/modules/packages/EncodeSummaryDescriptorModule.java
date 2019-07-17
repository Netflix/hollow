package com.netflix.vms.transformer.modules.packages;

//TODO: enable me once we can turn on the new data set including follow vip functionality
//import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.Dataset.OSCAR;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.vms.transformer.hollowinput.StreamProfilesHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowinput.VideoGeneralHollow;
import com.netflix.vms.transformer.hollowoutput.DownloadableId;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptor;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptorData;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.StreamData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TimedTextTypeDescriptor;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.datasets.OscarDataset;
import com.netflix.vms.transformer.modules.ModuleDataSourceTransitionUtil;
import com.netflix.vms.transformer.modules.mpl.AudioChannelsDescriptorCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EncodeSummaryDescriptorModule {

    /// represents bits for "PartiallyDeployedReplacement" and "DoNotPlay" -- see deploymentLabel creation in StreamDataModule.
    private static final int DEPLOYMENT_LABELS_TO_EXCLUDE_FROM_SUMMARY = 0x11;

    private final TimedTextTypeDescriptor SUBTITLES = new TimedTextTypeDescriptor("Subtitles");
    
    private final AudioChannelsDescriptorCache audioChannelsDescriptorCache = new AudioChannelsDescriptorCache();


    private final VMSHollowInputAPI api;
    private final HollowPrimaryKeyIndex streamProfileIdx;
    private final HollowPrimaryKeyIndex videoGeneralIdx;
//    private final OscarDataset oscarDataset;


    public EncodeSummaryDescriptorModule(VMSHollowInputAPI api, VMSTransformerIndexer indexer, UpstreamDatasetHolder upstream) {
        this.api = api;
        this.streamProfileIdx = indexer.getPrimaryKeyIndex(IndexSpec.STREAM_PROFILE);
        this.videoGeneralIdx = indexer.getPrimaryKeyIndex(IndexSpec.VIDEO_GENERAL);
//        this.oscarDataset = upstream.getHashIndex(OSCAR);
    }


    public void summarize(PackageData packageData) {
        Map<EncodeSummaryDescriptorDataKey, EncodeSummaryDescriptor> descriptorMap = new HashMap<EncodeSummaryDescriptorDataKey, EncodeSummaryDescriptor>();

        for(StreamData stream : packageData.streams) {

            /// exclude PartiallyDeployedReplacement,DoNotPlay
            if((stream.additionalData.mostlyConstantData.deploymentLabel & DEPLOYMENT_LABELS_TO_EXCLUDE_FROM_SUMMARY) != 0)
                continue;

            int streamProfileOrdinal = streamProfileIdx.getMatchingOrdinal((long)stream.downloadDescriptor.encodingProfileId);

            if(streamProfileOrdinal == -1)
                continue;

            StreamProfilesHollow profile = api.getStreamProfilesHollow(streamProfileOrdinal);
            String profileType = profile._getProfileType()._getValue();

            String language = stream.downloadDescriptor.audioLanguageBcp47code != null ? new String(stream.downloadDescriptor.audioLanguageBcp47code.value) : stream.downloadDescriptor.textLanguageBcp47code != null ? new String(stream.downloadDescriptor.textLanguageBcp47code.value) : null;

            if(language == null)
                continue;
            
            int numAudioChannels = (int)profile._getAudioChannelCount();

            EncodeSummaryDescriptorData data = new EncodeSummaryDescriptorData();
            data.assetType = stream.downloadDescriptor.assetTypeDescriptor;
            data.assetMetaData = stream.downloadDescriptor.assetMetaData;
            data.timedTextType = stream.downloadDescriptor.timedTextTypeDescriptor;
            data.audioLanguage = stream.downloadDescriptor.audioLanguageBcp47code;
            data.textLanguage = stream.downloadDescriptor.textLanguageBcp47code;
            data.encodingProfileId = stream.downloadDescriptor.encodingProfileId;
            data.audioChannels = audioChannelsDescriptorCache.getAudioChannels(numAudioChannels);

            String nativeLanguage = getNativeLanguage(packageData.video.value);

//            String nativeLanguage = (ModuleDataSourceTransitionUtil.useOscarFeedVideoGeneral()) ?
//                    getNativeLanguageOscar(packageData.video.value)
//                    : getNativeLanguage(packageData.video.value);

            data.isNative = language != null && language.equals(nativeLanguage);
            data.isSubtitleBurnedIn = isSubtitleBurnedIn(profileType, data.textLanguage);

            // if MUXED or AUDIO
            if(isAudio(profileType)) {
                EncodeSummaryDescriptorData audioData = data;
                if(data.textLanguage != null || data.timedTextType != null || data.isSubtitleBurnedIn) {
                    audioData = data.clone();
                    audioData.textLanguage = null;
                    audioData.timedTextType = null;
                    audioData.isSubtitleBurnedIn = false;
                }

                EncodeSummaryDescriptorDataKey key = new EncodeSummaryDescriptorDataKey(audioData, numAudioChannels, SummaryType.AUDIO);
                addDownloadableIdToDescriptor(key, profileType, stream, descriptorMap);
            }

            // if TEXT or VIDEO
            if(isText(profileType)) {
                if(data.timedTextType == null) {
                    data = data.clone();
                    data.timedTextType = SUBTITLES;
                }

                EncodeSummaryDescriptorDataKey key = new EncodeSummaryDescriptorDataKey(data, 0, SummaryType.TEXT);
                addDownloadableIdToDescriptor(key, profileType, stream, descriptorMap);
            }

            // if MUXED
            if(isMuxed(profileType)) {
                if(data.timedTextType == null && data.textLanguage != null) {
                    data = data.clone();
                    data.timedTextType = SUBTITLES;
                }
                EncodeSummaryDescriptorDataKey key = new EncodeSummaryDescriptorDataKey(data, 0, SummaryType.MUXED);
                addDownloadableIdToDescriptor(key, profileType, stream, descriptorMap);
            }
        }

        packageData.textStreamSummary = new HashSet<>();
        packageData.audioStreamSummary = new HashSet<>();
        packageData.muxAudioStreamSummary = new HashSet<>();

        for(Map.Entry<EncodeSummaryDescriptorDataKey, EncodeSummaryDescriptor> entry : descriptorMap.entrySet()) {
            if(entry.getKey().getSummaryType() == SummaryType.TEXT) {
                packageData.textStreamSummary.add(entry.getValue());
            } else if(entry.getKey().getSummaryType() == SummaryType.AUDIO) {
                packageData.audioStreamSummary.add(entry.getValue());
            } else if(entry.getKey().getSummaryType() == SummaryType.MUXED) {
                packageData.muxAudioStreamSummary.add(entry.getValue());
            }
        }

    }

    private void addDownloadableIdToDescriptor(EncodeSummaryDescriptorDataKey key, String profileType, StreamData stream, Map<EncodeSummaryDescriptorDataKey, EncodeSummaryDescriptor> descriptorMap) {
        EncodeSummaryDescriptor descriptor = descriptorMap.get(key);

        if(descriptor == null) {
            descriptor = new EncodeSummaryDescriptor();
            descriptor.descriptorData = key.data;
            descriptor.downloadableIds = new ArrayList<DownloadableId>();
            descriptor.fromMuxedOnlyStreams = true;

            descriptorMap.put(key, descriptor);
        } 

        if(key.data.encodingProfileId < descriptor.descriptorData.encodingProfileId)
            descriptor.descriptorData.encodingProfileId = key.data.encodingProfileId;

        descriptor.fromMuxedOnlyStreams = descriptor.fromMuxedOnlyStreams && "MUXED".equals(profileType);
        descriptor.downloadableIds.add(stream.downloadableId);
    }

    private String getNativeLanguage(long videoId) {
        int ordinal = videoGeneralIdx.getMatchingOrdinal(videoId);
        if (ordinal == -1) return null;

        VideoGeneralHollow general = api.getVideoGeneralHollow(ordinal);
        StringHollow originalLangCode = general._getOriginalLanguageBcpCode();
        if(originalLangCode != null)
            return originalLangCode._getValue();
        return null;
    }

//    private String getNativeLanguageOscar(long videoId) {
//        return oscarDataset.mapWithMovieIfExists(videoId,(movie)-> movie.getOriginalLanguageBcpCode()).orElse(null);
//    }

    private boolean isSubtitleBurnedIn(String profileType, Strings language) {
        if(language != null) {
            return "VIDEO".equals(profileType) || "MUXED".equals(profileType);
        }

        return false;
    }

    private boolean isText(String streamProfileType) {
        return "TEXT".equals(streamProfileType) || "VIDEO".equals(streamProfileType);
    }

    private boolean isAudio(String streamProfileType) {
        return "AUDIO".equals(streamProfileType) || "MUXED".equals(streamProfileType);
    }

    private boolean isMuxed(String streamProfileType) {
        return "MUXED".equals(streamProfileType);
    }


    private static class EncodeSummaryDescriptorDataKey {

        private final EncodeSummaryDescriptorData data;
        private final int audioChannels;
        private final int hashCode;
        private final SummaryType summaryType;

        public EncodeSummaryDescriptorDataKey(EncodeSummaryDescriptorData data, int audioChannels, SummaryType summaryType) {
            int hashCode = 0;

            if(data.assetType != null)
                hashCode = (hashCode * 31) + data.assetType.id;

            if(summaryType == SummaryType.TEXT || summaryType == SummaryType.MUXED) {
                if(data.timedTextType != null)
                    hashCode = (hashCode * 31) + Arrays.hashCode(data.timedTextType.nameStr);
                if(data.textLanguage != null)
                    hashCode = (hashCode * 31) + Arrays.hashCode(data.textLanguage.value);
                if(data.isSubtitleBurnedIn)
                    hashCode *= 31;
            }
            if(summaryType == SummaryType.AUDIO || summaryType == SummaryType.MUXED) {
                hashCode = (hashCode * 31) + audioChannels;
                if(data.audioLanguage != null)
                    hashCode = (hashCode * 31) + Arrays.hashCode(data.audioLanguage.value);
            }


            this.data = data;
            this.hashCode = hashCode;

            this.audioChannels = audioChannels;
            this.summaryType = summaryType;
        }

        public SummaryType getSummaryType() {
            return summaryType;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof EncodeSummaryDescriptorDataKey) {
                EncodeSummaryDescriptorDataKey other = ((EncodeSummaryDescriptorDataKey) obj);

                if(summaryType != other.summaryType)
                    return false;

                if(data.assetType == null) {
                    if(other.data.assetType != null)
                        return false;
                } else {
                    if(!data.assetType.equals(other.data.assetType))
                        return false;
                }

                if(summaryType == SummaryType.TEXT || summaryType == SummaryType.MUXED) {
                    if(data.timedTextType == null) {
                        if(other.data.timedTextType != null)
                            return false;
                    } else {
                        if(!data.timedTextType.equals(other.data.timedTextType))
                            return false;
                    }

                    if(data.textLanguage == null) {
                        if(other.data.textLanguage != null)
                            return false;
                    } else {
                        if(!data.textLanguage.equals(other.data.textLanguage))
                            return false;
                    }

                    if(data.isSubtitleBurnedIn != other.data.isSubtitleBurnedIn)
                        return false;
                }

                if(summaryType == SummaryType.AUDIO || summaryType == SummaryType.MUXED) {
                    if(audioChannels != other.audioChannels)
                        return false;

                    if(data.audioLanguage == null) {
                        if(other.data.audioLanguage != null)
                            return false;
                    } else {
                        if(!data.audioLanguage.equals(other.data.audioLanguage))
                            return false;
                    }
                }


                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static enum SummaryType {
        TEXT,
        AUDIO,
        MUXED
    }

}
