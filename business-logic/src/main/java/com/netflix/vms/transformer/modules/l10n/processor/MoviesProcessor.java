package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.MoviesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class MoviesProcessor extends AbstractL10NVideoProcessor<MoviesHollow> {

    public MoviesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper, indexer, IndexSpec.L10N_MOVIES);
    }

    @Override
    protected MoviesHollow getDataForOrdinal(int ordinal) {
        return api.getMoviesHollow(ordinal);
    }

    @Override
    public void processInput(MoviesHollow input) {
        final int inputId = (int) input._getMovieId();

        {
            final String resourceId = L10nResourceIdLookup.getMovieTitleID(inputId);
            addL10NResources(resourceId, input._getDisplayName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getMovieShortTitleID(inputId);
            addL10NResources(resourceId, input._getShortDisplayName());
        }

        {
            final String resourceId = L10nResourceIdLookup.getMovieSynopsisID(inputId);
            addL10NResources(resourceId, input._getSiteSynopsis());
        }

        {
            final String resourceId = L10nResourceIdLookup.getMovieTVSynopsisID(inputId);
            addL10NResources(resourceId, input._getTvSynopsis());
        }

        //            {
        //                final String resourceId = L10nResourceIdLookup.getMovieASCIITitleID(inputId);
        //                addL10NResources(resourceId, input._???);
        //            }

        {
            final String resourceId = L10nResourceIdLookup.getMovieOriginalTitleID(inputId);
            addL10NResources(resourceId, input._getOriginalTitle());

        }

        {
            final String resourceId = L10nResourceIdLookup.getMovieAkaTitleResourceID(inputId);
            addL10NResources(resourceId, input._getAka());
        }

        {
            final String resourceId = L10nResourceIdLookup.getMovieTransliteratedTitleResourceID(inputId);
            addL10NResources(resourceId, input._getTransliterated());
        }
    }
}