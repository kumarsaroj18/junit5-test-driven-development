package bookstoread;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BookShelfSpec {

    @Test
    public void shelfEmptyWhenNoBookAdded() throws Exception {
        BookShelf bookShelf = new BookShelf();
        List<String> books = bookShelf.books();
        Assertions.assertTrue(books.isEmpty(), () -> "Bookshelf should be empty");
    }
}
