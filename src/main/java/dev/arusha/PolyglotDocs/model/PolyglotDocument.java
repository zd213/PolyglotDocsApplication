package dev.arusha.PolyglotDocs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Map;

@Document(indexName = "polyglot-docs")
public class PolyglotDocument {

    @Id
    private String id;

    private Map<String, String> body; // Key is the language, value is the text

    public String getId() {
        return id;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }
}

