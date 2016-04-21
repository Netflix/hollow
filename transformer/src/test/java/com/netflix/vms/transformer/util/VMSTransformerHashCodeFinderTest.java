package com.netflix.vms.transformer.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.DrmKeyString;
import com.netflix.vms.transformer.hollowoutput.Episode;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.SupplementalInfoType;
import com.netflix.vms.transformer.hollowoutput.TrickPlayType;
import com.netflix.vms.transformer.hollowoutput.VPerson;
import com.netflix.vms.transformer.hollowoutput.Video;
import com.netflix.vms.transformer.hollowoutput.VideoFormatDescriptor;
import com.netflix.vms.transformer.hollowoutput.VideoSetType;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder.RecordType;

public class VMSTransformerHashCodeFinderTest {

    private VMSTransformerHashCodeFinder subject;

    @Before
    public void initSubject() {
        subject = new VMSTransformerHashCodeFinder();
    }

    @Test
    public void unknownRecordTypeHashesToOrdinal() {
        int ordinal = 13;
        assertThat(subject.hashCode(null, ordinal, new Object()))
            .isEqualTo(ordinal);
    }

    @Test
    public void drmKeyHashesToStringHashCode() {
        String value = "BADF00D";
        DrmKeyString ks = new DrmKeyString(value);

        assertThat(subject.hashCode(RecordType.DrmKeyString.name(), anyOrdinal(), ks))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void episodeHashesToId() {
        int id = 42;
        Episode ep = new Episode(id);

        assertThat(subject.hashCode(RecordType.Episode.name(), anyOrdinal(), ep))
            .isEqualTo(id);
    }

    @Test
    public void hollowIntegerHashesToValue() {
        int value = 17;
        com.netflix.vms.transformer.hollowoutput.Integer i =
                new com.netflix.vms.transformer.hollowoutput.Integer(value);

        assertThat(subject.hashCode(RecordType.Integer.name(), anyOrdinal(), i))
            .isEqualTo(value);
    }

    @Test
    public void hollowLongHashesToLongValueHashCode() {
        Long value = 19L;
        com.netflix.vms.transformer.hollowoutput.Long l =
                new com.netflix.vms.transformer.hollowoutput.Long(value);

        assertThat(subject.hashCode(RecordType.Long.name(), anyOrdinal(), l))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void localeHashesToLocaleStringHashCode() {
        String localeString = "MX";
        NFLocale locale = new NFLocale(localeString);

        assertThat(subject.hashCode(RecordType.NFLocale.name(), anyOrdinal(), locale))
            .isEqualTo(localeString.hashCode());
    }

    @Test
    public void hollowStringHashesToStringValueHashCode() {
        String value = "Pancakes";
        Strings s = new Strings(value);

        assertThat(subject.hashCode(RecordType.Strings.name(), anyOrdinal(), s))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void supplementalInfoHashesToStringValueHashCode() {
        String value = "'Sup";
        SupplementalInfoType info = new SupplementalInfoType(value);

        assertThat(subject.hashCode(RecordType.SupplementalInfoType.name(), anyOrdinal(), info))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void trickPlayHashesToStringValueHashCode() {
        String value = "TreatWork";
        TrickPlayType trick = new TrickPlayType(value);

        assertThat(subject.hashCode(RecordType.TrickPlayType.name(), anyOrdinal(), trick))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void vPersonHashesToId() {
        int id = 1010101;
        VPerson person = new VPerson(id);

        assertThat(subject.hashCode(RecordType.VPerson.name(), anyOrdinal(), person))
            .isEqualTo(id);
    }

    @Test
    public void videoHashesToItsOwnHashCode() {
        Video v = new Video(99); // …luftballons

        assertThat(subject.hashCode(RecordType.Video.name(), anyOrdinal(), v))
            .isEqualTo(v.hashCode());
    }

    @Test
    public void vidFormatDescriptorHashesToId() {
        int id = 23;
        VideoFormatDescriptor vfd = new VideoFormatDescriptor();
        vfd.id = id;

        assertThat(subject.hashCode(RecordType.VideoFormatDescriptor.name(), anyOrdinal(), vfd))
            .isEqualTo(id);
    }

    @Test
    public void videoSetHashesToStringValueHashCode() {
        String value = "ready, set, go!";
        VideoSetType vst = new VideoSetType(value);

        assertThat(subject.hashCode(RecordType.VideoSetType.name(), anyOrdinal(), vst))
            .isEqualTo(value.hashCode());
    }

    @Test
    public void artworkImageHashesToNameHashCode() {
        String name = "the-starry-night";
        ArtWorkImageTypeEntry artwork = new ArtWorkImageTypeEntry();
        artwork.nameStr = name.toCharArray();

        assertThat(subject.hashCode(RecordType.ArtWorkImageTypeEntry.name(), anyOrdinal(), artwork))
            .isEqualTo(name.hashCode());
    }

    @Test
    public void definedTypes() {
        // not intended to test exhaustively
        assertThat(subject.getTypesWithDefinedHashCodes()).contains(
                RecordType.ArtWorkImageTypeEntry.name(),
                RecordType.Integer.name());
    }

    private final int anyOrdinal() {
        return -1;
    }
}
