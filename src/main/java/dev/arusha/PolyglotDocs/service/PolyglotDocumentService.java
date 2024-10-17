package dev.arusha.PolyglotDocs.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import dev.arusha.PolyglotDocs.model.PolyglotDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PolyglotDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(PolyglotDocumentService.class);

    private final String indexName;
    private final ElasticsearchClient elasticsearchClient;

    public PolyglotDocumentService(@Value("${elasticsearch.index}") String indexName,
                                   ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
        ensureIndexExists(indexName);
    }

    private void ensureIndexExists(String indexName) {
        try {
            ExistsRequest existsRequest = new ExistsRequest.Builder().index(indexName).build();
            BooleanResponse existsResponse = elasticsearchClient.indices().exists(existsRequest);

            if (!existsResponse.value()) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                        .index(indexName)
                        .mappings(m -> m
                                .properties("field1", p -> p.text(t -> t))
                                .properties("field2", p -> p.keyword(k -> k))
                        )
                        .build();

                CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(createIndexRequest);
                if (createIndexResponse.acknowledged()) {
                    logger.info("Index {} created successfully.", indexName);
                } else {
                    logger.info("Index creation for {} failed.", indexName);
                }
            } else {
                logger.info("Index {} already exists.", indexName);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String createOrUpdateDocument(PolyglotDocument document) throws IOException {

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(document.getId())
                .document(document)
        );
        if (response.result().name().equals("Created")) {
            return "Document has been successfully created.";
        } else if (response.result().name().equals("Updated")) {
            return "Document has been successfully updated.";
        }
        return "Error while performing the operation.";
    }

    public PolyglotDocument getDocumentById(String documentId) throws IOException {
        PolyglotDocument document = null;
        GetResponse<PolyglotDocument> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(documentId),
                PolyglotDocument.class
        );

        if (response.found()) {
            document = response.source();
            logger.info("Document body {}", document.getBody());
        } else {
            logger.info("Document {} not found", documentId);
        }
        return document;
    }

    public String deleteDocumentById(String documentId) throws IOException {

        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(documentId));
        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return "Document with id " + deleteResponse.id() + " has been deleted.";
        }
        logger.info("Document {} not found", documentId);
        return "Document with id " + deleteResponse.id() + " does not exist.";
    }

    public List<PolyglotDocument> searchAllDocuments() throws IOException {

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
        SearchResponse<PolyglotDocument> searchResponse = elasticsearchClient.search(searchRequest, PolyglotDocument.class);

        List<Hit<PolyglotDocument>> hits = searchResponse.hits().hits();
        List<PolyglotDocument> documentList = new ArrayList<>();
        hits.forEach(object -> {
            documentList.add(object.source());
        });
        return documentList;
    }

    public List<PolyglotDocument> searchTextInDocuments(String searchText) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
                .index(indexName)
                .query(q -> q
                        .multiMatch(m -> m
                                .query(searchText)
                                .fields("body.*")
                        )
                )
        );
        SearchResponse<PolyglotDocument> searchResponse = elasticsearchClient.search(request, PolyglotDocument.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}

