package hollow;

import com.github.javafaker.Faker;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.explorer.ui.HollowExplorerUIServer;
import com.netflix.hollow.history.ui.HollowHistoryUIServer;
import hollow.model.Art;
import hollow.model.Artist;
import hollow.model.Book;
import hollow.model.BookId;
import hollow.model.BookImages;
import hollow.model.BookMetadata;
import hollow.model.Chapter;
import hollow.model.ChapterId;
import hollow.model.ChapterInfo;
import hollow.model.Country;
import hollow.model.Genre;
import hollow.model.Scene;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Produces a fake book catalog dataset with some entropy in delta chain.
 */
public class FakeDataGenerator {
    // tunable parameters of the generated fake dataset
    public static final String BLOB_PATH = "/tmp/fakehollowdata";
    public static final int NUM_RUN_CYCLES = 100;   // how many producer cycles to run, typically maps to the no. of states in the produced delta chain
    public static final int NUM_BOOKS_IN_CATALOG = 10000;   // total no. of records at the top level
    public static final int MAX_ADDS_PER_CYCLE = 200;   // entropy
    public static final int MAX_REMOVES_PER_CYCLE = 100;   // entropy
    public static final int MAX_MODIFICATIONS_PER_CYCLE = 1000;   // entropy


    // other variables controlling dataset size and cardinality
    public static final int MAX_CHARACTERS_IN_A_CHAPTER = 100;
    public static final int NUM_ARTISTS = 1000;


    private final static Faker faker = new Faker();
    private static final List<String> COUNTRIES = Arrays.asList("US", "CA", "MX", "BE", "BZ", "BJ", "BM", "BT", "BR", "BG", "BI", "CV", "KH", "CM", "CL", "CN", "CW", "CY", "CZ", "DK", "DJ", "DM", "EC", "EG", "SV", "ER", "EE", "SZ", "ET", "FI", "FR", "GA", "GE", "DE", "GH", "GI", "GR", "GL", "GD", "GP", "GT", "GG", "GN", "GY", "HT", "HK", "HU", "IS", "IN", "ID", "IRQ", "IE", "Man", "IL", "IT", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "PK", "PW", "TK", "TO", "TN", "TR", "TM", "TV", "UG", "UA");

    private static List<Book> books = new ArrayList<>();
    private static int maxBookId;
    private static List<Artist> artists = new ArrayList<>(100000);

    
    public static void main(String[] args) throws Exception {
        System.out.println("Writing fake data blobs to local path: " + BLOB_PATH);

        populateArtists();
        populateCatalog(1, NUM_BOOKS_IN_CATALOG);

        HollowProducer p = new HollowProducer.Builder<>()
                .withPublisher(new HollowFilesystemPublisher(Paths.get(BLOB_PATH)))
                .withNumStatesBetweenSnapshots(NUM_RUN_CYCLES/3)
                .build();

        p.runCycle(state -> {
            for (Book book : books) {
                state.add(book);
            }
        });

        HollowConsumer c = HollowConsumer.withBlobRetriever(new HollowFilesystemBlobRetriever(Paths.get(BLOB_PATH))).build();
        c.triggerRefresh();

        HollowExplorerUIServer explorerUIServer = new HollowExplorerUIServer(c, 7001).start();
        System.out.println("Explorer started at http://localhost:7001");

        new HollowHistoryUIServer(c, 7002).start();
        System.out.println("History server started listening at http://localhost:7002");

        for (int i = 0; i < NUM_RUN_CYCLES-1; i ++) {
            Set<Integer> bookIdsToModifyThisCycle = randomBookIds(new Random().nextInt(MAX_MODIFICATIONS_PER_CYCLE));
            int booksToAddThisCycle = new Random().nextInt(MAX_ADDS_PER_CYCLE);
            Set<Integer> bookIdsToRemoveThisCycle = randomBookIds(new Random().nextInt(MAX_REMOVES_PER_CYCLE));
            bookIdsToRemoveThisCycle.removeAll(bookIdsToModifyThisCycle);

            long v = p.runCycle(state -> {
                populateCatalog(maxBookId, booksToAddThisCycle);
                for (Book book : books) {
                    if (bookIdsToRemoveThisCycle.contains(book.id.value)) {
                        continue; // drop book
                    }
                    if (bookIdsToModifyThisCycle.contains(book.id.value)) {
                        modifyBook(book);
                    }
                    state.add(book);    // add remaining as is
                }
            });

            c.triggerRefreshTo(v);
        }

        explorerUIServer.join();
    }

    private static Genre randomGenre() {
        int randomGenreOrdinal = new Random().nextInt(Genre.values().length);
        Genre randomGenre = Genre.values()[randomGenreOrdinal];
        return randomGenre;
    }

    private static String randomSceneDescription() {
        return faker.funnyName().name() + ", a " + faker.job().title().toString() + ", teams up with a pet " + faker.animal().name() + " to rescue the planet from " + faker.pokemon().name();
    }

