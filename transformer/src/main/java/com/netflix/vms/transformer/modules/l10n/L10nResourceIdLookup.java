package com.netflix.vms.transformer.modules.l10n;

import com.netflix.i18n.NFResourceID;
import com.netflix.videometadata.type.HookType;

public class L10nResourceIdLookup {

    public static NFResourceID getHookTextId(final Integer id, final HookType type) {
        return new NFResourceID("m" + id + ".h" + "." + type.getId());
    }

    public static NFResourceID getNarrativeTextId(final Integer id) {
        return new NFResourceID("m" + id + ".n");
    }

    public static NFResourceID getTrackLabelId(final String id) {
        return new NFResourceID("s" + id + ".l");
    }

    public static NFResourceID getGenericResourceId(final Integer id, final String resourceIdPrefix, final String attribName) {
        return new NFResourceID(resourceIdPrefix + id + "." + attribName);
    }

    public static NFResourceID getNFResourceID(final String value) {
        return new NFResourceID(value);
    }

    public static String createRolloutAttribResourceId(final Integer id, final String name, final String label) {
        return String.format("rv_%d_%s_%s", id, name, label);
    }

    public static String createCharacterAttribResourceId(final Integer id, final String name, final String label) {
        return String.format("rc_%d_%s_%s", id, name, label);
    }

    public static NFResourceID getRolloutAttribResourceId(final Integer id, final String name, final String label) {
        return new NFResourceID(createRolloutAttribResourceId(id, name, label));
    }

    public static NFResourceID getCharacterAttribResourceId(final Integer id, final String name, final String label) {
        return new NFResourceID(createCharacterAttribResourceId(id, name, label));
    }

    public static NFResourceID getMovieAkaTitleResourceID(Integer id) {
        return new NFResourceID("m" + id + ".ak");
    }

    public static NFResourceID getMovieTransliteratedTitleResourceID(Integer id) {
        return new NFResourceID("m" + id + ".tl");
    }

    public static NFResourceID getPersonAkaNameResourceID(Integer id) {
        return new NFResourceID("p" + id + ".ak");
    }

    public static NFResourceID getPersonTransliteratedNameResourceID(Integer id) {
        return new NFResourceID("p" + id + ".tl");
    }
}