package com.lastminute.recruitment.rest;

import com.lastminute.recruitment.domain.WikiScrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/wiki")
@RestController
public class WikiScrapperResource {

    @Autowired
    WikiScrapper wikiScrapper;

    @PostMapping("/scrap")
    public ResponseEntity<String> scrapWikipedia(@RequestBody String link) {
        if (link == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Page not found");
        }

        // We expect the link to be surrounded by ""
        if (link.startsWith("\"") && link.endsWith("\"")) {
            link = link.substring(1, link.length() - 1);
        }

        if (wikiScrapper.read(link).isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Page not found: " + link);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Found link: " + link);
    }
}
