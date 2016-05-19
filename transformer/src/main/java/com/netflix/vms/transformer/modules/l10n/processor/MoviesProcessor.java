package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.MoviesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class MoviesProcessor extends AbstractL10NProcessor<MoviesHollow> {

    public MoviesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<MoviesHollow> getInputs() {
        return api.getAllMoviesHollow();
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