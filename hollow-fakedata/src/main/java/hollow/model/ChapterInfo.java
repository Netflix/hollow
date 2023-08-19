package hollow.model;

public class ChapterInfo {
    BookId bookId;
    int pages;
    byte[] content;

    public ChapterInfo(BookId bookId, int pages, byte[] content) {
        this.bookId = bookId;
        this.pages = pages;
        this.content = content;
    }
}
