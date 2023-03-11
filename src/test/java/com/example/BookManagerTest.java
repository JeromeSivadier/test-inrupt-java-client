package com.example;

import com.example.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BookManagerTest {
    private static final String CLIENT_ID = "075dd092-c1f9-4718-b8d8-9492086c6dc5";
    private static final String CLIENT_SECRET = "bc22c70c-7bc4-48f5-9b5f-1c9ce321516b";

    private InruptAuthenticator authenticator;

    @BeforeEach
    public void setup() {
        authenticator = new InruptAuthenticator(CLIENT_ID, CLIENT_SECRET);
    }

    @Test
    public void testAuthenticator() {
        var session = authenticator.getSession();
        var maybePrincipal = session.getPrincipal();
        assertTrue(maybePrincipal.isPresent());
        assertEquals(URI.create("https://id.inrupt.com/utester3"), maybePrincipal.get());
    }

    @Test
    public void testBooksManager() {
        var booksManager = new BookManager(authenticator);
        assertTrue(booksManager.getBooksFolderURI().getPath().endsWith("booksLibrary"));
    }

    @Test
    public void testCRUDBooks() {
        var booksManager = new BookManager(authenticator);
        assertEquals(0, booksManager.listBooks().size());

        Book book = booksManager.createBook("Test",
                null,
                List.of("Some content"),
                300,
                Instant.now()
        );
        assertEquals("Test", book.getName());
        assertNull(book.getAuthor());
        assertEquals(List.of("Some content"), book.getText().collect(Collectors.toList()));
        assertEquals(300, book.getNumberOfPages());
        assertNotNull(book.getDatePublished());

        List<Book> books = booksManager.listBooks();
        assertEquals(1, books.size());
        assertEquals(book, books.get(0));

        book = booksManager.editBook(book.getIdentifier(),
                "Test",
                URI.create("http://test"),
                List.of("Modified"),
                book.getNumberOfPages(),
                book.getDatePublished()
        );
        assertEquals("Test", book.getName());
        assertEquals(URI.create("http://test"), book.getAuthor());
        assertEquals(List.of("Modified"), book.getText().collect(Collectors.toList()));
        assertEquals(300, book.getNumberOfPages());
        assertNotNull(book.getDatePublished());

        books = booksManager.listBooks();
        assertEquals(1, books.size());
        assertEquals(book, books.get(0));

        booksManager.deleteBook(book.getIdentifier());
        assertEquals(0, booksManager.listBooks().size());
    }
}