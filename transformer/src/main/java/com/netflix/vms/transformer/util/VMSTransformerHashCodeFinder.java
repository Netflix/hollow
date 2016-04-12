package com.netflix.vms.transformer.util;

import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;

import com.netflix.hollow.util.HollowObjectHashCodeFinder;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalInfoType;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VMSTransformerHashCodeFinder implements HollowObjectHashCodeFinder {

    private static enum RecordType {
        ArtWorkImageTypeEntry,
        DrmKeyString,
        Episode,
        ISOCountry,
        Integer,
        Long,
        NFLocale,
        Strings,
        SupplementalInfoType,
        TrickPlayType,
        VPerson,
        Video,
        VideoFormatDescriptor,
        VideoSetType;
    }


    private final Map<String, RecordType> definedHashCodeTypes;
    private final Set<String> typesWithDefinedHashCodes;

    public VMSTransformerHashCodeFinder() {
        Map<String, RecordType> recordTypes = new HashMap<String, RecordType>();

        for(RecordType recordType : RecordType.values())
            recordTypes.put(recordType.toString(), recordType);

        this.definedHashCodeTypes = new HashMap<String, RecordType>();
        this.typesWithDefinedHashCodes = Collections.unmodifiableSet(definedHashCodeTypes.keySet());
    }

    @Override
    public int hashCode(String typeName, int ordinal, Object objectToHash) {
        RecordType recordType = definedHashCodeTypes.get(typeName);

        if(recordType == null)
            return ordinal;

        switch(recordType) {
        case DrmKeyString:
            return new String(((DrmKeyString)objectToHash).value).hashCode();
        case Episode:
            return ((Episode)objectToHash).id;
        case Integer:
            return ((com.netflix.vms.transformer.hollowoutput.Integer)objectToHash).val;
        case ISOCountry:
            return new String(((ISOCountry)objectToHash).id).hashCode();
        case Long:
            return Long.hashCode(((com.netflix.vms.transformer.hollowoutput.Long)objectToHash).val);
        case NFLocale:
            return new String(((NFLocale)objectToHash).value).hashCode();
        case Strings:
            return new String(((Strings)objectToHash).value).hashCode();
        case SupplementalInfoType:
            return new String(((SupplementalInfoType)objectToHash).value).hashCode();
        case TrickPlayType:
            return new String(((TrickPlayType)objectToHash).value).hashCode();
        case VPerson:
            return ((VPerson)objectToHash).id;
        case Video:
            return ((Video)objectToHash).hashCode();
        case VideoFormatDescriptor:
            return ((VideoFormatDescriptor)objectToHash).id;
        case VideoSetType:
            return new String(((VideoSetType)objectToHash).value).hashCode();
        case ArtWorkImageTypeEntry:
            return new String(((ArtWorkImageTypeEntry)objectToHash).nameStr).hashCode();
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int hashCode(Object objectToHash) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getTypesWithDefinedHashCodes() {
        return typesWithDefinedHashCodes;
    }

    @Deprecated
    @Override
    public int hashCode(int ordinal, Object objectToHash) {
        throw new UnsupportedOperationException();
    }

}
