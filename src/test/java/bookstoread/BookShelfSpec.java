package bookstoread;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("<== Bookshelf ==>")
public class BookShelfSpec {
    private BookShelf bookShelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;

    private Book cleanCode;

    private BookShelfSpec(TestInfo testInfo) {
        System.out.println("Working on test " + testInfo.getDisplayName());
    }

    @BeforeEach
    void init() {
        this.bookShelf = new BookShelf();
        effectiveJava = new Book("Effective Java", "Joshua Bloch",
                LocalDate.of(2008, Month.MAY, 8));
        codeComplete = new Book("Code Complete", "Steve McConnel",
                LocalDate.of(2004, Month.JUNE, 9));
        mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks",
                LocalDate.of(1975, Month.JANUARY, 1));

        cleanCode = new Book("Clean Code", "Robert C. Martin",
                LocalDate.of(2008, Month.JANUARY, 1));
    }

    @Nested
    @DisplayName("is empty")
    class IsEmpty {
        @Test
        @DisplayName("Is empty when no book is added to it")
        public void shelfEmptyWhenNoBookAdded(TestInfo testInfo) throws Exception {
            System.out.println("Working on test case " + testInfo.getDisplayName());
            List<Book> books = bookShelf.books();
            assertTrue(books.isEmpty(), () -> "Bookshelf should be empty");
        }

        @Test
        @DisplayName("when add is called with no books")
        void emptyBookShelfWhenAddIsCalledWithoutAnyBook() {
            bookShelf.add();
            List<Book> books = bookShelf.books();
            assertTrue(books.isEmpty(), () -> "Bookshelf should be empty");
        }
    }

    @Nested
    @DisplayName("When books are added")
    class WhenBooksAreAdded {
        @Test
        @DisplayName("Books shelf should contain two books when two books are added")
        void bookShelfContainsTwoBooksWhenTwoBooksAdded() {
            bookShelf.add(effectiveJava, codeComplete);
            List<Book> books = bookShelf.books();
            assertEquals(2, books.size(), () -> "There should be only two books on the shelf");
        }

        @Test
        @DisplayName("Returned book should be in the form of immutable list")
        void bookListReturnedFromShelfShouldBeImmutableToClient() {
            bookShelf.add(effectiveJava, codeComplete);
            List<Book> books = bookShelf.books();
            try {
                books.add(mythicalManMonth);
                fail(() -> "Should not be able to add books to the bookshelf");
            } catch (Exception e) {
                assertTrue(e instanceof UnsupportedOperationException, () -> "Should throw unsupported exception");
            }
        }
    }

    @Nested
    @DisplayName("Books are arranged")
    class BooksArrangement {
        @Test
        @DisplayName("by book title")
        void bookShelfShouldBeArrangedByBookTitle() {
            bookShelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> sortedBooks = bookShelf.arrange();

            assertEquals(asList(codeComplete, effectiveJava, mythicalManMonth), sortedBooks,
                    () -> "Books in a bookshelf should be\n" +
                            "arranged lexicographically by book title");
        }

        @Test
        @DisplayName("by provided criteria")
        void bookshelfArrangedByUserProvidedCriteria() {
            bookShelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = bookShelf.arrange(Comparator.<Book>naturalOrder().reversed());
//        assertEquals(asList(mythicalManMonth, effectiveJava, codeComplete), books,
//                () -> "Books in a bookshelf are arranged in descending order of book title");
            Assertions.assertThat(books).isSortedAccordingTo(Comparator.<Book>naturalOrder().reversed());
        }

    }

    @Nested
    @DisplayName("Books are grouped")
    class BooksGrouped {
        @Test
        @DisplayName(" by publication year")
        void groupBooksInBookShelfByPublicationYear() {
            bookShelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);

            Map<Year, List<Book>> booksByPublicationYear = bookShelf.groupByPublicationYear();
            Assertions.assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2008))
                    .containsValue(Arrays.asList(effectiveJava, cleanCode));

            Assertions.assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2004))
                    .containsValue(Collections.singletonList(codeComplete));

            Assertions.assertThat(booksByPublicationYear)
                    .containsKey(Year.of(1975))
                    .containsValue(Collections.singletonList(mythicalManMonth));
        }

        @Test
        @DisplayName("by user provided criteria (group by author name)")
        void groupBooksByUserProvidedCriteria() {
            bookShelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<String, List<Book>> booksByAuthor = bookShelf.groupBy(Book::getAuthor);
            Assertions.assertThat(booksByAuthor)
                    .containsKey("Joshua Bloch")
                    .containsValues(Collections.singletonList(effectiveJava));
            Assertions.assertThat(booksByAuthor)
                    .containsKey("Steve McConnel")
                    .containsValues(Collections.singletonList(codeComplete));
            Assertions.assertThat(booksByAuthor)
                    .containsKey("Frederick Phillips Brooks")
                    .containsValues(Collections.singletonList(mythicalManMonth));
            Assertions.assertThat(booksByAuthor)
                    .containsKey("Robert C. Martin")
                    .containsValues(Collections.singletonList(cleanCode));
        }
    }


    @Test
    @DisplayName("Should check for even numbers")
    void shouldCheckForEvenNumbers() {
        int number = new Random(10).nextInt();
        assertTrue(() -> number % 2 == 0, number + " is not an even number");

        BiFunction<Integer, Integer, Boolean> divisible = (x, y) -> x % y == 0;
        Function<Integer, Boolean> multipleOfTwo = (x) -> divisible.apply(x, 2);

        assertTrue(() -> multipleOfTwo.apply(number), () -> " 2 is not a factor of " + number);

        List<Integer> numbers = asList(1, 1, 1, 1, 2);
        assertTrue(() -> numbers.stream().distinct().anyMatch(BookShelfSpec::isEven), "Did you find an even number in the list");
    }

    static boolean isEven(int number) {
        return number % 2 == 0;
    }

}
