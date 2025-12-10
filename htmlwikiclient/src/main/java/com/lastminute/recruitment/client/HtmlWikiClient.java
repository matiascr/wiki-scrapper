package com.lastminute.recruitment.client;

import com.lastminute.recruitment.client.error.WikiPageHtmlParseException;
import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;

import java.util.Optional;


/**
 * Fetches pages in HTML and parses them accordingly to return a WikiPage.
 */
public class HtmlWikiClient implements WikiClient {
    @Override
    public Optional<WikiPage> get(String link) throws WikiPageNotFound, WikiPageHtmlParseException {
        return Optional.empty();
    }
}
