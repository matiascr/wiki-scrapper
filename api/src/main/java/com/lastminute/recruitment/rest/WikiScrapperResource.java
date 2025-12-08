package com.lastminute.recruitment.rest;

import com.lastminute.recruitment.domain.WikiScrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RequestMapping("/wiki")
@RestController
public class WikiScrapperResource {

    @Autowired
    WikiScrapper wikiScrapper;

    @PostMapping("/scrap")
    public void scrapWikipedia(@RequestBody String link) {
        if (link == null || link.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing method body");
        }

        // We expect the link to be surrounded by ""
        if (link.startsWith("\"") && link.endsWith("\"")) {
            link = link.substring(1, link.length() - 1);
        }

        if (wikiScrapper.read(link).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found: " + link);
        }
    }
}