    private static Set<String> randomSetOfPeople() {
        int numPeople = new Random().nextInt(5);
        Set<String> people = new HashSet<>(5);
        for (int i = 0; i < numPeople; i ++) {
            people.add(faker.name().name());
        }
        return people;
    }

    private static Set<String> randomSetOfCountries() {
        int numCountries = new Random().nextInt(COUNTRIES.size());
        if (numCountries == 0) {
            numCountries = 1;
        }
        Set<String> countries = new HashSet<>(numCountries);
        for (int i = 0; i < numCountries; i ++) {
            countries.add(COUNTRIES.get(new Random().nextInt(COUNTRIES.size())));
        }
        return countries;
    }

    private static byte[] randomChapterContent() {
        return faker.lorem().characters(10, MAX_CHARACTERS_IN_A_CHAPTER).getBytes();
    }

    private static Artist randomArtist() {
        return artists.get(new Random().nextInt(artists.size()));
    }

    private static void populateArtists() {
        Set<Artist> setOfArtists = new HashSet<>(NUM_ARTISTS);
        for (int i = 0; i < NUM_ARTISTS; i ++) {
            Artist artist = new Artist(faker.artist().name().toString(), faker.country().capital().toString());
            setOfArtists.add(artist);
        }
        artists = new ArrayList<>(setOfArtists);
    }

    private static void populateCatalog(int bookStartId, int numBooksToAdd) {
        for (int bookIdIter = bookStartId; bookIdIter < numBooksToAdd; bookIdIter ++) {
            final int bookId = bookIdIter;
            for (String country : randomSetOfCountries()) {
                int pages = new Random().nextInt(1000);

                Book book = new Book(
                        new BookId(bookId),
                        new Country(country),
                        new BookImages(new HashMap<String, List<Art>>() {{
                            put("small", Arrays.asList(new Art(bookId + country, randomArtist(), System.currentTimeMillis(), new Random().nextInt(10000))));
                            put("medium", Arrays.asList(new Art(bookId + country, randomArtist(), System.currentTimeMillis(), new Random().nextInt(10000))));
                            put("large", Arrays.asList(new Art(bookId + country, randomArtist(), System.currentTimeMillis(), new Random().nextInt(10000))));
                        }}
                                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))),
                        new BookMetadata(faker.book().title(), randomGenre(), Arrays.asList(
                                new Chapter(new ChapterId(UUID.randomUUID().toString()), new ChapterInfo(new BookId(bookId), pages, randomChapterContent()), Arrays.asList(new Scene(randomSceneDescription(), new Random().nextInt(100000), randomSetOfPeople()))),
                                new Chapter(new ChapterId(UUID.randomUUID().toString()), new ChapterInfo(new BookId(bookId), pages, randomChapterContent()), Arrays.asList(new Scene(randomSceneDescription(), new Random().nextInt(100000), randomSetOfPeople())))
                        ).stream().collect(Collectors.toList()))
                );
                books.add(book);
                maxBookId ++;
            }
        }
    }

    private static void modifyBook(Book book) {
        boolean maybeRemoveArt = new Random().nextBoolean();
        if (maybeRemoveArt) {
            Optional<Map.Entry<String, List<Art>>> art =  book.images.art.entrySet().stream().findFirst();
            if (art.isPresent()) {
                book.images.art.remove(art.get());
            }
        }

        boolean maybeAddArt = new Random().nextBoolean();
        if (maybeAddArt && book.images.art.size() < 3) {
            Set<String> sizes = book.images.art.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
            String size;
            if (!sizes.contains("small")) {
                size = "small";
            } else if (!sizes.contains("medium")) {
                size = "medium";
            } else {
                size = "large";
            }
            book.images.art.put(size, Arrays.asList(new Art(book.id.toString() + book.country, randomArtist(), System.currentTimeMillis(), new Random().nextInt(10000))));
        }

        boolean maybeRemoveBookChapter = new Random().nextBoolean();
        if (maybeRemoveBookChapter) {
            if (book.bookMetadata.chapters.size() > 0) {
                book.bookMetadata.chapters.remove(0);
            }
        }

        boolean maybeAddBookChapter = new Random().nextBoolean();
        if (maybeAddBookChapter) {
            book.bookMetadata.chapters.add(
                    new Chapter(new ChapterId(UUID.randomUUID().toString()), new ChapterInfo(new BookId(book.id.value),
                            new Random().nextInt(1000), randomChapterContent()),
                            Arrays.asList(new Scene(randomSceneDescription(), new Random().nextInt(100000), randomSetOfPeople()))));
        }

    }

    private static Set<Integer> randomBookIds(int maxHowMany) {
        if (maxHowMany == 0) {
            maxHowMany = 1;
        }
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < maxHowMany; i ++) {
            int bookId = new Random().nextInt(maxBookId);
            if (bookId == 0) {
                bookId = 1;
            }
            result.add(bookId);
        }
        return result;
    }

}