package com.netflix.vms.transformer.util;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.ISOCountry;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalInfoType;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.VRole;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class VMSTransformerHashCodeFinder implements HollowObjectHashCodeFinder {

    static enum RecordType {
        ArtWorkImageTypeEntry,
        ArtWorkImageFormatEntry,
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
        VRole,
        Video,
        VideoFormatDescriptor,
        VideoSetType;

        static final Map<String, RecordType> nameToRecordType =
                Arrays.stream(values()).collect(toMap(RecordType::name, identity()));
    }

    public VMSTransformerHashCodeFinder() {}

    @Override
    public int hashCode(String typeName, int ordinal, Object objectToHash) {
        RecordType recordType = RecordType.nameToRecordType.get(typeName);

        if (recordType == null)
            return ordinal;

        switch(recordType) {
        case DrmKeyString:
            return stringHashCode(((DrmKeyString)objectToHash).value);
        case Episode:
            return ((Episode)objectToHash).id;
        case Integer:
            return ((com.netflix.vms.transformer.hollowoutput.Integer)objectToHash).val;
        case ISOCountry:
            return stringHashCode(((ISOCountry)objectToHash).id);
        case Long:
            return Long.hashCode(((com.netflix.vms.transformer.hollowoutput.Long)objectToHash).val);
        case NFLocale:
            return stringHashCode(((NFLocale)objectToHash).value);
        case Strings:
            return stringHashCode(((Strings)objectToHash).value);
        case SupplementalInfoType:
            return stringHashCode(((SupplementalInfoType)objectToHash).value);
        case TrickPlayType:
            return stringHashCode(((TrickPlayType)objectToHash).value);
        case VPerson:
            return ((VPerson)objectToHash).id;
        case VRole:
            return ((VRole)objectToHash).id;
        case Video:
            return ((Video)objectToHash).value;
        case VideoFormatDescriptor:
            return ((VideoFormatDescriptor)objectToHash).id;
        case VideoSetType:
            return stringHashCode(((VideoSetType)objectToHash).value);
        case ArtWorkImageTypeEntry:
            return stringHashCode(((ArtWorkImageTypeEntry)objectToHash).nameStr);
        case ArtWorkImageFormatEntry:
            return stringHashCode(((ArtWorkImageFormatEntry)objectToHash).nameStr);
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
        return RecordType.nameToRecordType.keySet();
    }

    @Deprecated
    @Override
    public int hashCode(int ordinal, Object objectToHash) {
        throw new UnsupportedOperationException();
    }
    
    private static int stringHashCode(char[] str) {
        int h = 0;
        for(int i=0;i<str.length;i++)
            h = 31*h + str[i];
        return h;
    }

}
