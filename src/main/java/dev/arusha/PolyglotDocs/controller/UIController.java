package dev.arusha.PolyglotDocs.controller;

import dev.arusha.PolyglotDocs.model.PolyglotDocument;
import dev.arusha.PolyglotDocs.service.PolyglotDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class UIController {

    @Autowired
    private PolyglotDocumentService elasticSearchQuery;

    @GetMapping("/")
    public String viewHomePage(Model model) throws IOException {
        model.addAttribute("listDocuments", elasticSearchQuery.searchAllDocuments());
        return "index";
    }

    @PostMapping("/saveDocument")
    public String saveDocument(@ModelAttribute("document") PolyglotDocument document) throws IOException {
        elasticSearchQuery.createOrUpdateDocument(document);
        return "redirect:/";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") String id, Model model) throws IOException {
        PolyglotDocument polyglotDocument = elasticSearchQuery.getDocumentById(id);
        model.addAttribute("document", polyglotDocument);
        return "updateDocument";
    }

    @GetMapping("/showNewDocumentForm")
    public String showNewEmployeeForm(Model model) {
        PolyglotDocument polyglotDocument = new PolyglotDocument();
        model.addAttribute("document", polyglotDocument);
        return "newDocument";
    }

    @GetMapping("/showSearchForm")
    public String showSearchForm() {
        return "searchDocument";
    }

    @GetMapping("/deleteDocument/{id}")
    public String deleteDocument(@PathVariable(value = "id") String id) throws IOException {
        this.elasticSearchQuery.deleteDocumentById(id);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchDocuments(@RequestParam(value = "q", required = false) String query, Model model) throws IOException {
        if (query != null && !query.isEmpty()) {
            List<PolyglotDocument> documents = elasticSearchQuery.searchTextInDocuments(query);
            model.addAttribute("documents", documents);
        }
        return "searchDocument";
    }
}
