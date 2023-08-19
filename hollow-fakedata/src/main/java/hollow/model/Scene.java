package hollow.model;

import java.util.Set;

public class Scene {
    String description;
    long popularity;
    Set<String> characters;

    public Scene(String description, long popularity, Set<String> characters) {
        this.description = description;
        this.popularity = popularity;
        this.characters = characters;
    }
}
