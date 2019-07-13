package com.netflix.vms.transformer.data.gen.mclEarliestDate;

import com.netflix.hollow.api.testdata.HollowTestDataMapEntry;
import com.netflix.hollow.api.testdata.HollowTestDataset;

public class MclEarliestDateTestData extends HollowTestDataset {

    public FeedMovieCountryLanguagesTestData FeedMovieCountryLanguages(FeedMovieCountryLanguagesTestData.FeedMovieCountryLanguagesField... fields) {
        FeedMovieCountryLanguagesTestData rec = FeedMovieCountryLanguagesTestData.FeedMovieCountryLanguages(fields);
        add(rec);
        return rec;
    }

    public LongTestData Long(LongTestData.LongField... fields) {
        LongTestData rec = LongTestData.Long(fields);
        add(rec);
        return rec;
    }

    @SafeVarargs
    public final MapOfStringToLongTestData MapOfStringToLong(HollowTestDataMapEntry<StringTestData, LongTestData>... entries) {
        MapOfStringToLongTestData rec = MapOfStringToLongTestData.MapOfStringToLong(entries);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

}