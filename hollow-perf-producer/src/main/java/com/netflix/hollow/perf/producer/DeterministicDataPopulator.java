package com.netflix.hollow.perf.producer;

import com.netflix.hollow.perf.producer.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class DeterministicDataPopulator {

    private static final String[] COUNTRIES = {
            "US", "CA", "MX", "BE", "BZ", "BJ", "BM", "BT", "BR", "BG",
            "BI", "CV", "KH", "CM", "CL", "CN", "CW", "CY", "CZ", "DK",
            "DJ", "DM", "EC", "EG", "SV", "ER", "EE", "SZ", "ET", "FI",
            "FR", "GA", "GE", "DE", "GH", "GI", "GR", "GL", "GD", "GP",
            "GT", "GG", "GN", "GY", "HT", "HK", "HU", "IS", "IN", "ID"
    };

    private static final String[] CITY_NAMES = {
            "NewYork", "London", "Tokyo", "Paris", "Berlin", "Sydney", "Toronto",
            "Mumbai", "Shanghai", "SaoPaulo", "Cairo", "Lagos", "Moscow", "Seoul",
            "MexicoCity", "Jakarta", "Istanbul", "BuenosAires", "Nairobi", "Bangkok"
    };

    private final int numBooks;
    private final int countriesPerBook;
    private final int chaptersPerBook;
    private final int chapterContentSize;
    private final int scenesPerChapter;
    private final int charactersPerScene;
    private final int numArtists;
    private final int addsPerCycle;
    private final int removesPerCycle;
    private final int modificationsPerCycle;

    private final List<Artist> artists;
    private List<Book> books;
    private int nextBookId;

    public DeterministicDataPopulator(int numBooks, int countriesPerBook, int chaptersPerBook,
                                      int chapterContentSize, int scenesPerChapter,
                                      int charactersPerScene, int numArtists,
                                      int addsPerCycle, int removesPerCycle,
                                      int modificationsPerCycle) {
        this.numBooks = numBooks;
        this.countriesPerBook = countriesPerBook;
        this.chaptersPerBook = chaptersPerBook;
        this.chapterContentSize = chapterContentSize;
        this.scenesPerChapter = scenesPerChapter;
        this.charactersPerScene = charactersPerScene;
        this.numArtists = numArtists;
        this.addsPerCycle = addsPerCycle;
        this.removesPerCycle = removesPerCycle;
        this.modificationsPerCycle = modificationsPerCycle;
        this.artists = generateArtists();
        this.books = new ArrayList<>();
        this.nextBookId = 0;
    }

    private List<Artist> generateArtists() {
        List<Artist> list = new ArrayList<>(numArtists);
        for (int i = 0; i < numArtists; i++) {
            list.add(new Artist(
                    "Artist_" + String.format("%05d", i),
                    CITY_NAMES[i % CITY_NAMES.length]
            ));
        }
        return list;
    }

    public List<Book> generateInitialCatalog() {
        books = new ArrayList<>(numBooks * countriesPerBook);
        for (int i = 0; i < numBooks; i++) {
            addBooksForId(nextBookId++);
        }
        return books;
    }

    private void addBooksForId(int bookId) {
        for (int c = 0; c < countriesPerBook; c++) {
            int countryIdx = (bookId + c) % COUNTRIES.length;
            Country country = new Country(COUNTRIES[countryIdx]);
            BookImages images = buildImages(bookId, COUNTRIES[countryIdx]);
            BookMetadata metadata = buildMetadata(bookId);
            books.add(new Book(new BookId(bookId), country, images, metadata));
        }
    }

    private BookImages buildImages(int bookId, String countryCode) {
        Map<String, List<Art>> artMap = new HashMap<>();
        String[] sizes = {"small", "medium", "large"};
        for (int s = 0; s < sizes.length; s++) {
            Artist artist = artists.get((bookId + s) % numArtists);
            Art art = new Art(
                    "art_" + bookId + "_" + countryCode + "_" + sizes[s],
                    artist,
                    1000000L + bookId,
                    500L + (bookId * 7L + s * 13L) % 10000
            );
            artMap.put(sizes[s], Collections.singletonList(art));
        }
        return new BookImages(artMap);
    }

    private BookMetadata buildMetadata(int bookId) {
        Genre genre = Genre.values()[bookId % Genre.values().length];
        List<Chapter> chapters = new ArrayList<>(chaptersPerBook);
        for (int ch = 0; ch < chaptersPerBook; ch++) {
            chapters.add(buildChapter(bookId, ch));
        }
        return new BookMetadata("Book_" + String.format("%05d", bookId), genre, chapters);
    }

    private Chapter buildChapter(int bookId, int chapterIdx) {
        ChapterId chapterId = new ChapterId("ch_" + bookId + "_" + String.format("%02d", chapterIdx));
        int pages = 100 + (bookId * 3 + chapterIdx * 7) % 900;
        byte[] content = deterministicContent(bookId, chapterIdx, chapterContentSize);
        ChapterInfo chapterInfo = new ChapterInfo(new BookId(bookId), pages, content);

        List<Scene> scenes = new ArrayList<>(scenesPerChapter);
        for (int sc = 0; sc < scenesPerChapter; sc++) {
            scenes.add(buildScene(bookId, chapterIdx, sc));
        }
        return new Chapter(chapterId, chapterInfo, scenes);
    }

    private Scene buildScene(int bookId, int chapterIdx, int sceneIdx) {
        String description = "Scene_" + bookId + "_" + chapterIdx + "_" + sceneIdx
                + ": A tale of adventure in " + CITY_NAMES[(bookId + sceneIdx) % CITY_NAMES.length];
        long popularity = (bookId * 1000L + chapterIdx * 100L + sceneIdx * 10L) % 100000;
        Set<String> characters = new LinkedHashSet<>();
        for (int i = 0; i < charactersPerScene; i++) {
            characters.add("Character_" + String.format("%05d", (bookId * 7 + chapterIdx * 3 + sceneIdx + i) % 50000));
        }
        return new Scene(description, popularity, characters);
    }

    static byte[] deterministicContent(int bookId, int chapterIdx, int size) {
        byte[] content = new byte[size];
        int seed = bookId * 1000 + chapterIdx;
        for (int i = 0; i < size; i++) {
            content[i] = (byte) ((seed + i * 31) & 0xFF);
        }
        return content;
    }

    /**
     * Applies deterministic adds, removes, and modifications to the book catalog for a given cycle.
     * Uses a seeded RNG so the same cycleNum always produces the same mutations, enabling reproducible benchmarks.
     *
     * Mutation order: removes -> modifications -> adds (adds use fresh IDs that won't collide with removes/mods).
     */
    public void applyDeltaModifications(int cycleNum) {
        // Seed RNG per cycle for deterministic, reproducible mutations
        Random rng = new Random(42L + cycleNum);

        // Deduplicate book IDs (each ID appears once per country) to operate at the logical book level
        Set<Integer> bookIdSet = books.stream()
                .map(b -> b.id.value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Integer> bookIdList = new ArrayList<>(bookIdSet);

        // Pick random book IDs to remove (skip if catalog is too small)
        Set<Integer> toRemove = new HashSet<>();
        if (bookIdList.size() > removesPerCycle) {
            for (int i = 0; i < removesPerCycle; i++) {
                toRemove.add(bookIdList.get(rng.nextInt(bookIdList.size())));
            }
        }

        // Pick random book IDs to modify, excluding any already marked for removal
        Set<Integer> toModify = new HashSet<>();
        for (int i = 0; i < modificationsPerCycle; i++) {
            int id = bookIdList.get(rng.nextInt(bookIdList.size()));
            if (!toRemove.contains(id)) {
                toModify.add(id);
            }
        }

        // Remove all country variants of each removed book ID
        books.removeIf(b -> toRemove.contains(b.id.value));

        // Modify selected books by replacing their metadata (new name suffix + rotated genre, same chapters)
        for (Book book : books) {
            if (toModify.contains(book.id.value)) {
                book.bookMetadata = new BookMetadata(
                        "Book_" + String.format("%05d", book.id.value) + "_v" + cycleNum,
                        Genre.values()[(book.id.value + cycleNum) % Genre.values().length],
                        book.bookMetadata.chapters
                );
            }
        }

        // Add new books with fresh IDs (one per country variant)
        for (int i = 0; i < addsPerCycle; i++) {
            addBooksForId(nextBookId++);
        }
    }

    public List<Book> getBooks() {
        return books;
    }
}
