package com.lastminute.recruitment.client;

import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import java.util.Optional;


public class HtmlWikiClient implements WikiClient {
    // this should have a method that returns a Html representation of a Wikipedia page
    @Override
    public Optional<WikiPage> get(String link) throws WikiPageNotFound {
        return Optional.empty();
    }
}
