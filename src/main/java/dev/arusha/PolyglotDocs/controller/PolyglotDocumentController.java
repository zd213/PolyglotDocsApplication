package dev.arusha.PolyglotDocs.controller;

import dev.arusha.PolyglotDocs.model.PolyglotDocument;
import dev.arusha.PolyglotDocs.service.PolyglotDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class PolyglotDocumentController {

    private static final Logger logger = LoggerFactory.getLogger(PolyglotDocumentController.class);

    @Autowired
    private PolyglotDocumentService elasticSearchQuery;

    @PostMapping("/document")
    public ResponseEntity<Object> createOrUpdateDocument(@RequestBody PolyglotDocument document) throws IOException {
        String response = elasticSearchQuery.createOrUpdateDocument(document);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/document")
    public ResponseEntity<Object> getDocumentById(@RequestParam(required = false) String documentId) throws IOException {
        if (documentId == null) {
            List<PolyglotDocument> polyglotDocuments = elasticSearchQuery.searchAllDocuments();
            return new ResponseEntity<>(polyglotDocuments, HttpStatus.OK);
        }
        PolyglotDocument document = elasticSearchQuery.getDocumentById(documentId);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @DeleteMapping("/document")
    public ResponseEntity<Object> deleteDocumentById(@RequestParam String documentId) throws IOException {
        String response = elasticSearchQuery.deleteDocumentById(documentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/document/search")
    public ResponseEntity<Object> searchDocuments(@RequestParam(value = "q", required = false) String query, Model model) throws IOException {
        List<PolyglotDocument> polyglotDocuments = Collections.emptyList();
        if (query != null && !query.isEmpty()) {
            polyglotDocuments = elasticSearchQuery.searchTextInDocuments(query);
        }
        return new ResponseEntity<>(polyglotDocuments, HttpStatus.OK);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        logger.error("IOException occurred: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request. Please try again later.");
    }
}
