package com.lastminute.recruitment.rest;

import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
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
        System.out.println("Hello Scrap -> " + link);
        if (Optional.ofNullable(link).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Missing method body");
        }

        try {
            wikiScrapper.read(link);
        } catch (WikiPageNotFound wikiPageNotFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, wikiPageNotFound.getLocalizedMessage());
        }
    }
}
