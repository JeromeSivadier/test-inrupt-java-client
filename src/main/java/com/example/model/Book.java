package com.example.model;

import com.inrupt.client.Resource;
import com.inrupt.rdf.wrapping.commons.TermMappings;
import com.inrupt.rdf.wrapping.commons.ValueMappings;
import com.inrupt.rdf.wrapping.commons.WrapperIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Book extends Resource {
    private final Node subject;

    public Book(URI identifier, Dataset dataset) {
        super(identifier, dataset);
        this.subject = new Node(rdf.createIRI(identifier.toString()), getGraph());
    }

    public String getName() {
        return subject.getName();
    }

    public Book setName(String name) {
        subject.setName(name);
        return this;
    }

    public URI getAuthor() {
        return subject.getAuthor();
    }

    public Book setAuthor(URI author) {
        subject.setAuthor(author);
        return this;
    }

    public Instant getDatePublished() {
        return subject.getDatePublished();
    }

    public Book setDatePublished(Instant datePublished) {
        subject.setDatePublished(datePublished);
        return this;
    }

    public Integer getNumberOfPages() {
        return subject.getNumberOfPages();
    }

    public Book setNumberOfPages(Integer numberOfPages) {
        subject.setNumberOfPages(numberOfPages);
        return this;
    }

    public Stream<String> getText() {
        String text = subject.getText();
        if (text != null) {
            return text.lines();
        }
        return Stream.of();
    }

    public Book setText(List<String> content) {
        subject.setText(String.join("\n", content));
        return this;
    }

    @Override
    public String toString() {
        return String.format("{ name: %s, author: %s, numberOfPages: %s, datePublished: %s, text: %s}",
                getName(), getAuthor(), getNumberOfPages(), getDatePublished(), getText().collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(subject, book.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject);
    }

    static IRI NAME = rdf.createIRI("https://schema.org/name");
    static IRI AUTHOR = rdf.createIRI("https://schema.org/author");

    static IRI DATE_PUBLISHED = rdf.createIRI("https://schema.org/datePublished");
    static IRI TEXT = rdf.createIRI("https://schema.org/text");
    static IRI NUMBER_OF_PAGES = rdf.createIRI("https://schema.org/numberOfPages");


    static class Node extends WrapperIRI {
        protected Node(RDFTerm original, Graph graph) {
            super(original, graph);
        }

        String getName() {
            return anyOrNull(NAME, ValueMappings::literalAsString);
        }

        void setName(String name) {
            overwriteNullable(NAME, name, TermMappings::asStringLiteral);
        }

        URI getAuthor() {
            return anyOrNull(AUTHOR, ValueMappings::iriAsUri);
        }

        void setAuthor(URI author) {
            overwriteNullable(AUTHOR, author, TermMappings::asIri);
        }

        Instant getDatePublished() {
            return anyOrNull(DATE_PUBLISHED, ValueMappings::literalAsInstant);
        }

        void setDatePublished(Instant datePublished) {
            overwriteNullable(DATE_PUBLISHED, datePublished, TermMappings::asTypedLiteral);
        }

        String getText() {
            return anyOrNull(TEXT, ValueMappings::literalAsString);
        }

        void setText(String text) {
            overwriteNullable(TEXT, text, TermMappings::asStringLiteral);
        }

        Integer getNumberOfPages() {
            return anyOrNull(NUMBER_OF_PAGES, ValueMappings::literalAsIntegerOrNull);
        }

        void setNumberOfPages(Integer value) {
            overwriteNullable(NUMBER_OF_PAGES, value, AdditionalTermMappings::asTypedLiteral);
        }

    }
}
