package com.netflix.vms.transformer.modules.l10n;

/*
 * Represents a type of hook which represents additional text in a given category
 */
public enum HookType {
    TV_RATINGS(0, "TV Ratings Hook"),
    AWARDS_CRITICAL_PRAISE(1, "Awards/Critical Praise Hook"),
    BOX_OFFICE(2, "Box Office Hook"),
    TALENT_ACTORS(3, "Talent/Actors Hook"),
    UNKNOWN(-1, "Unknown");

    final private int id;
    final private String name;

    private HookType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HookType toHookType(String name) {
        for(HookType type : values()) {
            if(type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int getId() {
        return id;
    }


}
