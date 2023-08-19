package hollow.model;

public class Art {
    String id;

    Artist artist;

    long timeOfCreation;
    long size;

    public Art(String id, Artist artist, long timeOfCreation, long size) {
        this.id = id;
        this.artist = artist;
        this.timeOfCreation = timeOfCreation;
        this.size = size;
    }
}
