package com.example.model;

import com.inrupt.rdf.wrapping.commons.RDFFactory;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;

import java.util.Objects;

/**
 * Note: this class should not be needed but some methods were missing from the @{@link com.inrupt.rdf.wrapping.commons.TermMappings} class
 */
public class AdditionalTermMappings {
    private static final String VALUE_REQUIRED = "Value must not be null";
    private static final String GRAPH_REQUIRED = "Graph must not be null";
    private static final RDF FACTORY = RDFFactory.getInstance();

    public static Literal asTypedLiteral(final Integer value, final Graph graph) {
        Objects.requireNonNull(value, VALUE_REQUIRED);
        Objects.requireNonNull(graph, GRAPH_REQUIRED);

        return FACTORY.createLiteral(value.toString(), FACTORY.createIRI("https://www.w3.org/TR/xmlschema-2/#integer"));
    }
}
