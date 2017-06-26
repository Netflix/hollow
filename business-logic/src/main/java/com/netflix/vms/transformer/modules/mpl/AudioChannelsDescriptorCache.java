package com.netflix.vms.transformer.modules.mpl;

import com.netflix.vms.transformer.hollowoutput.AudioChannelsDescriptor;
import com.netflix.vms.transformer.hollowoutput.Strings;
import java.util.concurrent.ConcurrentHashMap;

public class AudioChannelsDescriptorCache {
    
    private final ConcurrentHashMap<Integer, AudioChannelsDescriptor> audioChannelsMap = new ConcurrentHashMap<>();

    public AudioChannelsDescriptor getAudioChannels(final int channels) {

        AudioChannelsDescriptor result = audioChannelsMap.get(channels);
        if (result != null) return result;

        switch(channels) {
            case 0:
                result = newAudioChannelsDescriptor(0, "", "");
                break;
            case 1:
                result = newAudioChannelsDescriptor(1, "1.0", "Mono");
                break;
            case 2:
                result = newAudioChannelsDescriptor(2, "2.0", "Stereo");
                break;
            case 6:
                result = newAudioChannelsDescriptor(6, "5.1", "Dolby Digital Plus");
                break;
            case 8:
                result = newAudioChannelsDescriptor(8, "7.1", "Dolby Digital Plus");
                break;
            default:
                result = newAudioChannelsDescriptor(-1, "UNKNOWN", "");
                break;
        }

        AudioChannelsDescriptor existing = audioChannelsMap.putIfAbsent(channels, result);
        if(existing != null)
            return existing;
        
        return result;
    }

    private static AudioChannelsDescriptor newAudioChannelsDescriptor(final int numberOfChannels, final String name, final String description) {
        AudioChannelsDescriptor result = new AudioChannelsDescriptor();
        result.numberOfChannels = numberOfChannels;
        result.name = name == null ? null : new Strings(name);
        result.description = description == null ? null : new Strings(description);
        return result;
    }

}
