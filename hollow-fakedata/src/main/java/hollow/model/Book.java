package hollow.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields = {"id", "country"})
public class Book {
    public BookId id;
    public Country country;
    public BookImages images;
    public BookMetadata bookMetadata;

    public Book(BookId id, Country country, BookImages images, BookMetadata bookMetadata) {
        this.id = id;
        this.country = country;
        this.images = images;
        this.bookMetadata = bookMetadata;
    }
}
