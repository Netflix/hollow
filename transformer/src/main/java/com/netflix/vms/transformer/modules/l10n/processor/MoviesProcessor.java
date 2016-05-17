package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.MoviesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class MoviesProcessor extends AbstractL10NProcessor {

    public MoviesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public void processResources() {
        for (MoviesHollow item : api.getAllMoviesHollow()) {
            final int itemId = (int) item._getMovieId();

            {
                final String resourceId = L10nResourceIdLookup.getMovieTitleID(itemId);
                addL10NResources(resourceId, item._getTransliterated()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieShortTitleID(itemId);
                addL10NResources(resourceId, item._getShortDisplayName()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieSynopsisID(itemId);
                addL10NResources(resourceId, item._getSiteSynopsis()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieTVSynopsisID(itemId);
                addL10NResources(resourceId, item._getTvSynopsis()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieASCIITitleID(itemId);
                // @TODO: No input?
                //addL10NResources(resourceId, item._getTransliterated()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieOriginalTitleID(itemId);
                addL10NResources(resourceId, item._getOriginalTitle()._getTranslatedTexts());

            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieAkaTitleResourceID(itemId);
                addL10NResources(resourceId, item._getAka()._getTranslatedTexts());
            }

            {
                final String resourceId = L10nResourceIdLookup.getMovieTransliteratedTitleResourceID(itemId);
                addL10NResources(resourceId, item._getTransliterated()._getTranslatedTexts());
            }

        }
    }
}