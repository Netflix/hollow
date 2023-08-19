package hollow.model;

import java.util.List;

public class BookMetadata {
    public String name;
    public Genre genre;
    public List<Chapter> chapters;

    public BookMetadata(String name, Genre genre, List<Chapter> chapters) {
        this.name = name;
        this.genre = genre;
        this.chapters = chapters;
    }
}
