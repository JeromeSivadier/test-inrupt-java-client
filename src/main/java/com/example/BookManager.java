package com.example;

import com.example.model.Book;
import com.inrupt.client.Resource;
import com.inrupt.client.auth.Session;
import com.inrupt.client.solid.Metadata;
import com.inrupt.client.solid.SolidContainer;
import com.inrupt.client.solid.SolidSyncClient;
import com.inrupt.client.util.URIBuilder;
import com.inrupt.client.webid.WebIdProfile;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookManager {
    private static final String FOLDER_NAME = "booksLibrary";

    private final Session session;
    private final SolidSyncClient client;
    private final URI booksFolderURI;

    public BookManager(InruptAuthenticator authenticator) {
        this.session = authenticator.getSession();
        this.client = SolidSyncClient.getClient().session(session);
        this.booksFolderURI = getOrCreateBooksFolder();
    }

    public Book createBook(String name, URI author, List<String> text, Integer numberOfPages, Instant datePublished) {
        var bookURI = URIBuilder.newBuilder(booksFolderURI).path(name).build();
        try (var book = new Book(bookURI, null)
                .setName(name)
                .setAuthor(author)
                .setText(text)
                .setNumberOfPages(numberOfPages)
                .setDatePublished(datePublished)) {
            client.create(book);
            return book;
        }
    }

    public Book editBook(URI bookURI, String name, URI author, List<String> text, Integer numberOfPages, Instant datePublished) {
        try (var book = client.read(bookURI, Book.class)) {
            book.setName(name);
            book.setAuthor(author);
            book.setText(text);
            book.setNumberOfPages(numberOfPages);
            book.setDatePublished(datePublished);
            client.update(book);
            return book;
        }
    }

    public void deleteBook(URI bookURI) {
        try (var book = client.read(bookURI, Book.class)) {
            client.delete(book);
        }
    }

    public List<Book> listBooks() {
        try (var container = client.read(booksFolderURI, SolidContainer.class)) {
            // FIXME: this hack should not be needed but container.getContainedResources() was returning an empty stream
            return container.stream()
                    .filter(q -> q.getPredicate().getIRIString().contains("contains"))
                    .map(Quad::getObject)
                    .map(IRI.class::cast)
                    .map(IRI::getIRIString)
                    .map(URI::create)
                    .map(uri -> client.read(uri, Book.class))
                    .collect(Collectors.toList());
        }
    }

    private URI getOrCreateBooksFolder() {
        try (var solidContainer = session.getPrincipal().stream()
                .map(webId -> client.read(webId, WebIdProfile.class))
                .map(profile -> profile.getStorage().stream().findFirst().orElseThrow())
                .map(storage -> client.read(storage, SolidContainer.class))
                .findFirst().orElseThrow()) {
            Optional<URI> maybeBooksFolder = solidContainer.getContainedResources()
                    .map(Resource::getIdentifier)
                    .filter(uri -> uri.getPath().endsWith(FOLDER_NAME))
                    .findFirst();
            if (maybeBooksFolder.isPresent()) {
                return maybeBooksFolder.get();
            }

            // FIXME: this does not create a "Container" resource, but always a standard one (with or without metadata)
            var newResourceURI = URIBuilder.newBuilder(solidContainer.getIdentifier()).path(FOLDER_NAME).build();
            try (var newFolder = new SolidContainer(newResourceURI, null, Metadata.newBuilder()
                    .type(URI.create("http://www.w3.org/ns/ldp#BasicContainer"))
                    .build())) {
                client.create(newFolder);
            }
            return newResourceURI;
        }
    }

    public URI getBooksFolderURI() {
        return booksFolderURI;
    }
}
